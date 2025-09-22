package net.macolabo.sform2.domain.services.ApiToken

import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertResponse}
import play.api.mvc.Session

import java.time.LocalDateTime

trait ApiTokenService {
  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, sessionInfo: SessionInfo): ApiTokenInsertResponse
  def getExpiry(sessionInfo: SessionInfo): Option[LocalDateTime]
}
