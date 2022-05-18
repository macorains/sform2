package net.macolabo.sform2.services.ApiToken

import net.macolabo.sform2.services.ApiToken.insert.ApiTokenInsertRequest

import java.time.LocalDateTime

trait ApiTokenService {
  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, user: String, userGroup: String): Unit
  def getExpiry(userGroup: String): Option[LocalDateTime]
}
