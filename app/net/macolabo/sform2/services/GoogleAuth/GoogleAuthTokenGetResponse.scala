package net.macolabo.sform2.services.GoogleAuth

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath}

case class GoogleAuthTokenGetResponse(
  access_token: String,
  expires_in: Int,
  scope: String,
  token_type: String
)

trait GoogleAuthTokenGetResponseJson {
  implicit val GoogleAuthTokenGetResponseFormat: Format[GoogleAuthTokenGetResponse] = (
    (JsPath \ "access_token").format[String] ~
      (JsPath \ "expires_in").format[Int] ~
      (JsPath \ "scope").format[String] ~
      (JsPath \ "token_type").format[String]
    )(GoogleAuthTokenGetResponse.apply, unlift(GoogleAuthTokenGetResponse.unapply))
}
