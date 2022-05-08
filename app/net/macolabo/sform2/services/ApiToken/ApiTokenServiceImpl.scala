package net.macolabo.sform2.services.ApiToken

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.ApiTokenDAO
import net.macolabo.sform2.models.entity.api_token.ApiToken
import net.macolabo.sform2.services.ApiToken.insert.ApiTokenInsertRequest
import scalikejdbc.DB

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl @Inject()(
  apiTokenDAO: ApiTokenDAO
) extends ApiTokenService {

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest) = {
    DB.localTx(implicit session => {
      val apiToken = ApiToken(
        UUID.randomUUID(),
        apiTokenInsertRequest.group_name, // 暗号化したものが入ってくる
        apiTokenInsertRequest.token, // 暗号化したものが入ってくる
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now()
      )
      apiTokenDAO.save(apiToken)
    })
  }
}
