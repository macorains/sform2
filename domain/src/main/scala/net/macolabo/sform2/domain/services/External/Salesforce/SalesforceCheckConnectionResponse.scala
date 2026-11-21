package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Format, Json}

case class SalesforceCheckConnectionResponse(
                                          result: String,
                                          message: String
                                          )

object SalesforceCheckConnectionResponse {
  implicit val format: Format[SalesforceCheckConnectionResponse] = Json.format[SalesforceCheckConnectionResponse]
}
