package com.aluxian.uos.bot.mongo

import java.sql.Date

case class MongoUser(facebookId: String,
                     firstName: String,
                     lastName: String,
                     pictureUrl: String,
                     memory: List[MongoThought])

case class MongoThought(key: String,
                        value: String,
                        expiresAt: Date)

case class MongoMessage(`type`: String,
                        senderId: String,
                        receiverId: String,
                        senderType: String,
                        receiverType: String,
                        createdAt: Date)
