package com.aluxian.uos.bot

import com.aluxian.uos.bot.config.Config
import com.aluxian.uos.bot.controllers.{FacebookCtrl, RootCtrl}
import com.aluxian.uos.bot.utils.FutureBijection._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.param.Stats
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import io.finch.Text
import io.finch.circe._
import reactivemongo.api.collections.bson._
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

import scala.concurrent.Future

object Main extends TwitterServer {

  val endpoints = RootCtrl.root :+:
    FacebookCtrl.webhookVerify :+:
    FacebookCtrl.webhookNewEvent

  val api: Service[Request, Response] = endpoints.toServiceAs[Text.Plain]
  var db: DefaultDB = _

  def main() = {
    val server = Http.server
      .configured(Stats(statsReceiver))
      .serve(Config.listenAddress, api)

    onExit {
      server.close()
    }

    // MongoDB connection setup
    val databaseFuture = for {
      parsedUri <- Future.fromTry(MongoConnection.parseURI(Config.mongoUrl))
      conn = new MongoDriver().connection(parsedUri)
      database <- conn.database(parsedUri.db.get)
    } yield database
    db = Await.result(databaseFuture.asTwitter)

    // Finagle admin panel
    Await.ready(adminHttpServer)
  }

  object collections {
    def users = db.collection("users")
    def messages = db.collection("messages")
  }

}
