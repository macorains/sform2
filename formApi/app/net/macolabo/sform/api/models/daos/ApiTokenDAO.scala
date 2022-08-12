package net.macolabo.sform.api.models.daos

import scalikejdbc.DBSession

trait ApiTokenDAO {
  /**
   * ユーザー検索(pac4j)
   * @param fields DBフィールド
   * @param key 検索キー
   * @param value 検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]]
}
