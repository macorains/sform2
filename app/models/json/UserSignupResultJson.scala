package models.json

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class UserSignUpResult(resultCode:Int, activateUrl:Option[String])

trait userSignUpResultJson {
  implicit val jsonUserSignUpResultWrites: Writes[UserSignUpResult] = (userSignUpResult: UserSignUpResult) => Json.obj(
  "resultCode" -> userSignUpResult.resultCode,
    "activateUrl" -> userSignUpResult.activateUrl.getOrElse("")
  )

  implicit val jsonUserSignUpResultReads: Reads[UserSignUpResult] = (
    (JsPath \ "resultCode").read[Int] ~
      (JsPath \ "activateUrl").readNullable[String]
    )(UserSignUpResult.apply _)
}