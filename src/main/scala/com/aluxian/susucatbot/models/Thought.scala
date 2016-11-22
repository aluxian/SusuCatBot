package com.aluxian.susucatbot.models

import java.util.Date

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

case class Thought(value: String, expiresAt: Date) {
  def isExpired: Boolean = DateTime.now > new DateTime(expiresAt)
}
