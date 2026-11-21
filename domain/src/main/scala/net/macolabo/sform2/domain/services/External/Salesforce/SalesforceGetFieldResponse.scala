package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Format, Json}

case class SalesforceGetFieldResponse(
                                      name: String,
                                      label: String,
                                      field_type: String,
                                      )

object SalesforceGetFieldResponse {
  implicit val format: Format[SalesforceGetFieldResponse] = Json.format[SalesforceGetFieldResponse]
}
