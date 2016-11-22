package com.aluxian.susucatbot.mongo

import java.util.Date

case class MongoUser(facebookId: String,
                     firstName: String,
                     lastName: String,
                     pictureUrl: String,
                     memory: List[MongoThought])

case class MongoEntity(key: String,
                       value: String)

case class MongoThought(key: String,
                        value: String,
                        expiresAt: Date)

case class MongoMessage(`type`: String,
                        text: Option[String],
                        imageUrl: Option[String],
                        postback: Option[String],
                        senderId: String,
                        receiverId: String,
                        senderType: String,
                        receiverType: String,
                        createdAt: Date,
                        entities: List[MongoEntity])
