package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class SalesforceLoginResponse(
  access_token: String,
  instance_url: String,
  id: String,
  token_type: String,
  issued_at: String,
  signature: String
)

object SalesforceLoginResponse {
  implicit val format: Format[SalesforceLoginResponse] = Json.format[SalesforceLoginResponse]
}
