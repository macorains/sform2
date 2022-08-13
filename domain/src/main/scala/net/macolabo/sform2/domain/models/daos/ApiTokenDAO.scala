package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import scalikejdbc.DBSession

import java.time.LocalDateTime

trait ApiTokenDAO {
  def save(apiToken: ApiToken)(implicit session: DBSession): Int
  def getExpiry(userGroup: String)(implicit session: DBSession): Option[LocalDateTime]

  /**
   * ユーザー検索(pac4j専用)
   *
   * @param fields  DBフィールド
   * @param key     検索キー
   * @param value   検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]]
}
