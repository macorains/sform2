package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Format, Json}

case class SalesforceGetObjectResponse(
                                      name: String,
                                      label: String
                                      )

object SalesforceGetObjectResponse {
  implicit val format: Format[SalesforceGetObjectResponse] = Json.format[SalesforceGetObjectResponse]
}
