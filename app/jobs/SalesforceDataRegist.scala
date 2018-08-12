package jobs

import javax.inject.Inject
import akka.actor.Actor
import com.sforce.soap.partner.PartnerConnection
import play.api.Logger
import models.daos._
import models.connector.SalesforceConnector
import play.api.libs.json._
import com.sforce.soap.partner.sobject._

case class SalesforceTransferConfig(user: String, password: String, securityToken: String)
object SalesforceTransferConfig {
  implicit def jsonSalesforceTransferConfigWrites: Writes[SalesforceTransferConfig] = Json.writes[SalesforceTransferConfig]
  implicit def jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = Json.reads[SalesforceTransferConfig]
}
case class SalesforceTransferTaskConfig(formId: String, sfObject: String, columnConvertDefinition: List[JsObject])
object SalesforceTransferTaskConfig {
  implicit def jsonSalesforceTransferTaskConfigWrites: Writes[SalesforceTransferTaskConfig] = Json.writes[SalesforceTransferTaskConfig]
  implicit def jsonSalesforceTransferTaskConfigReads: Reads[SalesforceTransferTaskConfig] = Json.reads[SalesforceTransferTaskConfig]
}

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

  case class SalesforceDataSet(postdata_id: Int, sobject: SObject, postdata: JsValue)
  case class TransferLogInfo(log_id: Long)

  def receive = {
    case msg: String => {
      println(msg)
      val transferTaskList = transfersDao
        .getTransfer(transferType).map(s => getTransferTask(s).getOrElse(None)).filter(s => s != None)
      transferTaskList.map(s => getPostData(s.asInstanceOf[(List[TransferTask], SalesforceTransferConfig)]))
    }
    case _ => {
      Logger.error("SalesforceDataRegister.receive failed.")
      None
    }
  }

  def getTransferTask(transfer: Any): Option[(List[TransferTask], SalesforceTransferConfig)] = {
    transfer match {
      case s: SalesforceDataRegister.this.transfersDao.Transfer => {
        s.config.validate[SalesforceTransferConfig] match {
          case t: JsSuccess[SalesforceTransferConfig] =>
            Option((transferTaskDao.getTransferTaskList(transferType), t.value))
          case _ =>
            Logger.error("SalesforceDataRegister.getTransferTask failed.")
            None
        }
      }
      case _ => None
    }
  }

  def getPostData(task: (List[TransferTask], SalesforceTransferConfig)) = {
    task match {
      case t: (List[TransferTask], SalesforceTransferConfig) => {
        t._1.foreach(s => {
          s.config.validate[SalesforceTransferTaskConfig] match {
            case c: JsSuccess[SalesforceTransferTaskConfig] => {
              createSalesforceDataSet(t._2, c.get, s.id)
            }
            case e: JsError =>
              Logger.error("SalesforceDataRegister.getPostData failed.")
              None
          }
        })
      }
    }
  }

  def createSalesforceDataSet(transferConfig: SalesforceTransferConfig, taskConfig: SalesforceTransferTaskConfig, taskId: Int) = {
    val columnConvertDefinition: List[(String, String)] = taskConfig.columnConvertDefinition
      .map(s => (s.value.get("sfCol").getOrElse("").toString, s.value.get("sformCol").getOrElse("").toString))
    val posts = postdataDao.getPostdataByFormHashedId(taskConfig.formId, transferType)
    val log_id = transferLogDao.create(transferType)
    transferLogDao.start(log_id, taskId, posts.map(p => { p.postdata.toString() }).mkString("[", ",", "]"))
    posts.size match {
      case p1 if p1 > 0 => {
        val sfobjArray: Array[SalesforceDataSet] =
          posts.map(p => {
            val sfobj: SObject = new SObject()
            sfobj.setType(taskConfig.sfObject)
            columnConvertDefinition.foreach(s => {
              val tmpColName = s._2.replace("\"", "")
              val tmpPostValue: String = (p.postdata \ s"$tmpColName").validate[String] match {
                case c: JsSuccess[String] => c.get
                case _ => ""
              }
              sfobj.setField(s._1.replace("\"", ""), tmpPostValue)
            })
            SalesforceDataSet(p.postdata_id, sfobj, p.postdata)
          }).toArray
        sendToSalesforce(sfobjArray, transferConfig, log_id)
      }
      case p2 if p2 == 0 => {
        transferLogDao.update(log_id, 1)
      }
    }
  }

  def sendToSalesforce(sfobjArray: Array[SalesforceDataSet], transferConfig: SalesforceTransferConfig, log_id: Long) = {
    val connection = salesforceConnector.getConnection(transferConfig.user, transferConfig.password, transferConfig.securityToken).getOrElse(None)
    print(connection)
    connection match {
      case c: PartnerConnection => {
        val d = sfobjArray.map(b => b.sobject)
        val dt = sfobjArray.map(b => b)
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
        error_count match {
          case e1 if e1 > 0 =>
            Logger.error("SalesforceDataRegister.sendToSalesforce failed. (1)")
            transferLogDao.update(log_id, 9)
          case e2 if e2 == 0 =>
            transferLogDao.update(log_id, 1)
        }
      }
      case None => {
        Logger.error("SalesforceDataRegister.sendToSalesforce failed. (2)")
        transferLogDao.update(log_id, 9)
      }
    }
  }

}

