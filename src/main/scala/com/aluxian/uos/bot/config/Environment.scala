package com.aluxian.uos.bot.config

sealed abstract class Environment(name: String, isProduction: Boolean, isDevelopment: Boolean, isTest: Boolean) {
  override def toString: String = name
}

object Environment {
  def apply(name: String): Environment = name.toLowerCase() match {
    case "production" => Production
    case "development" => Development
    case "test" => Test
    case e => sys.error(s"Unknown environment '$e'")
  }
}

case object Production extends Environment("production", true, false, false)
case object Development extends Environment("development", false, true, false)
case object Test extends Environment("test", false, false, true)
