package com.aluxian.uos.bot.apis

import com.aluxian.uos.bot.config.Config
import com.aluxian.uos.bot.models.{AddressedFacebookAction, AddressedFacebookMessage, FacebookUser}
import com.twitter.util.{Future, FuturePool}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scalaj.http.Http

object FacebookApi {

  def getUser(id: String): Future[FacebookUser] = FuturePool.unboundedPool {
    val fields = List("first_name", "last_name", "profile_pic", "gender", "locale", "timezone")
    val userJson = Http(Config.facebookApiUrl + id)
      .param("access_token", Config.facebookAccessToken)
      .param("fields", fields.mkString(","))
      .header("Content-Type", "application/json")
      .header("User-Agent", Config.userAgent)
      .asString
      .throwError
      .body
    println("got user json from fb:" + userJson)
    println(parse(userJson))
    val dec = decode[FacebookUser](userJson)
    if (dec.isLeft) {
      dec.leftMap(_.printStackTrace())
    }
      dec.toOption.get
  }

  def post(body: String): Future[Unit] = FuturePool.unboundedPool {
    println("sending: " + body)
    val r = Http(Config.facebookApiUrl + "me/messages")
      .postData(body)
      .param("access_token", Config.facebookAccessToken)
      .header("Content-Type", "application/json")
      .header("User-Agent", Config.userAgent)
      .asString
    println("sent: " + r.body)
  }

  def postMessage(addressedFacebookAction: AddressedFacebookAction): Future[Unit] =
    post(addressedFacebookAction.asJson.noSpaces)

  def postMessage(addressedFacebookMessage: AddressedFacebookMessage): Future[Unit] =
    post(addressedFacebookMessage.toAlt.asJson.noSpaces)
}
