package net.macolabo.sform2.domain.services.External.Salesforce

import play.api.libs.json.{Format, JsPath, Reads}
import play.api.libs.functional.syntax._

case class SalesforceCheckConnectionRequest(
  username: String,
  password: String,
  client_id: String,
  client_secret: String,
  domain: String,
  api_version: String
)

trait SalesforceCheckConnectionRequestJson {
  implicit val  SalesforceCheckConnectionRequestFormat: Format[SalesforceCheckConnectionRequest] = (
    (JsPath \ "username").format[String] ~
      (JsPath \ "password").format[String] ~
      (JsPath \ "client_id").format[String] ~
      (JsPath \ "client_secret").format[String] ~
      (JsPath \ "domain").format[String] ~
      (JsPath \ "api_version").format[String]
    )(SalesforceCheckConnectionRequest.apply, unlift(SalesforceCheckConnectionRequest.unapply))
}
