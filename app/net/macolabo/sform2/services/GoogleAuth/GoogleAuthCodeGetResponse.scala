package net.macolabo.sform2.services.GoogleAuth

import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._

case class GoogleAuthCodeGetResponse(
                            client_id: String,
                            project_id: String,
                            auth_uri: String,
                            token_uri: String,
                            auth_provider_x509_cert_url: String,
                            client_secret: String,
                            redirect_uris: List[String],
                            javascript_origins: List[String]
                          )

trait GoogleAuthCodeGetResponseJson {
  implicit val GoogleAuthCodeGetResponseWrites: Writes[GoogleAuthCodeGetResponse] = (
    (JsPath \ "client_id").write[String] ~
      (JsPath \ "project_id").write[String] ~
      (JsPath \ "auth_uri").write[String] ~
      (JsPath \ "token_uri").write[String] ~
      (JsPath \ "auth_provider_x509_cert_url").write[String] ~
      (JsPath \ "client_secret").write[String] ~
      (JsPath \ "redirect_uris").write[List[String]] ~
      (JsPath \ "javascript_origins").write[List[String]]
    )(unlift(GoogleAuthCodeGetResponse.unapply))

  implicit val GoogleAuthCodeGetResponseReads: Reads[GoogleAuthCodeGetResponse] = (
    (JsPath \ "client_id").read[String] ~
      (JsPath \ "project_id").read[String] ~
      (JsPath \ "auth_uri").read[String] ~
      (JsPath \ "token_uri").read[String] ~
      (JsPath \ "auth_provider_x509_cert_url").read[String] ~
      (JsPath \ "client_secret").read[String] ~
      (JsPath \ "redirect_uris").read[List[String]] ~
      (JsPath \ "javascript_origins").read[List[String]]
    )(GoogleAuthCodeGetResponse.apply _)
}
