package com.aluxian.uos.bot.channels.facebook

import java.util.Date

import com.aluxian.uos.bot.Main
import com.aluxian.uos.bot.ai.{BotResponse, ImageBotResponse, TextBotResponse, WitBot}
import com.aluxian.uos.bot.models._
import com.aluxian.uos.bot.mongo.{MongoEntity, MongoMessage, MongoThought, MongoUser}
import com.twitter.util.Future
import org.joda.time.DateTime
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext.Implicits.global

class EventsHandler(sender: Sender, bot: WitBot, reply: FacebookReply) {
  def handle(event: MessagingEvent): Unit = {
    reply.actions(MarkSeen)

    implicit def mongoEntityWriter: BSONDocumentWriter[MongoEntity] = Macros.writer[MongoEntity]
    implicit def mongoThoughtWriter: BSONDocumentWriter[MongoThought] = Macros.writer[MongoThought]
    implicit def mongoMessageWriter: BSONDocumentWriter[MongoMessage] = Macros.writer[MongoMessage]
    implicit def mongoUserWriter: BSONDocumentWriter[MongoUser] = Macros.writer[MongoUser]

    implicit def mongoEntityReader: BSONDocumentReader[MongoEntity] = Macros.reader[MongoEntity]
    implicit def mongoThoughtReader: BSONDocumentReader[MongoThought] = Macros.reader[MongoThought]
    implicit def mongoMessageReader: BSONDocumentReader[MongoMessage] = Macros.reader[MongoMessage]
    implicit def mongoUserReader: BSONDocumentReader[MongoUser] = Macros.reader[MongoUser]

    getBotActions(event)
      .onSuccess { botActions =>
        println("BOT ACTIONS=" + botActions)
        var hasSentAMsg = false

        botActions.foreach {
          case resp: Respond =>
            hasSentAMsg = true
            reply.actions(TypingOn)
            //            wait(200)
            reply.messages(botResponseToFacebookMessage(resp.botResponse))
            val mongoMessage = resp.botResponse match {
              case tbr: TextBotResponse =>
                MongoMessage(
                  "text",
                  Some(tbr.text),
                  None,
                  None,
                  sender.id,
                  "0bot0",
                  "bot",
                  "user",
                  new Date(),
                  tbr.entities.map(e => MongoEntity(e.name, e.value))
                )
              case ibr: ImageBotResponse =>
                MongoMessage(
                  "image",
                  None,
                  Some(ibr.url),
                  None,
                  sender.id,
                  "0bot0",
                  "bot",
                  "user",
                  new Date(),
                  ibr.entities.map(e => MongoEntity(e.name, e.value))
                )
            }

            Main.collections.messages.insert[MongoMessage](mongoMessage)
          case rem: Remember =>
            val selector = BSONDocument("facebookId" -> sender.id)
            val doc = BSONDocument(
              "$push" -> BSONDocument(
                "memory" -> BSONDocument(
                  "key" -> rem.key,
                  "value" -> rem.value,
                  "expiresAt" -> new DateTime().plus(rem.duration.getMillis).toDate
                )
              )
            )
            Main.collections.users.update(selector, doc)
        }

        if (!hasSentAMsg) {
          reply.actions(TypingOn)
          //          wait(200)
          reply.messages(botResponseToFacebookMessage(TextBotResponse("Meow, I don't know about that")))
        }
      }
      .onFailure(_.printStackTrace())
  }

  def botResponsesToFacebookMessages(botResponses: List[BotResponse]): List[FacebookMessage] = {
    botResponses.map(botResponseToFacebookMessage)
  }

  def botResponseToFacebookMessage(botResponse: BotResponse): FacebookMessage = {
    botResponse match {
      case textResponse: TextBotResponse =>
        TextMessage(textResponse.text,
          Some(textResponse.quickReplies.map(qr => TextMessage.QuickReply(qr.text, qr.payload))))
      case imageResponse: ImageBotResponse =>
        ImageMessage(imageResponse.url)
    }
  }

  def getBotActions(event: MessagingEvent): Future[List[BotAction]] = {
    if (event.message.isDefined) {
      val message = event.message.get

      val messageText = message.text.getOrElse("")
      val messagePostback = message.quick_reply.map(_.payload).getOrElse("")

      return bot.process(messageText, messagePostback)
    }

    if (event.postback.isDefined) {
      val postback = event.postback.get
      return bot.process("@" + postback.payload, postback.payload)
    }

    Future(List())
  }
}
