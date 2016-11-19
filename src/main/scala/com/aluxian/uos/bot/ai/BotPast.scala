package com.aluxian.uos.bot.ai

import com.aluxian.uos.bot.models.{Intent, PastMessage}
import com.aluxian.uos.bot.models._

/**
  * @param messages all the previous messages in this conversation, sorted in descending order; the first message is
  *                 the one that is currently being processed (sent by the user)
  */
class BotPast(messages: List[PastMessage]) {
  val LOOK_BACK_AMOUNT = 3
  val currentMessage = messages.head

  def userAsked(intent: Intent) = existsByAsked(_.sentByUser)(intent)
  def botAsked(intent: Intent) = existsByAsked(_.sentByBot)(intent)

  private def existsByAsked(predicate: (PastMessage) => Boolean)(intent: Intent): Boolean = {
    messages
      .filter(predicate)
      .take(LOOK_BACK_AMOUNT)
      .flatMap(_.entities)
      .contains(intent.toEntity)
  }
}
