package com.aluxian.susucatbot.ai

import com.aluxian.susucatbot.models.Thought

class BotMemory(thoughts: Map[String, Thought]) {
  def get(key: String): Option[String] = {
    thoughts.get(key).map(_.value)
  }
}
