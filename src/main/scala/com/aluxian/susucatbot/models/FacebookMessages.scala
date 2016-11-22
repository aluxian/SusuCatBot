package com.aluxian.susucatbot.models

sealed trait BaseFacebookRequest
sealed trait FacebookMessage {
  def toFacebookRequest: BaseFacebookRequest
}

final case class TextMessage(text: String, quick_replies: Option[List[TextMessage.QuickReply]]) extends FacebookMessage {
  require(text.length <= 320, "text must not be longer than 320 chars")
  override def toFacebookRequest = TextMessage.FacebookRequest(text, quick_replies)
}

final case class ImageMessage(url: String) extends FacebookMessage {
  override def toFacebookRequest = ImageMessage.FacebookRequest(
    ImageMessage.Attachment("image", ImageMessage.AttachmentPayload(url))
  )
}

final case class CarouselMessage(cards: List[CarouselMessage.Card]) extends FacebookMessage {
  require(cards.forall(_.buttons.length <= 3), "cards must not have more than 3 buttons")
  require(cards.forall(_.title.length <= 80), "card title must not be longer than 80 chars")
  require(cards.forall(_.subtitle.length <= 80), "card subtitle must not be longer than 80 chars")
  override def toFacebookRequest = CarouselMessage.FacebookRequest(
    CarouselMessage.Attachment("template",
      CarouselMessage.AttachmentPayload("generic", cards))
  )
}

object TextMessage {
  case class QuickReply(title: String, payload: String)
  case class FacebookRequest(text: String, quick_replies: Option[List[QuickReply]]) extends BaseFacebookRequest
}

object ImageMessage {
  case class Attachment(`type`: String, payload: AttachmentPayload)
  case class AttachmentPayload(url: String)
  case class FacebookRequest(attachment: Attachment) extends BaseFacebookRequest
}

object CarouselMessage {
  sealed abstract class Button(`type`: String)
  case class LinkButton(url: String) extends Button("web_url")
  case class PostbackButton(payload: String) extends Button("postback")

  case class Card(title: String, subtitle: String, item_url: String, image_url: String, buttons: List[Button])
  case class Attachment(`type`: String, payload: AttachmentPayload)
  case class AttachmentPayload(template_type: String, elements: List[Card])
  case class FacebookRequest(attachment: Attachment) extends BaseFacebookRequest
}
