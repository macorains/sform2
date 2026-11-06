package net.macolabo.sform2.domain.services.Form.sendtestmail

import play.api.libs.json.{Format, Json}

case class FormSendTestMailRequest(
  from_address_id: BigInt,
  to_address: Option[String],
  to_address_id: Option[BigInt],
  subject: String,
  body: String,
  cc_address: Option[String],
  cc_address_id: Option[BigInt],
  bcc_address_id: Option[BigInt],
  replyto_address_id: Option[BigInt]
)

object FormSendTestMailRequest {
  implicit val format: Format[FormSendTestMailRequest] = Json.format[FormSendTestMailRequest]
}
