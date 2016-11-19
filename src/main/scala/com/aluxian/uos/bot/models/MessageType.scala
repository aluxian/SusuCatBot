package com.aluxian.uos.bot.models

sealed trait MessageType

object MessageType {
  case object Text extends MessageType
  case object Image extends MessageType
}
