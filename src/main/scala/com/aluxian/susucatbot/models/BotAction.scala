package com.aluxian.susucatbot.models

import com.aluxian.susucatbot.ai.BotResponse
import org.joda.time.Duration

sealed abstract class BotAction()

final case class Respond(botResponse: BotResponse) extends BotAction()
final case class Remember(key: String, value: String, duration: Duration) extends BotAction()
