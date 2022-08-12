package net.macolabo.sform.api.models.entity.user

import scalikejdbc._

import java.time.LocalDateTime
import java.util.UUID

case class AuthToken(
  id: UUID,
  user_id: UUID,
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
