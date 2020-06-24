package net.macolabo.sform2.services.transfer

import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import com.sforce.ws._
import play.api.Logger

class SalesforceConnectionService {

  // 開発時用ダミー
  def getDummyConnection(user: String, password: String, securityToken: String): Option[PartnerConnection] = {
    None
  }

  def getConnection(user: String, password: String, securityToken: String): Option[PartnerConnection] = {
    val config: ConnectorConfig = new ConnectorConfig()
    config.setUsername(user)
    config.setPassword(password + securityToken)
    try {
      val connection = Connector.newConnection(config)

      // コネクション確立とは別にログインできるかチェックする必要がある
      val loginResult = connection.login(user, password + securityToken)
      if(loginResult.isPasswordExpired) {
        // ログインした結果、パスワード有効期限切れならログ出力
        Logger.logger.error(s"Salesforce Login Failed. Password expired. Please change password and security token. user={$user}")
        None
      } else {
        Logger.logger.info(s"Salesforce Login Success. user={$user}")
        Some(connection)
      }
    } catch {
      case e: Exception => {
        Logger.logger.error(s"Salesforce Login Failed. user={$user} password={$password} securityToken={$securityToken}")
        Logger.logger.error(e.toString)
        None
      }
    }
  }

  def create(connection: PartnerConnection, sObjectArray: Array[SObject]) = {
    val res = connection.create(sObjectArray)

    res.foreach(r => r.getErrors.foreach(e => Logger.logger.debug(e.getMessage)))
    res
  }

  def getSObjectNames(connection: PartnerConnection): List[String] = {
    val objects = connection.describeGlobal().getSobjects()

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