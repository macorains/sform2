package net.macolabo.sform2.controllers

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class SignInVerificationRequest
(
  authkey: String,
  verification_code: String
)

trait SignInVerificationRequestConverter {
  implicit val SignInVerificationRequestReads: Reads[SignInVerificationRequest] = (
    (JsPath \ "authkey").read[String] ~
      (JsPath \ "verification_code").read[String]
  )(SignInVerificationRequest.apply _)

  implicit val signInVerificationRequestWrites: Writes[SignInVerificationRequest]
  = (signInVerificationRequest: SignInVerificationRequest) => Json.obj(
    "authkey" -> signInVerificationRequest.authkey,
    "verification_code" -> signInVerificationRequest.verification_code
  )
}
