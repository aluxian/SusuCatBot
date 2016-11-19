package com.aluxian.uos.bot.models

object EntityDef {
  sealed abstract class Type(name: String, example: String)

  // entity definitions
  final case class WitLocation(name: String, example: String) extends Type(name, example)

  object Intent {
    sealed abstract class DefType(name: String) extends EntityDef.Type(name, "")

    // intent definitions
    case object GetWeather extends DefType("get_weather")
    case object GetMum extends DefType("get_mum")
    case object GetYouCanDoIt extends DefType("get_youcandoit")
    case object GetCheat extends DefType("get_cheat")
    case object GetPompey extends DefType("get_pompey")
    case object GetSolent extends DefType("get_solent")
    case object GetLove extends DefType("get_love")
    case object GetTurtles extends DefType("get_turtles")
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
