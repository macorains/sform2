package jobs

import javax.inject.Inject

import akka.actor.Actor
import com.sforce.soap.partner.PartnerConnection
import play.api.Logger
import models.daos.TransfersDAO
import models.daos.TransferTaskDAO
import models.daos.TransferLogDAO
import models.daos.TransferDetailLogDAO
import models.daos.PostdataDAO
import models.connector.SalesforceConnector
import play.api.libs.json._
import com.sforce.soap.partner.sobject._

class SalesforceDataRegister @Inject() (
  transfersDao: TransfersDAO,
  transferTaskDao: TransferTaskDAO,
  transferLogDao: TransferLogDAO,
  transferDetailLogDao: TransferDetailLogDAO,
  postdataDao: PostdataDAO,
  salesforceConnector: SalesforceConnector
)
  extends Actor {

  val transferType = 1

  case class SalesforceTransferConfig(user: String, password: String, securityToken: String)
  object SalesforceTransferConfig {
    implicit def jsonSalesforceTransferConfigWrites: Writes[SalesforceTransferConfig] = Json.writes[SalesforceTransferConfig]
    implicit def jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = Json.reads[SalesforceTransferConfig]
  }
  case class SalesforceTransferTaskConfig(formId: String, sfObject: String, columnConvertDefinition: JsObject)
  object SalesforceTransferTaskConfig {
    implicit def jsonSalesforceTransferTaskConfigWrites: Writes[SalesforceTransferTaskConfig] = Json.writes[SalesforceTransferTaskConfig]
    implicit def jsonSalesforceTransferTaskConfigReads: Reads[SalesforceTransferTaskConfig] = Json.reads[SalesforceTransferTaskConfig]
  }

  case class SalesforceDataSet(postdata_id: Int, sobject: SObject, postdata: JsValue)
  case class TransferLogInfo(log_id: Long)

  def receive: Receive = {
    case msg: String => {
      val salesforceObjectArray = transfersDao.getTransfer(transferType) match {
        case Some(t1: transfersDao.Transfer) => {
          t1.config.validate[SalesforceTransferConfig] match {
            case c1: JsSuccess[SalesforceTransferConfig] => {
              val connection = salesforceConnector.getConnection(c1.get.user, c1.get.password, c1.get.securityToken).getOrElse(None)
              val log_id = transferLogDao.create(transferType)
              transferTaskDao.getTransferTaskList(transferType).map(t2 => {
                t2.config.validate[SalesforceTransferTaskConfig] match {
                  case c2: JsSuccess[SalesforceTransferTaskConfig] => {
                    val columnConvertDefinition = c2.get.columnConvertDefinition.value
                    val posts = postdataDao.getPostdataByFormHashedId(c2.get.formId, transferType)
                    transferLogDao.start(log_id, t2.id, posts.map(p => { p.postdata.toString() }).mkString("[", ",", "]"))
                    val sfobjArray: Array[SalesforceDataSet] =
                      posts.map(p => {
                        val sfobj: SObject = new SObject()
                        sfobj.setType(c2.get.sfObject)
                        sfobj.setField(
                          columnConvertDefinition.get("sfCol").toString,
                          columnConvertDefinition.get("sformCol").toString)
                        SalesforceDataSet(p.postdata_id, sfobj, p.postdata)
                      }).toArray
                    sfobjArray
                  }
                  case _ => None
                }
              }).foreach(f => {
                f match {
                  case s: Array[SalesforceDataSet] => {
                    connection match {
                      case c: PartnerConnection => {
                        val d = s.map(b => b.sobject)
                        val dt = s.map(b => b)
                        var result = salesforceConnector.create(c, d)
                        (0 to d.length - 1).foreach(u => {
                          val errors = result(u).getErrors.map(e => {
                            e.getMessage
                          }).mkString("{\"message\":\"", ",", "\"}")
                          transferDetailLogDao.save(
                            dt(u).postdata_id, transferType, if (result(u).getSuccess) 1 else 9,
                            dt(u).postdata.toString, "{}", if (result(u).getSuccess) 1 else 9, errors, "", "")
                          Logger.debug(s"SfId:${result(u).getId}, success:${result(u).getSuccess}, errors:${errors}, data:${dt(u).postdata.toString}")
                        })
                        val error_count = result.count(r => {
                          !r.getSuccess
                        })
                        // TransferLogDaoに作業終了記録
                        transferLogDao.update(log_id, if (error_count > 0) 9 else 1)
                      }
                      case None => {
                        transferLogDao.update(log_id, 9)
                      }
                    }
                  }
                  case _ => {
                    transferLogDao.update(log_id, 1)
                  }
                }
              })
            }
            case _ => Array(new SObject)
          }
        }
        case _ => {
          Array(new SObject)
        }
      }
    }
  }

}

