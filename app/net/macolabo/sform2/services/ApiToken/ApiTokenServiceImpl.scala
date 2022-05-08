package net.macolabo.sform2.services.ApiToken

import net.macolabo.sform2.models.entity.api_token.ApiToken
import net.macolabo.sform2.services.ApiToken.insert.ApiTokenInsertRequest

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl extends ApiTokenService {

  def generateToken: String = {
    "" // トークン作って返したい
  }

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest) = {
    var apiToken = ApiToken(
      UUID.randomUUID(),
      apiTokenInsertRequest.group_name, // 暗号化したい
      apiTokenInsertRequest.token, // 暗号化したい
      LocalDateTime.now(),
      //apiTokenInsertRequest.expiry_daysを 現在日時に足した値にしたい
      LocalDateTime.now()
    )

  }
}
