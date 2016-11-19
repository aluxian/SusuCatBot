package com.aluxian.uos.bot.models

sealed trait CorrespondentType

object CorrespondentType {
  case object Bot extends CorrespondentType
  case object User extends CorrespondentType
}
