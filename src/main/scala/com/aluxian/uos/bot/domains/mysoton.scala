package com.aluxian.uos.bot.domains

import com.aluxian.uos.bot.ai.{BotInterface, BotPast, ImageBotResponse, TextBotResponse}
import com.aluxian.uos.bot.models.Phrase._
import com.aluxian.uos.bot.models._
import com.aluxian.uos.bot.{ResponseGenerator, Story}
import com.twitter.util.Future

object MySotonResponses extends ResponseGenerator {

}

object MySotonActions {

}

object MySotonStory extends Story {
  val phrases = List(
    "Do I have any mail?" \\ EntityDef.Intent.GetMail,
    "I hate coursework" \\ EntityDef.Intent.GetCoursework,
    "I'm lost" \\ EntityDef.Intent.GetMap
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.currentMessage.hasIntent("get_mail") ||
      past.currentMessage.hasIntent("get_coursework")||
      past.currentMessage.hasIntent("get_map")
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = {
    Future(
      if (past.currentMessage.hasIntent("get_mail"))
        List(Respond(TextBotResponse("You have 1 parcel to collect")))
      else if (past.currentMessage.hasIntent("get_coursework"))
        List(Respond(TextBotResponse("Great, your ECS coursework is due in exactly 18 days!")))
      else if (past.currentMessage.hasIntent("get_map"))
        List(Respond(TextBotResponse("https://www.ocs.soton.ac.uk/public/site/images/ms4v09/Highfield_JUNE2012.jpg")))
      else
        List()
    )
  }
}
