package net.macolabo.sform2.domain.services.ApiToken

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.ApiTokenDAO
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import net.macolabo.sform2.domain.services.ApiToken.insert.ApiTokenInsertRequest
import org.apache.shiro.authc.credential.DefaultPasswordService
import scalikejdbc.DB

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl @Inject()(
  apiTokenDAO: ApiTokenDAO
) extends ApiTokenService {

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, user: String, userGroup: String): Unit = {
    DB.localTx(implicit session => {
      val service = new DefaultPasswordService
      val apiToken = ApiToken(
        UUID.randomUUID(),
        userGroup,
        service.encryptPassword(apiTokenInsertRequest.token),
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now(),
        user, // TODO 作成ユーザー入れる
        userGroup
      )
      apiTokenDAO.save(apiToken)
    })
  }
  def getExpiry(userGroup: String): Option[LocalDateTime] = {
    DB.localTx(implicit session => {
      apiTokenDAO.getExpiry(userGroup)
    })
  }
}
