package com.aluxian.susucatbot.models

case class Phrase(text: String, entities: List[EntityDef.Type])

object Phrase {
  implicit class StringDsl(s: String) {
    def \\(entity: EntityDef.Type) = Phrase(s, List(entity))
  }

  implicit class PhraseDsl(phrase: Phrase) {
    def \\(entity: EntityDef.Type) = phrase.copy(entities = phrase.entities :+ entity)
  }
}
