package net.macolabo.sform2.models.json

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class UserSignUpResult(resultCode:Int, message:Option[String])

trait UserSignUpResultJson {
  implicit val jsonUserSignUpResultWrites: Writes[UserSignUpResult] = (userSignUpResult: UserSignUpResult) => Json.obj(
  "resultCode" -> userSignUpResult.resultCode,
    "message" -> userSignUpResult.message
  )

  implicit val jsonUserSignUpResultReads: Reads[UserSignUpResult] = (
    (JsPath \ "resultCode").read[Int] ~
      (JsPath \ "message").readNullable[String]
    )(UserSignUpResult.apply _)
}