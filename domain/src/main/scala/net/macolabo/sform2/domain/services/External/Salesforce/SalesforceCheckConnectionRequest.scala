package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Format, Json}

case class SalesforceCheckConnectionRequest(
  username: String,
  password: String,
  client_id: String,
  client_secret: String,
  domain: String,
  api_version: String
)

object SalesforceCheckConnectionRequest {
  implicit val format: Format[SalesforceCheckConnectionRequest] = Json.format[SalesforceCheckConnectionRequest]
}
