package com.aluxian.uos.bot.ai

import com.aluxian.uos.bot.Story
import com.aluxian.uos.bot.apis.WitAiApi
import com.aluxian.uos.bot.domains.WeatherStory
import com.aluxian.uos.bot.models.{BotAction, Entity, PastMessage, _}
import com.twitter.util.Future
import org.joda.time.DateTime

case class WitEntity(name: String, value: String, confidence: Float)

class Bot {
  def process(text: String, postback: String, entities: List[WitEntity]): Future[List[BotAction]] = {
    val currentMessagePastMessage = PastMessage(MessageType.Text, CorrespondentType.User, CorrespondentType.Bot,
      Some(text), convertEntities(entities), DateTime.now)
    val botPast = new BotPast(List() :+ currentMessagePastMessage)
    val storyTypeOpt = pickStory(botPast)
    if (storyTypeOpt.isDefined) {
      val storyType = storyTypeOpt.get
      val botMemory = new BotMemory(Map()) // TODO mutable
      val botInterface = new BotInterface(botMemory)
      storyType.run(botPast, botInterface)
    }

    Future(List())
  }

  case class Result(story: Story, matched: Boolean)

  private def pickStory(botPast: BotPast): Option[Story] = {
    List(
      (WeatherStory, WeatherStory.analyse(botPast))
    )
      .find(_._2)
      .map(_._1)
  }

  private def convertEntities(entities: List[WitEntity]): List[Entity] = {
    entities.map(we => Entity(we.name, we.value))
  }
}

class WitBot extends Bot {
  def process(text: String, postback: String): Future[List[BotAction]] = {
    if (text.nonEmpty) {
      for {
        witAiResponse <- WitAiApi.parse(text)
        botResponses <- super.process(witAiResponse._text, postback, convertEntities(witAiResponse.entities))
      } yield botResponses
    } else {
      super.process(text, postback, List())
    }
  }

  private def convertEntities(entities: Map[String, List[WitAiApi.Entry]]): List[WitEntity] = {
    entities.toList.map {
      case (key, entries) => WitEntity(key, entries.head.value.getOrElse(""), entries.head.confidence)
    }
  }
}
