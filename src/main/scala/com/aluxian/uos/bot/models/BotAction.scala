package com.aluxian.uos.bot.models

import com.aluxian.uos.bot.ai.BotResponse
import org.joda.time.Duration

sealed abstract class BotAction()

final case class Respond(botResponse: BotResponse) extends BotAction()
final case class Remember(key: String, value: String, duration: Duration) extends BotAction()
