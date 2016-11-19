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
    Future(Location("Soton UK"))
  }

  /**
    * Use the Dark Sky API to find the forecast for the given location.
    *
    * @param loc the location of the forecast
    * @return a future with the forecast
    */
  def getForecast(loc: Location): Future[Forecast] = {
    Future(Forecast("sunny", "12.3"))
  }
}

object WeatherStory extends Story {
  val phrases = List(
    "What's the weather?" \\ EntityDef.Intent.GetWeather,
    "What's the weather in Paris?" \\ EntityDef.Intent.GetWeather \\ EntityDef.WitLocation("location", "Paris"),
    "In Paris" \\ EntityDef.WitLocation("location", "Paris")
  )

  def analyse(past: BotPast): Boolean = {
    past.userAsked(Intent("get_weather")) ||
      (past.currentMessage.hasEntity("location") && past.botAsked(Intent("get_weather")))
  }

  def run(past: BotPast, bot: BotInterface): Future[List[BotAction]] = FuturePool.unboundedPool {
    // get location from the current message
    if (past.currentMessage.hasEntity("location")) {
      val locationQuery = past.currentMessage.getEntity("location").value
      return for {
        location <- WeatherActions.getLocation(locationQuery)
        forecast <- WeatherActions.getForecast(location)
      } yield List(
        Remember("location", location, 2.hours),
        Remember("forecast", forecast, 30.minutes),
        Respond(TextBotResponse(WeatherResponses.forecast(location, forecast)))
      )
    }

//    // get location from memory
//    val locationMemoryOpt = bot.memory.get("location")
//    if (locationMemoryOpt.isDefined) {
//      return for {
//        location <- locationMemoryOpt.map(_.asInstanceOf[Location])
//        forecast <- WeatherActions.getForecast(location)
//      } yield List(
//        Remember("location", location, 2.hours), // reinforce the memory
//        Remember("forecast", forecast, 30.minutes),
//        Respond(TextBotResponse(WeatherResponses.forecast(location, forecast)))
//      )
//    }

    // ask for the location
    List[BotAction](
      Respond(TextBotResponse(WeatherResponses.whereLocation()))
    )
  }
}
