package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Json, Writes}

case class SalesforceGetObjectResponse(
                                      name: String,
                                      label: String
                                      )

trait SalesforceGetObjectResponseJson {
  implicit val SalesforceGetObjectResponseWrites: Writes[SalesforceGetObjectResponse] = (salesforceGetObjectResponse:SalesforceGetObjectResponse) => Json.obj(
    "name" -> salesforceGetObjectResponse.name,
    "label" -> salesforceGetObjectResponse.label
  )
}
