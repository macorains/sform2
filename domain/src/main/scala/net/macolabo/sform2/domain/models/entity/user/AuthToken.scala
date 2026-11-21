package net.macolabo.sform2.domain.models.entity.user

import scalikejdbc._

import java.time.{LocalDateTime, ZonedDateTime}
import java.util.UUID

case class AuthToken(
  id: UUID,
  user_id: UUID,
  expiry: LocalDateTime,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object AuthToken extends SQLSyntaxSupport[AuthToken] {
  override val tableName = "d_authtoken"
  def apply(rs: WrappedResultSet): AuthToken = {
    AuthToken(
      UUID.fromString(rs.string("id")),
      UUID.fromString(rs.string("user_id")),
      rs.localDateTime("expiry"),
      rs.zonedDateTime("created"),
      rs.zonedDateTime("modified")
    )
  }
}
