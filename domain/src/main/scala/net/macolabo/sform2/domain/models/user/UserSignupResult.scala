package net.macolabo.sform2.domain.models.user

import play.api.libs.json.{Format, Json}

case class UserSignUpResult(resultCode:Int, message:Option[String])

object UserSignUpResult {
  implicit val format: Format[UserSignUpResult] = Json.format[UserSignUpResult]
}
