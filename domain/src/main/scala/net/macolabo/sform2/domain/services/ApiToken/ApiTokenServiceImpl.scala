package net.macolabo.sform2.domain.services.ApiToken

import com.google.inject.Inject
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
    val user = session.get("user_id").getOrElse("")
    val group = session.get("user_group").getOrElse("")
    DB.localTx(implicit session => {
      val tokenString = generateToken
      val service = new DefaultPasswordService
      val id = UUID.randomUUID()
      val apiToken = ApiToken(
        id,
        group,
        service.encryptPassword(tokenString),
        LocalDateTime.now().plusDays(apiTokenInsertRequest.expiry_days),
        LocalDateTime.now(),
        user,
        group
      )
      apiTokenDAO.save(apiToken)
      apiTokenDAO.clearToken(group, id)
      ApiTokenInsertResponse(tokenString)
    })
  }
  def getExpiry(session: Session): Option[LocalDateTime] = {
    val group = session.get("user_group").getOrElse("")
    DB.localTx(implicit session => {
      apiTokenDAO.getExpiry(group)
    })
  }
}
