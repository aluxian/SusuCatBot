package com.aluxian.uos.bot.channels.facebook

import com.aluxian.uos.bot.apis.FacebookApi
import com.aluxian.uos.bot.models.{AddressedFacebookAction, AddressedFacebookMessage, FacebookMessage, Recipient}

sealed abstract class FacebookAction(val name: String)
case object TypingOn extends FacebookAction("typing_on")
case object TypingOff extends FacebookAction("typing_off")

class FacebookReply(recipientId: String) {
  def messages(facebookMessages: FacebookMessage*): Unit = {
    facebookMessages.foreach { fbMsg =>
      FacebookApi.postMessage(AddressedFacebookMessage(
        Recipient(recipientId),
        fbMsg
      ))
        .onFailure(println)
    }
  }
  def actions(actions: FacebookAction*): Unit = {
    actions.foreach { action =>
      FacebookApi.postMessage(AddressedFacebookAction(
        Recipient(recipientId),
        action.name
      ))
        .onFailure(println)
    }
  }
}
