package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class SmtpMailTestRequest(
  smtp_host: String,
  smtp_port: Int,
  smtp_user: String,
  from_address: String,
  smtp_password: String,
  to_address: String,
)

object SmtpMailTestRequest {
  implicit val format: Format[SmtpMailTestRequest] = Json.format[SmtpMailTestRequest]
}
