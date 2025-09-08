package net.macolabo.sform2.domain.services.ApiToken

import net.macolabo.sform2.domain.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertResponse}

import java.time.LocalDateTime

trait ApiTokenService {
  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, user: String, userGroup: String): ApiTokenInsertResponse
  def getExpiry(userGroup: String): Option[LocalDateTime]
}
