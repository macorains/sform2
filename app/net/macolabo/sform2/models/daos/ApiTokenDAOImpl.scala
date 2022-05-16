package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.api_token.ApiToken
import scalikejdbc._
import java.time.LocalDateTime

class ApiTokenDAOImpl extends ApiTokenDAO {
  def save(apiToken: ApiToken)(implicit session: DBSession): Int = {
    withSQL {
      val t = ApiToken.column
      insertInto(ApiToken).namedValues(
        t.id -> apiToken.id.toString,
        t.username -> apiToken.username,
        t.password -> apiToken.password,
        t.expiry -> apiToken.expiry,
        t.created -> apiToken.created,
        t.created_user -> apiToken.created_user,
        t.user_group -> apiToken.user_group
      )
    }.update().apply()
  }

  def getExpiry(userGroup: String)(implicit session: DBSession): Option[LocalDateTime] = {
    val f = ApiToken.syntax("f")
    withSQL(
      select(
        f.expiry
      )
        .from(ApiToken as f)
        .where
        .eq(f.user_group, userGroup)
        .and
        .gt(f.expiry, LocalDateTime.now())
        .orderBy(f.expiry).desc
    ).map(rs => rs.localDateTime("expiry")).list().apply().headOption
  }
}
