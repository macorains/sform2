package net.macolabo.sform2.domain.models.entity.user

import scalikejdbc._

import java.time.LocalDateTime
import java.util.UUID

case class AuthToken(
  id: UUID,
  user_id: UUID,
  expiry: LocalDateTime
)

object AuthToken extends SQLSyntaxSupport[AuthToken] {
  override val tableName = "d_authtoken"
  def apply(rs: WrappedResultSet): AuthToken = {
    AuthToken(
      UUID.fromString(rs.string("id")),
      UUID.fromString(rs.string("user_id")),
      rs.localDateTime("expiry")
    )
  }
}
