package com.aluxian.susucatbot.controllers

import java.util.Date

import com.aluxian.susucatbot.Main
import com.aluxian.susucatbot.ai.{WitBot, WitEntity}
import com.aluxian.susucatbot.apis.{FacebookApi, WitAiApi}
import com.aluxian.susucatbot.channels.facebook.{EventsHandler, FacebookReply}
import com.aluxian.susucatbot.config.Config
import com.aluxian.susucatbot.models.{MongoData, WebhookEvent}
import com.aluxian.susucatbot.mongo.{MongoUser, _}
import com.aluxian.susucatbot.utils.FutureBijection._
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{Endpoint, _}
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success}

object FacebookCtrl {
  /**
    * Used by Facebook to verify the app.
    */
  def webhookVerify: Endpoint[String] = get("facebook" :: param("hub.mode") :: param("hub.verify_token") ::
    param("hub.challenge")) { (hubMode: String, hubVerifyToken: String, hubChallenge: String) =>
    if (hubMode equals "subscribe") {
      if (Config.facebookVerifyToken equals hubVerifyToken) {
        Ok(hubChallenge)
      } else {
        BadRequest(new Exception("unrecognized hub.verify_token"))
      }
    } else {
      BadRequest(new Exception("unknown hub.mode"))
    }
  }

  /**
    * Used by Facebook to deliver events.
    */
  def webhookNewEvent: Endpoint[String] = post("facebook" :: header("X-Hub-Signature") ::
    body.as[WebhookEvent]) { (xHubSignature: String, body: WebhookEvent) =>

    try {
      body.entry
        .flatMap(_.messaging)
        .groupBy(_.sender)
        .foreach { case (sender, messageEvents) =>

          implicit def mongoEntityWriter: BSONDocumentWriter[MongoEntity] = Macros.writer[MongoEntity]
          implicit def mongoThoughtWriter: BSONDocumentWriter[MongoThought] = Macros.writer[MongoThought]
          implicit def mongoMessageWriter: BSONDocumentWriter[MongoMessage] = Macros.writer[MongoMessage]
          implicit def mongoUserWriter: BSONDocumentWriter[MongoUser] = Macros.writer[MongoUser]

          implicit def mongoEntityReader: BSONDocumentReader[MongoEntity] = Macros.reader[MongoEntity]
          implicit def mongoThoughtReader: BSONDocumentReader[MongoThought] = Macros.reader[MongoThought]
          implicit def mongoMessageReader: BSONDocumentReader[MongoMessage] = Macros.reader[MongoMessage]
          implicit def mongoUserReader: BSONDocumentReader[MongoUser] = Macros.reader[MongoUser]

          // insert the current messages in db
          val mongoMessages = messageEvents
            .filter(_.message.isDefined)
            .map { event =>
              val message = event.message.get

              val messageText = message.text
              val messagePostback = message.quick_reply.map(_.payload)

              if (messageText.isDefined) {
                def convertEntities(entities: Map[String, List[WitAiApi.Entry]]): List[MongoEntity] = {
                  entities.toList.map {
                    case (key, entries) =>
                      MongoEntity(key, entries.head.value.getOrElse(""))
                  }
                }

                val witAiResponse = Await.result(WitAiApi.parse(messageText.get))
                val mongoEntities = convertEntities(witAiResponse.entities)

                MongoMessage("text", messageText, None, messagePostback,
                  sender.id, "0bot0", "user", "bot", new Date(), mongoEntities)
              } else {
                MongoMessage("text", messageText, None, messagePostback,
                  sender.id, "0bot0", "user", "bot", new Date(), List()) // TODO can't add entities here (not parsed yet)
              }
            }

          val mongoMessagesDocs = mongoMessages
            .toStream
            .map(mongoMessageWriter.write)

          Main.collections.messages.bulkInsert(mongoMessagesDocs, ordered = true).onComplete {
            case Success(writeResult) =>

              val userQuery = BSONDocument("facebookId" -> sender.id)
              def getNewDoc = FacebookApi.getUser(sender.id).map {
                fbu => MongoUser(sender.id, fbu.first_name, fbu.last_name, fbu.profile_pic, List())
              }.asScala

              findOneOrCreate(Main.collections.users)(userQuery, getNewDoc).onComplete {
                case Success(mongoUser) =>
                  val messagesQuery = BSONDocument("$or" -> BSONArray(
                    BSONDocument("senderId" -> sender.id),
                    BSONDocument("receiverId" -> sender.id)
                  ))

                  Main.collections.messages
                    .find(messagesQuery)
                    .sort(BSONDocument("_id" -> -1))
                    .cursor[MongoMessage]()
                    .collect[List](1000)
                    .onComplete {
                      case Success(messages) =>
                        val mongoData = MongoData(mongoUser, messages)
                        val witBot = new WitBot(mongoData)
                        val reply = new FacebookReply(sender.id)
                        val handler = new EventsHandler(sender, witBot, reply)
                        messageEvents.foreach(handler.handle)
                      case Failure(ex) =>
                        println("can't query messages:")
                        ex.printStackTrace()
                    }

                case Failure(ex) =>
                  println("can't query user:")
                  ex.printStackTrace()
              }

            case Failure(ex) =>
              println("can't insert message:")
              ex.printStackTrace()
          }
        }
    } catch {
      case ex: Throwable => ex.printStackTrace()
    }

    Ok("ok")
  }
}
