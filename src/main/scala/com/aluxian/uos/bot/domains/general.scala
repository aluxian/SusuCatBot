package com.aluxian.uos.bot.domains

import com.aluxian.uos.bot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.uos.bot.models.Phrase._
import com.aluxian.uos.bot.models._
import com.aluxian.uos.bot.{ResponseGenerator, Story}
import com.twitter.util.Future

object GeneralResponses extends ResponseGenerator {
  def hello(): String = {
    random(
      "Hi! \uD83D\uDE3A",
      "Hi, there!",
      "Hey, human!",
      "Meeooow, hello \uD83D\uDE3D"
    )
  }

  def howAreYou(): String = {
    random(
      "Great, thanks!",
      "Lovely, meow \uD83D\uDE38"
    )
  }

  def thankYou(): String = {
    random(
      "^_^",
      "Don't mention it, human"
    )
  }

  def insult(): String = {
    random(
      "Chhhhhhhhhh \uD83D\uDE3E",
      "fuck off \uD83D\uDE3C"
    )
  }

  def meow(): String = {
    random(
      "Meeeeooooooooooow \uD83D\uDE3D"
    )
  }
}

object GeneralActions {

}

object GeneralStory extends Story {
  val phrases = List(
    "hi" \\ EntityDef.Intent.Greeting, // hey hello
    "how are you?" \\ EntityDef.Intent.HowAreYou,
    "thank you" \\ EntityDef.Intent.ThankYou,
    "fuck you" \\ EntityDef.Intent.Insult,
    "meow for me" \\ EntityDef.Intent.Meow
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.currentMessage.hasIntent("greeting") ||
      past.currentMessage.hasIntent("how_are_you") ||
      past.currentMessage.hasIntent("thank you")
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = {
    Future(
      if (past.currentMessage.hasIntent("greeting"))
        List(Respond(TextBotResponse(GeneralResponses.hello())))
      else if (past.currentMessage.hasIntent("how_are_you"))
        List(Respond(TextBotResponse(GeneralResponses.howAreYou())))
      else if (past.currentMessage.hasIntent("thank_you"))
        List(Respond(TextBotResponse(GeneralResponses.thankYou())))
      else if (past.currentMessage.hasIntent("insult"))
        List(Respond(TextBotResponse(GeneralResponses.insult())))
      else if (past.currentMessage.hasIntent("meow"))
        List(Respond(TextBotResponse(GeneralResponses.meow())))
      else
        List()
    )
  }
}
