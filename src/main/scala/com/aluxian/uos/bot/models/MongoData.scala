package com.aluxian.uos.bot.models

import com.aluxian.uos.bot.mongo.{MongoMessage, MongoUser}
import com.aluxian.uos.bot.mongo.MongoMessage

case class MongoData(user: MongoUser, messages: List[MongoMessage])
