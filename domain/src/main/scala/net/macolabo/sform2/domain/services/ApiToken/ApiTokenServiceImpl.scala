package net.macolabo.sform2.domain.services.ApiToken

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.daos.ApiTokenDAO
import net.macolabo.sform2.domain.models.entity.api_token.ApiToken
import net.macolabo.sform2.domain.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertResponse}
import net.macolabo.sform2.domain.utils.TokenUtil
import org.apache.shiro.authc.credential.DefaultPasswordService
import play.api.mvc.Session
import scalikejdbc.DB

import java.time.LocalDateTime
import java.util.UUID

class ApiTokenServiceImpl @Inject()(
  apiTokenDAO: ApiTokenDAO
) extends ApiTokenService with TokenUtil {

  def insert(apiTokenInsertRequest: ApiTokenInsertRequest, session: Session): ApiTokenInsertResponse = {
    val sessionInfo = SessionInfo(session)
    DB.localTx(implicit session => {
      val tokenString = generateToken
      val service = new DefaultPasswordService
      val id = UUID.randomUUID()
      val apiToken = ApiToken(
        id,
        sessionInfo.user_group,
        service.encryptPassword(tokenString),
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now(),
        sessionInfo.user_id,
        sessionInfo.user_group
      )
      apiTokenDAO.save(apiToken)
      apiTokenDAO.clearToken(sessionInfo.user_group, id)
      ApiTokenInsertResponse(tokenString)
    })
  }
  def getExpiry(session: Session): Option[LocalDateTime] = {
    val sessionInfo = SessionInfo(session)
    DB.localTx(implicit session => {
      apiTokenDAO.getExpiry(sessionInfo.user_group)
    })
  }
}
