package com.aluxian.susucatbot.domains

import com.aluxian.susucatbot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.susucatbot.models.Phrase._
import com.aluxian.susucatbot.models._
import com.aluxian.susucatbot.{ResponseGenerator, Story}
import com.twitter.util.Future

object AroundCampusResponses extends ResponseGenerator {
  def default(): String = {
    "Meow, please be more specific or ask me something else, I'm a cat, not a robot! :o"
  }
  def eat(): String = {
    "If you were a cat :v, you could eat wherever you want! But, as you are probably human :(, you can eat on campus " +
      "in Piazza, The Stags, The Bridge, or get a nice sandwich from The Shop!"
  }
  def bathroom(): String = {
    "If you are in Mountbatten building, and I know you are since I am a cat :D, its on 4th floor, just outside of the common room on the right!"
  }
  def beer(): String = {
    "Check out The Stag's, its the cheapest pub EVER! :o I hang out there all the time with my tomcats! :$"
  }
}

object AroundCampusActions {

}

object AroundCampusStory extends Story {
  val phrases = List(
    "Where can I eat on campus?" \\ EntityDef.Intent.GetEat,
    "Where is the nearest bathroom?" \\ EntityDef.Intent.GetBathroom,
    "Where I can get the cheapest pint?" \\ EntityDef.Intent.GetBeer
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.userAsked(Intent("get_eat")) ||
    past.userAsked(Intent("get_bathroom")) ||
    past.userAsked(Intent("get_beer"))
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = {
    val intentOpt = past.currentMessage.intent
    if (intentOpt.isDefined) {
      val intent = intentOpt.get
      if (intent equals Intent("get_eat")) {
        return Future(List(Respond(TextBotResponse(AroundCampusResponses.eat()))))
      } else if (intent equals Intent("get_bathroom")) {
        return Future(List(Respond(TextBotResponse(AroundCampusResponses.bathroom()))))
      } else if (intent equals Intent("get_beer")) {
        return Future(List(Respond(TextBotResponse(AroundCampusResponses.beer()))))
      } 
    }

    // send messages
    Future(List[BotAction](
      Respond(TextBotResponse(AroundCampusResponses.default()))
    ))
  }
}
