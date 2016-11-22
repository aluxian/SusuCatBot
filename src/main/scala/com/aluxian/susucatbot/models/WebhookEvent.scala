package com.aluxian.susucatbot.models

case class WebhookEvent(`object`: String, entry: List[Entry])
case class Entry(id: String, time: Long, messaging: List[MessagingEvent])

case class MessagingEvent(sender: Sender,
                          recipient: Recipient,
                          timestamp: Long,
                          message: Option[Message],
                          postback: Option[Postback])

case class Message(mid: String,
                   seq: Long,
                   text: Option[String],
                   attachments: Option[List[Attachment]],
                   quick_reply: Option[QuickReply])

case class Postback(payload: String, referral: Referral)
case class Referral(ref: String, source: String, `type`: String)

case class QuickReply(payload: String)
case class Attachment(`type`: String, payload: AttachmentPayload)
case class AttachmentPayload(url: String)

case class Sender(id: String)
case class Recipient(id: String)
