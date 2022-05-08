package net.macolabo.sform2.services.ApiToken

import net.macolabo.sform2.services.ApiToken.insert.ApiTokenInsertRequest

trait ApiTokenService {
  def insert(apiTokenInsertRequest: ApiTokenInsertRequest)
}
