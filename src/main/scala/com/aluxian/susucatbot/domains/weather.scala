package com.aluxian.susucatbot.domains

import com.aluxian.susucatbot.ai.{BotInterface, BotPast, TextBotResponse}
import com.aluxian.susucatbot.models.Phrase._
import com.aluxian.susucatbot.models._
import com.aluxian.susucatbot.{ResponseGenerator, Story}
import com.github.nscala_time.time.Imports._
import com.twitter.util.{Await, Future}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class Location(name: String)
case class Forecast(summary: String, temp: String)

object WeatherResponses extends ResponseGenerator {
  def forecast(location: Location, forecast: Forecast): String = {
    s"The weather in ${location.name} is ${forecast.summary} and ${forecast.temp}ºC"
  }

  def whereLocation(): String = {
    "Where?"
  }
}

object WeatherActions {
  /**
    * Use the Google Maps API to find the current location.
    *
    * @param query the location to search for
    * @return a future with the location
    */
  def getLocation(query: String): Future[Location] = {
    Future(Location("Southampton UK"))
  }

  /**
    * Use the Dark Sky API to find the forecast for the given location.
    *
    * @param loc the location of the forecast
    * @return a future with the forecast
    */
  def getForecast(loc: Location): Future[Forecast] = {
    Future(Forecast("cloudy", "6"))
  }
}

object WeatherStory extends Story {
  val phrases = List(
    "What's the weather?" \\ EntityDef.Intent.GetWeather,
    "What's the weather in Paris?" \\ EntityDef.Intent.GetWeather \\ EntityDef.WitLocation("location", "Paris"),
    "In Paris" \\ EntityDef.WitLocation("location", "Paris")
  )

  def analyse(past: BotPast, bot: BotInterface): Boolean = {
    past.currentMessage.hasIntent("get_weather") ||
      (past.currentMessage.hasEntity("location") && past.botAsked(Intent("get_weather")))
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = {
    // get location from the current message
    if (past.currentMessage.hasIntent("get_weather") && past.currentMessage.hasEntity("location")) {
      println(">>>> WEATHER STORY match 1")
      return Future {
        val locationQuery = past.currentMessage.getEntity("location").value
        val location = Await.result(WeatherActions.getLocation(locationQuery))
        val forecast = Await.result(WeatherActions.getForecast(location))
        List(
          Remember("location", location.asJson.noSpaces, 2.hours),
          Remember("forecast", forecast.asJson.noSpaces, 30.minutes),
          Respond(TextBotResponse(WeatherResponses.forecast(location, forecast)))
        )
      }
    }

    // get location from the current message, previous intent
    if (past.currentMessage.hasEntity("location") && past.botAsked(Intent("get_weather"))) {
      println(">>>> WEATHER STORY match 2")
      return Future {
        val locationQuery = past.currentMessage.getEntity("location").value
        val location = Await.result(WeatherActions.getLocation(locationQuery))
        val forecast = Await.result(WeatherActions.getForecast(location))
        List(
          Remember("location", location.asJson.noSpaces, 2.hours),
          Remember("forecast", forecast.asJson.noSpaces, 30.minutes),
          Respond(TextBotResponse(WeatherResponses.forecast(location, forecast)))
        )
      }
    }

    // get location from memory
    val locationMemoryOpt = bot.memory.get("location")
    if (past.currentMessage.hasIntent("get_weather") && locationMemoryOpt.isDefined) {
      println(">>>> WEATHER STORY match 3")
      return Future {
        val location = decode[Location](locationMemoryOpt.get).toOption.get
        val forecast = Await.result(WeatherActions.getForecast(decode[Location](locationMemoryOpt.get).toOption.get))
        List(
          Remember("location", location.asJson.noSpaces, 2.hours), // reinforce the memory
          Remember("forecast", forecast.asJson.noSpaces, 30.minutes),
          Respond(TextBotResponse(WeatherResponses.forecast(location, forecast)))
        )
      }
    }

    // ask for the location
    Future(List[BotAction](
      Respond(TextBotResponse(WeatherResponses.whereLocation(), List(Entity("intent", "get_weather"))))
    ))
  }
}
