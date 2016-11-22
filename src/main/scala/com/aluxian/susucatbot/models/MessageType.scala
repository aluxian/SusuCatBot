package com.aluxian.susucatbot.models

sealed trait MessageType

object MessageType {
  def fromString(str: String): MessageType = {
    str match {
      case "text" => Text
      case "image" => Image
    }
  }
  case object Text extends MessageType
  case object Image extends MessageType
}
