package models.daos.TransferConfig

import javax.inject.Inject
import models.connector.SalesforceConnector
import models.daos.{ Transfer, TransfersDAO }
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.sforce.soap.partner.{ DescribeSObjectResult, Field, PartnerConnection }
import models.User
import models.json.SalesforceTransferJson
import play.Logger

case class SalesforceTransferConfigDAO @Inject() (
  transfersDao: TransfersDAO
) extends BaseTransferConfigDAO with (SalesforceTransferJson) {
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
    Logger.debug("SalesforceTransferInfoDAO.getTransferConfig")
    transfersDao.getTransfer(transferType) match {
      case tx: List[Transfer] => {
        // 暫定対応
        tx.size match {
          case size1 if size1 > 0 => {
            val t1 = tx.apply(0)
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
                      case Some(s: List[DescribeSObjectResult]) => {
                        Logger.error("Couldn't get Object Definition from Salesforce. Use previous data.")
                        c1.get.sfObjectDefinition
                      }
                      case _ => {
                        Logger.error("Couldn't get Object Definition from Salesforce. Can't create Object Definition data.")
                        None
                      }
                    }
                  }
                }
                val newSalesforceTransferConfig = SalesforceTransferConfig(Option(t1.id), c1.get.user, c1.get.password, c1.get.securityToken, sfObjectDefinition)
                transfersDao.update(t1.id, t1.type_id, t1.name, t1.status, Json.toJson(newSalesforceTransferConfig).toString)
                Json.toJson(newSalesforceTransferConfig)
              }
              case e: JsError => {
                Logger.error("JSON validate[SalesforceTransferConfig] failed. ")
                Logger.error("[config]")
                Logger.error(t1.config.toString)
                Logger.error(e.toString)
                Json.toJson("""{"error" : "2"}""")
              }
            }
          }
        }
      }
      case _ => {
        Logger.error("Couldn't get Transfer. ")
        Json.toJson("""{"error" : "3"}""")
      }
    }
  }

  override def saveTransferConfig(config: JsValue, identity: User): JsValue = {
    Logger.debug("SalesforceTransferInfoDAO.saveTransferConfig")
    transfersDao.updateByUserIdentity(identity, transferType, config.toString())
    Json.toJson("""{}""")
  }

}

