package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath}

case class SalesforceLoginResponse(
  access_token: String,
  instance_url: String,
  id: String,
  token_type: String,
  issued_at: String,
  signature: String
)

trait SalesforceLoginResponseJson {
  implicit val salesforceLoginResponseFormat: Format[SalesforceLoginResponse] = (
    (JsPath \ "access_token").format[String] ~
      (JsPath \ "instance_url").format[String] ~
      (JsPath \ "id").format[String] ~
      (JsPath \ "token_type").format[String] ~
      (JsPath \ "issued_at").format[String] ~
      (JsPath \ "signature").format[String]
    ) (SalesforceLoginResponse.apply, unlift(SalesforceLoginResponse.unapply))
}
