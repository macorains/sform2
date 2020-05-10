package models.json

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class VerificationRequestEntry(verificationCode: String, formToken: String)

trait VerificationRequestJson {

  implicit val jsonVerificationRequestWrites: Writes[VerificationRequestEntry] = (verificationRequestEntry: VerificationRequestEntry) => Json.obj(
    "verification_code" -> verificationRequestEntry.verificationCode,
    "form_token" -> verificationRequestEntry.formToken
  )

  implicit val jsonVerificationRequestReads: Reads[VerificationRequestEntry] = (
    (JsPath \ "verification_code").read[String] ~
    (JsPath \ "form_token").read[String]
  )(VerificationRequestEntry.apply _)
}

