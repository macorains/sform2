package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Json, Writes}

case class SalesforceGetFieldResponse(
                                      name: String,
                                      label: String,
                                      field_type: String,
                                      )

trait SalesforceGetFieldResponseJson {
  implicit val SalesforceGetFieldResponseWrites: Writes[SalesforceGetFieldResponse] = (salesforceGetFieldResponse:SalesforceGetFieldResponse) => Json.obj(
    "name" -> salesforceGetFieldResponse.name,
    "label" -> salesforceGetFieldResponse.label,
    "field_type" -> salesforceGetFieldResponse.field_type
  )
}
