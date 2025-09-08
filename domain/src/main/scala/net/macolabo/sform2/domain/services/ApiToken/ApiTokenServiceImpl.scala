package net.macolabo.sform2.domain.services.ApiToken

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.ApiTokenDAO
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import net.macolabo.sform2.domain.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertResponse}
import net.macolabo.sform2.domain.utils.TokenUtil
import org.apache.shiro.authc.credential.DefaultPasswordService
import scalikejdbc.DB

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl @Inject()(
  apiTokenDAO: ApiTokenDAO
) extends ApiTokenService with TokenUtil {

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, user: String, userGroup: String): ApiTokenInsertResponse = {
    DB.localTx(implicit session => {
      val tokenString = generateToken
      val service = new DefaultPasswordService
      val apiToken = ApiToken(
        UUID.randomUUID(),
        userGroup,
        service.encryptPassword(tokenString),
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now(),
        user, // TODO 作成ユーザー入れる
        userGroup
      )
      apiTokenDAO.save(apiToken)
      ApiTokenInsertResponse(tokenString)
    })
  }
  def getExpiry(userGroup: String): Option[LocalDateTime] = {
    DB.localTx(implicit session => {
      apiTokenDAO.getExpiry(userGroup)
    })
  }
}
