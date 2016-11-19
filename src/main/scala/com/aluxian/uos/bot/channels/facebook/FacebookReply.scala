package com.aluxian.uos.bot.channels.facebook

import com.aluxian.uos.bot.apis.FacebookApi
import com.aluxian.uos.bot.models.{AddressedFacebookAction, AddressedFacebookMessage, FacebookMessage, Recipient}
import com.twitter.util.Future

sealed abstract class FacebookAction(val name: String)
case object TypingOn extends FacebookAction("typing_on")
case object TypingOff extends FacebookAction("typing_off")
case object MarkSeen extends FacebookAction("mark_seen")

class FacebookReply(recipientId: String) {
  def messages(facebookMessages: FacebookMessage*): Future[Unit] = {
    val results = facebookMessages.map {
      fbMsg =>
        FacebookApi.postMessage(AddressedFacebookMessage(
          Recipient(recipientId),
          fbMsg
        ))
    }
    Future.collect(results).map(_ => Unit)
  }

  def actions(actions: FacebookAction*): Future[Unit] = {
    val results = actions.map {
      action =>
        FacebookApi.postMessage(AddressedFacebookAction(
          Recipient(recipientId),
          action.name
        ))
    }
    Future.collect(results).map(_ => Unit)
  }
}
