package models.daos.TransferConfig

import javax.inject.Inject
import models.connector.SalesforceConnector
import models.daos.{Transfer, TransfersDAO}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.sforce.soap.partner.{DescribeSObjectResult, Field, PartnerConnection}
import models.User
import models.json.SalesforceTransferJson
import utils.Crypto
import play.Logger
import play.api.Configuration

case class SalesforceTransferConfigDAO @Inject() (
  transfersDao: TransfersDAO,
  configuration: Configuration
) extends BaseTransferConfigDAO with (SalesforceTransferJson) {
  override val transferType = 1
  val salesforceConnector = new SalesforceConnector
  val crypto = Crypto(configuration)

  case class SalesforceTransferConfig(id: Option[Int], user: String, password: String, securityToken: String, sfObjectDefinition: Option[List[DescribeSObjectResult]])
  implicit val jsonSalesforceTransferConfigWrites = (
    (JsPath \ "id").writeNullable[Int] ~
    (JsPath \ "user").write[String] ~
    (JsPath \ "password").write[String].contramap[String](_ + "a") ~
    (JsPath \ "securityToken").write[String].contramap[String](_ + "a") ~
    (JsPath \ "sfObjectDefinition").writeNullable[List[DescribeSObjectResult]]
  )(unlift(SalesforceTransferConfig.unapply))
  implicit val jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = (
    (JsPath \ "id").readNullable[Int] ~
    (JsPath \ "user").read[String] ~
    (JsPath \ "password").read[String].map[String](_ + "a") ~
    (JsPath \ "securityToken").read[String].map[String](_ + "a") ~
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
                    c1.get.sfObjectDefinition
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
                decrypto(Json.toJson(newSalesforceTransferConfig))
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
        Logger.error("encrypt failed.")
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
        Logger.error("decrypt failed.")
        config
      }
    }
  }

}

