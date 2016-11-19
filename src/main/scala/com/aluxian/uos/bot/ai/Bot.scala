package com.aluxian.uos.bot.ai

import com.aluxian.uos.bot.Story
import com.aluxian.uos.bot.apis.WitAiApi
import com.aluxian.uos.bot.domains.{AroundCampusStory, FunStory, GeneralStory, WeatherStory}
import com.aluxian.uos.bot.models.{BotAction, Entity, PastMessage, _}
import com.aluxian.uos.bot.mongo.MongoMessage
import com.twitter.util.Future

case class WitEntity(name: String, value: String, confidence: Float)

class Bot(val data: MongoData) {
  def process(text: String, postback: String, entities: List[WitEntity]): Future[List[BotAction]] = {

    println("processing bot")
    val botPast = new BotPast(messagesToPastMessages(data.messages))
    val thoughtsMap = data.user.memory
      .map(mt => (mt.key, Thought(mt.value, mt.expiresAt)))
      .toMap
    val botMemory = new BotMemory(thoughtsMap) // TODO mutable
    val botInterface = new BotInterface(botMemory)
    val storyPickOpt = pickStory(botPast, botInterface)

    println("storyPickOptDefined? " + storyPickOpt.isDefined)
    if (storyPickOpt.isDefined) {
      val story = storyPickOpt.get
      return story.run(botPast, botInterface)
    }

    Future(List())
  }

  case class Result(story: Story, matched: Boolean)

  private def messagesToPastMessages(messages: List[MongoMessage]): List[PastMessage] = {
    messages.map {
      msg =>
        PastMessage(
          MessageType.fromString(msg.`type`),
          CorrespondentType.fromString(msg.senderType),
          CorrespondentType.fromString(msg.receiverType),
          msg.text,
          msg.entities.map(me => Entity(me.key, me.value)),
          msg.createdAt
        )
    }
  }

  private def pickStory(botPast: BotPast, botInterface: BotInterface): Option[Story] = {
    List(
      (WeatherStory, WeatherStory.analyse(botPast, botInterface)),
      (FunStory, FunStory.analyse(botPast, botInterface)),
      (GeneralStory, GeneralStory.analyse(botPast, botInterface)),
      (AroundCampusStory, AroundCampusStory.analyse(botPast, botInterface))
    )
      .find(_._2)
      .map(_._1)
  }

  private def convertEntities(entities: List[WitEntity]): List[Entity] = {
    entities.map(we => Entity(we.name, we.value))
  }
}

class WitBot(override val data: MongoData) extends Bot(data) {
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
