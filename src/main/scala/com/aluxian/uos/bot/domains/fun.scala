package com.aluxian.uos.bot.domains

import com.aluxian.uos.bot.{ResponseGenerator, Story}
import com.aluxian.uos.bot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.uos.bot.models._
import com.github.nscala_time.time.Imports._
import com.twitter.util.{Future, FuturePool}
import com.aluxian.uos.bot.models.Phrase._
import com.aluxian.uos.bot.models._
import com.aluxian.uos.bot.Story

case class Location(name: String)

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
    " How can I cheat on exams?" \\ EntityDef.Intent.Cheat,
  )

  def analyse(past: BotPast): Boolean = {
    past.userAsked(Intent("get_mum")) || 
    past.userAsked(Intent("get_youcandoit"))  || 
    past.userAsked(Intent("get_cheat"))
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = FuturePool.unboundedPool {
    val intentOpt = past.currentMessage.intent
    if (intentOpt.isDefined) {
      val intent = intentOpt.get // Intent("someting")
      if (intent equals Intent("GetMum")) {
        return Respond(TextBotResponse(FunResponses.mum()))
      } else if (intent equals Intent("GetYouCanDoIt")) {
        return Respond(TextBotResponse(FunResponses.youcandoit()))
      } else if (intent equals Intent("GetCheat")) {
        return Respond(TextBotResponse(FunResponses.cheat()))
      }
    }
    // send messages
    List[BotAction](
      Respond(TextBotResponse(FunResponses.sotonrocks()))
    )
  }
}
