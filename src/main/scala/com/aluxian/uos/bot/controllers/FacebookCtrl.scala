package com.aluxian.uos.bot.controllers

import com.aluxian.uos.bot.Main
import com.aluxian.uos.bot.ai.WitBot
import com.aluxian.uos.bot.apis.FacebookApi
import com.aluxian.uos.bot.channels.facebook.{EventsHandler, FacebookReply}
import com.aluxian.uos.bot.config.Config
import com.aluxian.uos.bot.models.{MongoData, WebhookEvent}
import com.aluxian.uos.bot.mongo.{MongoUser, _}
import com.aluxian.uos.bot.utils.FutureBijection._
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{Endpoint, _}
import reactivemongo.bson._

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
    body.entry
      .flatMap(_.messaging)
      .groupBy(_.sender)
      .foreach { case (sender, messageEvents) =>

        implicit def mongoUserWriter: BSONDocumentWriter[MongoUser] = Macros.writer[MongoUser]

        val userQuery = BSONDocument("facebookId" -> sender.id)
        def getNewDoc = FacebookApi.getUser(sender.id).map {
          fbu => MongoUser(sender.id, fbu.first_name, fbu.last_name, fbu.profile_pic, List())
        }.asScala

        findOneOrCreate[MongoUser](Main.collections.users)(userQuery, getNewDoc).onComplete {
          case Success(mongoUser) =>
            val messagesQuery = BSONDocument()
            Main.collections.messages.find()

            val mongoData = MongoData(mongoUser, )
            val witBot = new WitBot()
            val reply = new FacebookReply(sender.id)
            val handler = new EventsHandler(sender, witBot, reply)
            messageEvents.foreach(handler.handle)

          case Failure(ex) => println("can't query user: " + ex)
        }
      }
    Ok("ok")
  }
}
