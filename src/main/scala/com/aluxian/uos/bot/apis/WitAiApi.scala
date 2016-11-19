package com.aluxian.uos.bot.apis

import com.aluxian.uos.bot.config.Config
import com.twitter.util.{Future, FuturePool}
import io.circe.generic.auto._
import io.circe.parser._

import scalaj.http.Http

object WitAiApi {
  //  lazy val client = new Client(new URL(Config.facebookApiUrl))

  case class Entry(value: Option[String], confidence: Float)
  case class WitAiResponse(msg_id: String, _text: String, entities: Map[String, List[Entry]])

  def parse(text: String): Future[WitAiResponse] = {
    require(text.length > 1 && text.length < 256, "text length must be > 0 and < 256")
    FuturePool.unboundedPool {
      val respJson = Http(Config.witAiApiUrl + "message")
        .param("v", Config.witAiApiVersion)
        .param("q", text)
        .header("Authorization", "Bearer " + Config.witAiAccessToken)
        .header("User-Agent", Config.userAgent)
        .asString
        .throwError
        .body
      decode[WitAiResponse](respJson).toOption.get
    }
    //    client
    //      .get("")
    //      .withQueryParams(
    //        "v" -> Config.witAiApiVersion,
    //        "q" -> text)
    //      .withHeaders(
    //        "User-Agent" -> Config.userAgent,
    //        "" -> )
    //      .accept("application/json")
    //      .send[WitAiResponse]()
  }
}
