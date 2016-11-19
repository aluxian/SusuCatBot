package com.aluxian.uos.bot.models

object EntityDef {
  sealed abstract class Type(name: String, example: String)

  // entity definitions
  final case class WitLocation(name: String, example: String) extends Type(name, example)

  object Intent {
    sealed abstract class DefType(name: String) extends EntityDef.Type(name, "")

    // intent definitions
    case object GetWeather extends DefType("get_weather")
  }
}

sealed case class Entity(name: String, value: String)
sealed case class Intent(value: String) {
  def toEntity = Entity("intent", value)
}

object Intent {
  def find(entities: List[Entity]): Option[Intent] = {
    entities.find(_.name equals "intent").map(e => Intent(e.value))
  }

  // intent definitions
//  case object GetWeather extends Intent("get_weather")
}
