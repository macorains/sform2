package models.daos.TransferConfig

import javax.inject.Inject

import models.connector.SalesforceConnector
import models.daos.TransfersDAO
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.sforce.soap.partner.{ DescribeSObjectResult, Field, PartnerConnection }
import models.User
import models.json.SalesforceTransferJson

case class SalesforceTransferConfigDAO @Inject() (
  transfersDao: TransfersDAO
) extends BaseTransferConfigDAO with SalesforceTransferJson {
  override val transferType = 1
  val salesforceConnector = new SalesforceConnector

  case class SalesforceTransferConfig(id: Option[Int], user: String, password: String, securityToken: String, sfObjectDefinition: Option[List[DescribeSObjectResult]])
  implicit val jsonSalesforceTransferConfigWrites = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "user").write[String] ~
    (JsPath \ "password").write[String] ~
    (JsPath \ "securityToken").write[String] ~
    (JsPath \ "sfObjectDefinition").writeNullable[List[DescribeSObjectResult]]
  )(unlift(SalesforceTransferConfig.unapply))
  implicit val jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "user").read[String] ~
    (JsPath \ "password").read[String] ~
    (JsPath \ "securityToken").read[String] ~
    (JsPath \ "sfObjectDefinition").readNullable[List[DescribeSObjectResult]]
  )(SalesforceTransferConfig.apply _)

  override def getTransferConfig: JsValue = {
    println("SalesforceTransferInfoDAO")
    transfersDao.getTransfer(transferType) match {
      case Some(t1: transfersDao.Transfer) => {
        t1.config.validate[SalesforceTransferConfig] match {
          case c1: JsSuccess[SalesforceTransferConfig] => {
            // ToDo configの日付見て古ければ取得しにいく
            val sfObjectDefinition: Option[List[DescribeSObjectResult]] = salesforceConnector.getConnection(c1.get.user, c1.get.password, c1.get.securityToken) match {
              case Some(c: PartnerConnection) => {
                val sObjectNames = salesforceConnector.getSObjectNames(c)
                Option(salesforceConnector.getSObjectInfo(c, sObjectNames))
              }
              case None => {
                c1.get.sfObjectDefinition match {
                  case Some(s: List[DescribeSObjectResult]) => c1.get.sfObjectDefinition
                  case _ => None
                }
              }
            }
            val newSalesforceTransferConfig = SalesforceTransferConfig(Option(t1.id), c1.get.user, c1.get.password, c1.get.securityToken, sfObjectDefinition)
            transfersDao.update(t1.id, t1.type_id, t1.name, t1.status, Json.toJson(newSalesforceTransferConfig).toString)
            Json.toJson(newSalesforceTransferConfig)
          }
          case e: JsError => {
            println(e.toString)
            Json.toJson("""{"error" : "2"}""")
          }
          case _ => {
            Json.toJson("""{"error" : "2"}""")
          }
        }
      }
      case None => {
        Json.toJson("""{"error" : "3"}""")
      }
    }
  }

  override def saveTransferConfig(config: JsValue, identity: User): JsValue = {
    transfersDao.updateByUserIdentity(identity, transferType, config.toString())
    Json.toJson("""{}""")
  }

}

