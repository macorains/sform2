package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.api_token.ApiToken
import scalikejdbc.DBSession

trait ApiTokenDAO {
  def save(apiToken: ApiToken)(implicit session: DBSession): Int
}
