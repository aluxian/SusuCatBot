package com.aluxian.uos.bot.models

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

case class Thought(value: Any, expiresAt: DateTime) {
  def isExpired: Boolean = DateTime.now > expiresAt
}
