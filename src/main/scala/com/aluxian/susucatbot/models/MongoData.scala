package com.aluxian.susucatbot.models

import com.aluxian.susucatbot.mongo.{MongoMessage, MongoUser}
import com.aluxian.susucatbot.mongo.MongoMessage

case class MongoData(user: MongoUser, messages: List[MongoMessage])
