package com.aluxian.uos.bot.channels.facebook

import com.aluxian.uos.bot.ai.{BotResponse, ImageBotResponse, TextBotResponse, WitBot}
import com.aluxian.uos.bot.models._
import com.twitter.util.FuturePool
import com.aluxian.uos.bot.ai._
import com.aluxian.uos.bot.models._

class EventsHandler(sender: Sender, bot: WitBot, reply: FacebookReply) {
  def handle(event: MessagingEvent): Unit = {
    var botResponses = getBotResponses(event)
    if (botResponses.isEmpty) {
      botResponses = List(TextBotResponse("idk how to do that"))
    }

    val facebookMessages = botResponsesToFacebookMessages(botResponses)
    FuturePool.unboundedPool {
      println(s"sending TypingOn and ${facebookMessages.length} messages")
      reply.actions(TypingOn)
      reply.messages(facebookMessages: _*)
    }
  }

  def botResponsesToFacebookMessages(botResponses: List[BotResponse]): List[FacebookMessage] = {
    botResponses.map {
      case textResponse: TextBotResponse =>
        TextMessage(textResponse.text,
          Some(textResponse.quickReplies.map(qr => TextMessage.QuickReply(qr.text, qr.payload))))
      case imageResponse: ImageBotResponse =>
        ImageMessage(imageResponse.url)
    }
  }

  def getBotResponses(event: MessagingEvent): List[BotResponse] = {
    if (event.message.isDefined) {
      val message = event.message.get

      val messageText = message.text.getOrElse("")
      val messagePostback = message.quick_reply.map(_.payload).getOrElse("")

      bot.process(messageText, messagePostback)
    }

    if (event.postback.isDefined) {
      val postback = event.postback.get
      bot.process("@" + postback.payload, postback.payload)
    }

    List()
  }
}
