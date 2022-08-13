package net.macolabo.sform2.domain.models.user

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

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
