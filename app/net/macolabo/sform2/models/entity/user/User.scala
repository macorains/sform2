package net.macolabo.sform2.models.entity.user

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import scalikejdbc._
import java.util.UUID

/**
 * The user object.
 *
 * @param userID    The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param group     Group name of user.
 * @param role      Role of user.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName  Maybe the last name of the authenticated user.
 * @param fullName  Maybe the full name of the authenticated user.
 * @param email     Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 * @param deletable User is deletable or not.
 */
case class User(
  user_id: UUID,
  username: String,
  password: String,
  user_group: Option[String],
  role: Option[String],
  first_name: Option[String],
  last_name: Option[String],
  full_name: Option[String],
  email: Option[String],
  avatar_url: Option[String],
  activated: Boolean,
  deletable: Boolean) {

  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name: Option[String] = full_name.orElse {
    first_name -> last_name match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}

object User extends SQLSyntaxSupport[User] {
  override val tableName = "M_USERINFO"
  def apply(rs: WrappedResultSet): User = {
    User(
      UUID.fromString(rs.string("user_id")),
      rs.string("username"),
      rs.string("password"),
      rs.stringOpt("user_group"),
      rs.stringOpt("role"),
      rs.stringOpt("first_name"),
      rs.stringOpt("last_name"),
      rs.stringOpt("full_name"),
      rs.stringOpt("email"),
      rs.stringOpt("avatar_url"),
      rs.boolean("activated"),
      rs.boolean("deletable")
    )
  }
}
