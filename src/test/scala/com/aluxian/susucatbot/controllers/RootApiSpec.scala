package com.aluxian.susucatbot.controllers

import com.twitter.finagle.http.Status
import io.finch._
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}

class RootApiSpec extends FlatSpec with Matchers with Checkers {

  behavior of "root endpoint"

  it should "return 'ok'" in {
    val input = Input.get("/")
    val res = RootCtrl.root(input)
    res.output.map(_.status) === Some(Status.Ok)
    res.value.isDefined === true
    res.value.get shouldBe "ok"
  }

}
