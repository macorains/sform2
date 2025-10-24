package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import scalikejdbc._

import java.time.LocalDateTime
import java.util.UUID

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

  def clearToken(userGroup: String, newTokenId:UUID)(implicit session: DBSession): Unit = {
    withSQL {
      val c = ApiToken.column
      update(ApiToken).set(
        c.expiry -> LocalDateTime.now()
      ).where
        .eq(c.user_group, userGroup)
        .and
        .ne(c.id, newTokenId.toString)
    }.update().apply()
  }

  /**
   * ユーザー検索(pac4j専用)
   *
   * @param fields  DBフィールド
   * @param key     検索キー
   * @param value   検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]] = {
    StringSQLRunner(s"""SELECT $fields FROM d_apitoken as c WHERE $key = '$value' AND expiry > NOW()""").run()
  }

}
