package net.macolabo.sform2.services.ApiToken

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.ApiTokenDAO
import net.macolabo.sform2.models.entity.api_token.ApiToken
import net.macolabo.sform2.services.ApiToken.insert.ApiTokenInsertRequest
import org.apache.shiro.authc.credential.DefaultPasswordService
import scalikejdbc.DB

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl @Inject()(
  apiTokenDAO: ApiTokenDAO
) extends ApiTokenService {

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, userGroup: String): Unit = {
    DB.localTx(implicit session => {
      val service = new DefaultPasswordService
      val apiToken = ApiToken(
        UUID.randomUUID(),
        service.encryptPassword(userGroup),
        service.encryptPassword(apiTokenInsertRequest.token),
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now()
      )
      apiTokenDAO.save(apiToken)
    })
  }
}
