package net.macolabo.sform2.domain.services.User

import play.api.libs.json.{Json, OFormat}

case class UserResponse (
  username: String,
  password: Option[String],
  user_group: Option[String],
  role: Option[String],
  first_name: Option[String],
  last_name: Option[String],
  email: Option[String],
  avatar_url: Option[String],
  activated: Boolean,
  deletable: Boolean
)
case class UserListResponse (
  user_list: List[UserResponse]
)

object UserResponse {
  implicit val format: OFormat[UserResponse] = Json.format[UserResponse]
}
object UserListResponse {
  implicit val format: OFormat[UserListResponse] = Json.format[UserListResponse]
}
