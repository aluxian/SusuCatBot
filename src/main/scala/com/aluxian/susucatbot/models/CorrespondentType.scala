package com.aluxian.susucatbot.models

sealed trait CorrespondentType

object CorrespondentType {
  def fromString(str: String): CorrespondentType = {
    str match {
      case "user" => User
      case "bot" => Bot
    }
  }
  case object Bot extends CorrespondentType
  case object User extends CorrespondentType
}
