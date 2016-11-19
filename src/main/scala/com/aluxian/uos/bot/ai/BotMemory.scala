package com.aluxian.uos.bot.ai

import com.aluxian.uos.bot.models.Thought

class BotMemory(thoughts: Map[String, Thought]) {
  def get(key: String): Option[Any] = {
    thoughts.get(key).map(_.value)
  }
}
