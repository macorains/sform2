package models.connector

import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import com.sforce.ws._
import play.Logger

class SalesforceConnector {

  def getConnection(user: String, password: String, securityToken: String): Option[PartnerConnection] = {
    val config: ConnectorConfig = new ConnectorConfig()
    config.setUsername(user)
    config.setPassword(password + securityToken)
    try {
      Some(Connector.newConnection(config))
    } catch {
      case e: Exception => {
        Logger.error("Salesforce Login Failed. user=" + user + " password=" + password + " securityToken=" + securityToken)
        Logger.error("[StackTrace]")
        e.getStackTrace.foreach(err => "  " + Logger.error(err.toString))
        None
      }
    }
  }

  def create(connection: PartnerConnection, sObjectArray: Array[SObject]) = {
    val res = connection.create(sObjectArray)

    res.foreach(r => r.getErrors.foreach(e => Logger.debug(e.getMessage)))
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
object SalesforceConnector {

}