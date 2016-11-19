package com.aluxian.uos.bot.models

case class AddressedFacebookMessage(recipient: Recipient, message: FacebookMessage) {

  case class AddFbMsg(recipient: Recipient, message: TextMessage)
  case class AddFbMsg2(recipient: Recipient, message: ImageMessage)

  def toAlt = AddFbMsg(recipient, message match {
    case message1: TextMessage => message1.copy(quick_replies = None)
    case _ => TextMessage("", None)
  })

  def toAlt2 = AddFbMsg2(recipient, message match {
    case message1: ImageMessage => message1.copy()
    case _ => ImageMessage("")
  })

}
case class AddressedFacebookAction(recipient: Recipient, sender_action: String)
