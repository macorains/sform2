package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class SalesforceCheckConnectionRequest(
                                         username: String,
                                         password: String,
                                         security_token: String
                                         )

trait SalesforceCheckConnectionRequestJson {
  implicit val  SalesforceCheckConnectionRequestReads: Reads[SalesforceCheckConnectionRequest] = (
    (JsPath \ "username").read[String] ~
      (JsPath \ "password").read[String] ~
      (JsPath \ "security_token").read[String]
    )(SalesforceCheckConnectionRequest.apply _)
}
