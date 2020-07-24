package net.macolabo.sform2.services.External.Salesforce

import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import com.sforce.ws._
import net.macolabo.sform2.utils.Logger

class SalesforceConnectionService extends Logger {

  /**
   * 疎通確認
   * @param request SalesforceCheckConnectionRequest
   * @return SalesforceCheckConnectionResponse
   */
  def checkConnection(request: SalesforceCheckConnectionRequest): SalesforceCheckConnectionResponse = {
    val result = getConnection(request.username, request.password, request.security_token)
    val resultCode = result._1 match {
      case c:Some[PartnerConnection] => "OK"
      case _ => "NG"
    }
    SalesforceCheckConnectionResponse(resultCode, result._2)
  }

  // 開発時用ダミー
  def getDummyConnection(user: String, password: String, securityToken: String): Option[PartnerConnection] = {
    None
  }

  def getConnection(user: String, password: String, securityToken: String): (Option[PartnerConnection], String) = {
    val config: ConnectorConfig = new ConnectorConfig()
    config.setUsername(user)
    config.setPassword(password + securityToken)

    try {
      val connection = Connector.newConnection(config)

      // コネクション確立とは別にログインできるかチェックする必要がある
      val loginResult = connection.login(user, password + securityToken)
      if(loginResult.isPasswordExpired) {
        // ログインした結果、パスワード有効期限切れならログ出力
        logger.error(s"Salesforce Login Failed. Password expired. Please change password and security token. user={$user}")
        (None,"PasswordExpired")
      } else {
        logger.info(s"Salesforce Login Success. user={$user}")
        (Some(connection),"Success")
      }
    } catch {
      case e: ConnectionException =>
        logger.error(s"Salesforce Login Failed. user={$user} password={$password} securityToken={$securityToken}")
        logger.error(e.toString)
        (None,"LoginFailed")
    }
  }

  def create(connection: PartnerConnection, sObjectArray: Array[SObject]): Array[SaveResult] = {
    val res = connection.create(sObjectArray)

    res.foreach(r => r.getErrors.foreach(e => logger.debug(e.getMessage)))
    res
  }

  def getSObjectNames(connection: PartnerConnection): List[String] = {
    // val objects = connection.describeGlobal().getSobjects()

    connection.describeGlobal().getSobjects()
      .filter(o => { o.getCreateable })
      .filter(o => { o.getDeletable })
      .filter(o => { o.getQueryable })
      .filter(o => { o.getSearchable })
      .map(o => {
        o.getName
      }).toList
  }

  def getSObjectInfo(connection: PartnerConnection, sObjects: List[String]): List[DescribeSObjectResult] = {
    connection.describeSObjects(sObjects.toArray).toList
  }
}
object SalesforceConnectionService {

}