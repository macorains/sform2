package net.macolabo.sform2.domain.services.Form.sendtestmail

import play.api.libs.json.{Format, Json}

case class FormSendTestMailResponse(result: Boolean, message: String)

object FormSendTestMailResponse {
  implicit val format: Format[FormSendTestMailResponse] = Json.format[FormSendTestMailResponse]
}
