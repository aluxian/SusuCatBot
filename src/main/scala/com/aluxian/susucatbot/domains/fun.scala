package com.aluxian.susucatbot.domains

import com.aluxian.susucatbot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.susucatbot.models.Phrase._
import com.aluxian.susucatbot.models._
import com.aluxian.susucatbot.{ResponseGenerator, Story}
import com.twitter.util.Future

object FunResponses extends ResponseGenerator {
  def mum(): String = {
    s"Awww, so cute, she said she loves you too and you are her favourite child!"
  }
  def youcandoit(): String = {
    s"https://www.youtube.com/watch?v=VZ2HcRl4wSk"
  }
  def cheat(): String = {
    s"Here is a video for you: https://www.youtube.com/watch?v=dQw4w9WgXcQ"
  }
  def pompey(): String = {
    s"Southampton is the best city in whole Hampshire!"
  }
  def solent(): String = {
    s"It’s the best University in E Park Terrace."
  }
  def love(): String = {
    s"https://www.youtube.com/watch?v=HEXWRTEbj1I"
  }
  def turtles(): String = {
    s"https://www.youtube.com/watch?v=CMNry4PE93Y"
  }
  def cutest(): String = {
    s"That's an easy question! Its Aysel! If you are Aysel, go to that coffee, I mean tea, it will be awesome! Catpromise!"
  }
  def sotonrocks(): String = {
    s"University of Southampton is the best universtiy ever! Take that Solent!"
  }
}

object FunActions {

}

object FunStory extends Story {
  val phrases = List(
    "Tell my mum I love her!" \\ EntityDef.Intent.GetMum,
    "Can I do it?" \\ EntityDef.Intent.GetYouCanDoIt,
    "How can I cheat on exams?" \\ EntityDef.Intent.GetCheat,
    "Is Southampton better than Portsmouth?" \\ EntityDef.Intent.GetPompey,
    "What do you think of Solent University?" \\ EntityDef.Intent.GetSolent,
    "What is love?" \\ EntityDef.Intent.GetLove,
    "Do you like turtles?" \\ EntityDef.Intent.GetTurtles,
    "Who's the cutest girl ever?" \\ EntityDef.Intent.GetCutest,
    "Who's the cutest cat?" \\ EntityDef.Intent.GetCutestCat
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.userAsked(Intent("get_mum")) ||
      past.userAsked(Intent("get_youcandoit")) ||
      past.userAsked(Intent("get_cheat")) ||
      past.userAsked(Intent("get_pompey")) ||
      past.userAsked(Intent("get_solent")) ||
      past.userAsked(Intent("get_love")) ||
      past.userAsked(Intent("get_turtles")) ||
      past.userAsked(Intent("get_cutest")) ||
      past.userAsked(Intent("get_cutest_cat"))
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = {
    val intentOpt = past.currentMessage.intent
    if (intentOpt.isDefined) {
      val intent = intentOpt.get
      if (intent equals Intent("get_mum")) {
        return Future(List(Respond(TextBotResponse(FunResponses.mum()))))
      } else if (intent equals Intent("get_youcandoit")) {
        return Future(List(Respond(TextBotResponse(FunResponses.youcandoit()))))
      } else if (intent equals Intent("get_cheat")) {
        return Future(List(Respond(TextBotResponse(FunResponses.cheat()))))
      } else if (intent equals Intent("get_pompey")) {
        return Future(List(Respond(TextBotResponse(FunResponses.pompey()))))
      } else if (intent equals Intent("get_solent")) {
        return Future(List(Respond(TextBotResponse(FunResponses.solent()))))
      } else if (intent equals Intent("get_love")) {
        return Future(List(Respond(TextBotResponse(FunResponses.love()))))
      } else if (intent equals Intent("get_turtles")) {
        return Future(List(Respond(TextBotResponse(FunResponses.turtles()))))
//      } else if (intent equals Intent("get_cutest")) {
//        return Future(List(Respond(TextBotResponse(FunResponses.cutest()))))
//      } else if (intent equals Intent("get_cutest_cat")) {
//        return Future(List(Respond(TextBotResponse("You are the cutest cat, Anastasia <3"))))
      }
    }

    // send messages
    Future(List[BotAction](
      Respond(TextBotResponse(FunResponses.sotonrocks()))
    ))
  }
}
