package com.aluxian.susucatbot.domains

import com.aluxian.susucatbot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.susucatbot.models.Phrase._
import com.aluxian.susucatbot.models._
import com.aluxian.susucatbot.{ResponseGenerator, Story}
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

  def favThing(): String = {
    random(
      "Sleeeeping \uD83D\uDE34",
      "Eating fish \uD83D\uDC20",
      "Getting stroked by students \uD83E\uDD17"
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
    "meow for me" \\ EntityDef.Intent.Meow,
    "What's your favourite thing to do?" \\ EntityDef.Intent.FavThing
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.currentMessage.hasIntent("greeting") ||
      past.currentMessage.hasIntent("how_are_you") ||
      past.currentMessage.hasIntent("thank you") ||
      past.currentMessage.hasIntent("insult") ||
      past.currentMessage.hasIntent("meow") ||
      past.currentMessage.hasIntent("fav_thing")
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
      else if (past.currentMessage.hasIntent("fav_thing"))
        List(Respond(TextBotResponse(GeneralResponses.favThing())))
      else
        List()
    )
  }
}
