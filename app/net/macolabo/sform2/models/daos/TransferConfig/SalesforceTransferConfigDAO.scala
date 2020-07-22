/*
package net.macolabo.sform2.models.daos.TransferConfig

import javax.inject.Inject
import net.macolabo.sform2.models.daos.{Transfer, TransfersDAO}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.sforce.soap.partner.{DescribeSObjectResult, Field, PartnerConnection}
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.json.SalesforceTransferJson
import net.macolabo.sform2.services.Transfer.SalesforceConnectionService
import net.macolabo.sform2.utils
import net.macolabo.sform2.utils.{Crypto, Logger}
import play.api.Configuration

case class SalesforceTransferConfigDAO @Inject() (
  transfersDao: TransfersDAO,
  configuration: Configuration
) extends BaseTransferConfigDAO with SalesforceTransferJson with Logger {
  override val transferType = 1
  val salesforceConnector = new SalesforceConnectionService
  val crypto = utils.Crypto(configuration)

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
    logger.debug("SalesforceTransferInfoDAO.getTransferConfig")
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
                    c1.get.sfObjectDefinition
                  }
                  case None => {
                    c1.get.sfObjectDefinition match {
                      case Some(s: List[DescribeSObjectResult]) => {
                        logger.error("Couldn't get Object Definition from Salesforce. Use previous data.")
                        c1.get.sfObjectDefinition
                      }
                      case _ => {
                        logger.error("Couldn't get Object Definition from Salesforce. Can't create Object Definition data.")
                        None
                      }
                    }
                  }
                }
                val newSalesforceTransferConfig = SalesforceTransferConfig(Option(t1.id), c1.get.user, c1.get.password, c1.get.securityToken, sfObjectDefinition)
                transfersDao.update(t1.id, t1.type_id, t1.name, t1.status, Json.toJson(newSalesforceTransferConfig).toString)
                decrypto(Json.toJson(newSalesforceTransferConfig))
              }
              case e: JsError => {
                logger.error("JSON validate[SalesforceTransferConfig] failed. ")
                Json.toJson("""{"error" : "2"}""")
              }
            }
          }
        }
      }
      case _ => {
        logger.error("Couldn't get Transfer. ")
        Json.toJson("""{"error" : "3"}""")
      }
    }
  }

  override def saveTransferConfig(config: JsValue, identity: User): JsValue = {
    logger.debug("SalesforceTransferInfoDAO.saveTransferConfig")
    transfersDao.updateByUserIdentity(identity, transferType, encrypto(config).toString())
    Json.toJson("""{}""")
  }

  private def encrypto(config: JsValue) :JsValue = {
    config.validate[SalesforceTransferConfig] match {
      case s: JsSuccess[SalesforceTransferConfig] => {
        s.map(c => {
          val newConfig = SalesforceTransferConfig(c.id, c.user, crypto.encrypt(c.password), crypto.encrypt(c.securityToken), c.sfObjectDefinition)
          Json.toJson(newConfig)
        }).getOrElse(config)
      }
      case _ => {
        logger.error("encrypt failed.")
        config
      }
    }
  }

  private def decrypto(config: JsValue) :JsValue = {
    config.validate[SalesforceTransferConfig] match {
      case s: JsSuccess[SalesforceTransferConfig] => {
        s.map(c => {
          val newConfig = SalesforceTransferConfig(c.id, c.user, crypto.decrypt(c.password), crypto.decrypt(c.securityToken), c.sfObjectDefinition)
          Json.toJson(newConfig)
        }).getOrElse(config)
      }
      case _ => {
        logger.error("decrypt failed.")
        config
      }
    }
  }


}
 */

