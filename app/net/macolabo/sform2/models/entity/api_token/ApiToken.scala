package net.macolabo.sform2.models.entity.api_token

import scalikejdbc._
import java.time.LocalDateTime
import java.util.UUID

case class ApiToken(
                     id: UUID,
                     username: String, // hash化したgroup名
                     password: String, // 生成したトークン
                     expiry: LocalDateTime,
                     created: LocalDateTime
)

object ApiToken extends SQLSyntaxSupport[ApiToken] {
  override val tableName = "d_apitoken"
  def apply(rs: WrappedResultSet): ApiToken = {
    ApiToken(
      UUID.fromString(rs.string("id")),
      rs.string("username"),
      rs.string("password"),
      rs.localDateTime("expiry"),
      rs.localDateTime("created"),
    )
  }
}