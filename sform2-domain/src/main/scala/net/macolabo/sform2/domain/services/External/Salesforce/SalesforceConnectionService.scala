package net.macolabo.sform2.domain.services.External.Salesforce

import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import com.sforce.ws._
import net.macolabo.sform2.domain.services.Transfer.TransferGetTransferResponseSalesforceTransferConfig
import net.macolabo.sform2.domain.utils.Logger

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

  /**
   * sObjectのname,labelのリスト取得
   * @param config TransferConfigSalesforce
   * @return SalesforceGetObjectResponseのリスト
   */
  def getObject(config: TransferGetTransferResponseSalesforceTransferConfig): Option[List[SalesforceGetObjectResponse]] = {
    getConnection(config.sf_user_name, config.sf_password, config.sf_security_token)._1.map(connection => {
      connection.describeGlobal().getSobjects()
        .filter(o => { o.getCreateable })
        .filter(o => { o.getDeletable })
        .filter(o => { o.getQueryable })
        .filter(o => { o.getSearchable })
        .map(o => {
          SalesforceGetObjectResponse(
            o.getName,
            o.getLabel
          )
        }).toList
    })
  }

  /**
   * sObjectフィールドの情報取得
   * @param config TransferConfigSalesforce
   * @param objectName オブジェクト名
   * @return SalesforceGetFieldResponseのリスト
   */
  def getField(config: TransferGetTransferResponseSalesforceTransferConfig, objectName: String): Option[List[SalesforceGetFieldResponse]] = {
    getConnection(config.sf_user_name, config.sf_password, config.sf_security_token)._1.map(connection => {
      connection.describeSObject(objectName).getFields
        .filter(f => f.isCreateable)
        .filter(f => !f.isAutoNumber)
        .filter(f => !f.isCalculated)
        .map(f => {
          SalesforceGetFieldResponse(
            f.getName,
            f.getLabel,
            f.getType.toString
          )
        }).toList
    })
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
