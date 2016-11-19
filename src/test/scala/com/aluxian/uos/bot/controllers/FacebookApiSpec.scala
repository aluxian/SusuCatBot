package com.aluxian.uos.bot.controllers

import com.aluxian.uos.bot.config.Config
import com.aluxian.uos.bot.controllers.fixtures._
import com.aluxian.uos.bot.models._
import com.twitter.finagle.http.Status
import io.finch._
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}

object fixtures {
  val sampleEvent = WebhookEvent("page", List(Entry(
    "1045932258858446",
    1479482564684L,
    List(MessagingEvent(
      Sender("1271264896226433"),
      Recipient("1045932258858446"),
      1479482564542L,
      Some(Message("mid.1479482564542:a2b7734d76", 7792L, Some("hey"), None, None)),
      None
    ))
  )))

  val sampleEventJson =
    """{
      |    "object": "page",
      |    "entry": [
      |        {
      |            "id": "1045932258858446",
      |            "time": 1479482564684,
      |            "messaging": [
      |                {
      |                    "sender": {
      |                        "id": "1271264896226433"
      |                    },
      |                    "recipient": {
      |                        "id": "1045932258858446"
      |                    },
      |                    "timestamp": 1479482564542,
      |                    "message": {
      |                        "mid": "mid.1479482564542:a2b7734d76",
      |                        "seq": 7792,
      |                        "text": "hey"
      |                    }
      |                }
      |            ]
      |        }
      |    ]
      |}"""

  val sampleEventSignature = "sha1=607dfe7747d975ee115003b83d9017f4a9fdfc93"
}

class FacebookApiSpec extends FlatSpec with Matchers with Checkers {

  behavior of "webhookVerify endpoint"

  it should "error for invalid hub.verify_token parameter" in {
    val hubChallenge = "123testChallenge"
    val input = Input.get("/facebook",
      "hub.mode" -> "subscribe",
      "hub.verify_token" -> "invalidToken",
      "hub.challenge" -> hubChallenge)
    val res = FacebookCtrl.webhookVerify(input)
    res.output.map(_.status) shouldBe Some(Status.BadRequest)
  }

  it should "return the challenge parameter as the response body" in {
    val hubChallenge = "123testChallenge"
    val input = Input.get("/facebook",
      "hub.mode" -> "subscribe",
      "hub.verify_token" -> Config.facebookVerifyToken,
      "hub.challenge" -> hubChallenge)
    val res = FacebookCtrl.webhookVerify(input)
    res.output.map(_.status) shouldBe Some(Status.Ok)
    res.value.isDefined shouldBe true
    res.value.get shouldBe hubChallenge
  }

  behavior of "webhookNewEvent endpoint"

  it should "error if the X-Hub-Signature header is missing" in {
    val input = Input.post("/facebook").withBody[Text.Plain](sampleEventJson)
    val res = FacebookCtrl.webhookNewEvent(input)
    an[io.finch.Error] should be thrownBy res.value
  }

  ignore should "error if the X-Hub-Signature header is invalid" in {
    val input = Input.post("/facebook")
      .withHeaders("X-Hub-Signature" -> "sha1=123")
      .withBody[Text.Plain](sampleEventJson)
    val res = FacebookCtrl.webhookNewEvent(input)
    res.output.map(_.status) shouldBe Some(Status.BadRequest)
  }

  ignore should "work with a valid X-Hub-Signature header" in {
    val input = Input.post("/facebook")
      .withHeaders("X-Hub-Signature" -> sampleEventSignature)
      .withBody[Text.Plain](sampleEventJson)
    val res = FacebookCtrl.webhookNewEvent(input)
    res.output.map(_.status) shouldBe Some(Status.Ok)
    res.value.isDefined shouldBe true
    res.value.get shouldBe "ok"
  }

}
