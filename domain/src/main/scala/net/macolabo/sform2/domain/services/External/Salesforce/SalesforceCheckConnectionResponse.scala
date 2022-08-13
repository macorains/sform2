package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Json, Writes}

case class SalesforceCheckConnectionResponse(
                                          result: String,
                                          message: String
                                          )

trait SalesforceCheckConnectionResponseJson {
  implicit val SalesforceCheckConnectionResponseWrites: Writes[SalesforceCheckConnectionResponse] = (salesforceCheckConnectionResponse:SalesforceCheckConnectionResponse) => Json.obj(
    "result" -> salesforceCheckConnectionResponse.result,
    "message" -> salesforceCheckConnectionResponse.message
  )
}
