package net.macolabo.sform2.models.entity.user

import scalikejdbc._

import java.time.LocalDateTime
import java.util.UUID

case class AuthToken(
  id: UUID,
  userID: UUID,
  expiry: LocalDateTime
)

object AuthToken extends SQLSyntaxSupport[AuthToken] {
  override val tableName = "D_AUTHTOKEN"
  def apply(rs: WrappedResultSet): AuthToken = {
    AuthToken(
      UUID.fromString(rs.string("id")),
      UUID.fromString(rs.string("user_id")),
      rs.localDateTime("expiry")
    )
  }
}
