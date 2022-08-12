package net.macolabo.sform.api.models.daos

import scalikejdbc.{DBSession, StringSQLRunner}

class ApiTokenDAOImpl extends ApiTokenDAO {

  /**
   * ユーザー検索(pac4j)
   *
   * @param fields  DBフィールド
   * @param key     検索キー
   * @param value   検索値
   * @param session DBSession
   * @return
   */
  def find(fields: String, key: String, value: String)(implicit session: DBSession): List[Map[String, Any]] = {
    StringSQLRunner(s"""SELECT $fields FROM d_apitoken as c WHERE $key = '$value'""").run()
  }
}
