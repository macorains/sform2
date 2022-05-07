package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.api_token.ApiToken
import scalikejdbc._

class ApiTokenDAOImpl extends ApiTokenDAO {
  def save(apiToken: ApiToken)(implicit session: DBSession): Int = {
    withSQL {
      val t = ApiToken.column
      insertInto(ApiToken).namedValues(
        t.id -> apiToken.id.toString,
        t.username -> apiToken.username,
        t.password -> apiToken.password,
        t.expiry -> apiToken.expiry,
        t.created -> apiToken.created
      )
    }.update().apply()
  }
}
