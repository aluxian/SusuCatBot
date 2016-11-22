package com.aluxian.susucatbot.ai

import com.aluxian.susucatbot.models.{Intent, PastMessage}
import com.aluxian.susucatbot.models._

/**
  * @param messages all the previous messages in this conversation, sorted in descending order; the first message is
  *                 the one that is currently being processed (sent by the user)
  */
class BotPast(messages: List[PastMessage]) {
  val LOOK_BACK_AMOUNT = 1
  val currentMessage = messages.head

  def userAsked(intent: Intent) = existsByAsked(_.sentByUser)(intent)
  def botAsked(intent: Intent) = {
    val x = existsByAsked2(_.sentByBot)(intent)
    println("\n\nbot asked " + intent + "? " + x + "   msgs=" + messages)
    x
  }

  private def existsByAsked(predicate: (PastMessage) => Boolean)(intent: Intent): Boolean = {

    messages
      .filter(predicate)
      .take(LOOK_BACK_AMOUNT)
      .flatMap(_.entities)
      .contains(intent.toEntity)
  }

  private def existsByAsked2(predicate: (PastMessage) => Boolean)(intent: Intent): Boolean = {
    val filtered = messages.filter(predicate)
      println("filtered="+filtered)
      val taken = filtered.take(LOOK_BACK_AMOUNT)
        println("taken="+taken)
      val mapped = taken.flatMap(_.entities)
        println("mapped" +mapped)

      val contains = mapped.contains(intent.toEntity)
    println("contains=")
    contains
  }
}
