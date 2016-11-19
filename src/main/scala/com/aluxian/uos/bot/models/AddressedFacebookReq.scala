package com.aluxian.uos.bot.models

case class AddressedFacebookMessage(recipient: Recipient, message: FacebookMessage) {
  case class AddFbMsg(recipient: Recipient, message: TextMessage)
  def toAlt = AddFbMsg(recipient, message match {
    case message1: TextMessage => message1.copy(quick_replies = None)
    case _ => TextMessage("", None)
  })
}
case class AddressedFacebookAction(recipient: Recipient, sender_action: String)
