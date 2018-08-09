package jobs

import javax.inject.Inject
import akka.actor.Actor
import com.sforce.soap.partner.PartnerConnection
import play.api.Logger
import models.daos._
import models.connector.SalesforceConnector
import play.api.libs.json._
import com.sforce.soap.partner.sobject._
import scala.reflect._

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

  //  case class SalesforceTransferConfig(user: String, password: String, securityToken: String)
  //  object SalesforceTransferConfig {
  //    implicit def jsonSalesforceTransferConfigWrites: Writes[SalesforceTransferConfig] = Json.writes[SalesforceTransferConfig]
  //    implicit def jsonSalesforceTransferConfigReads: Reads[SalesforceTransferConfig] = Json.reads[SalesforceTransferConfig]
  //  }
  //  case class SalesforceTransferTaskConfig(formId: String, sfObject: String, columnConvertDefinition: JsObject)
  //  object SalesforceTransferTaskConfig {
  //    implicit def jsonSalesforceTransferTaskConfigWrites: Writes[SalesforceTransferTaskConfig] = Json.writes[SalesforceTransferTaskConfig]
  //    implicit def jsonSalesforceTransferTaskConfigReads: Reads[SalesforceTransferTaskConfig] = Json.reads[SalesforceTransferTaskConfig]
  //  }

  case class SalesforceDataSet(postdata_id: Int, sobject: SObject, postdata: JsValue)
  case class TransferLogInfo(log_id: Long)

  /*
  def checkTransferConfig(transfer: SalesforceDataRegister.this.transfersDao.Transfer): Option[transfersDao.Transfer] = {
    transfer.config.validate[SalesforceTransferConfig] match {
      case s: JsSuccess[SalesforceTransferConfig] => Option(transfer)
      case _ => None
    }
  }
  */

  def getTransferTask(transfer: Any): Option[(List[TransferTask], SalesforceTransferConfig)] = {
    //def getTransferTask(transfer: Any): Option[(List[SalesforceDataRegister.this.transferTaskDao.TransferTask], SalesforceDataRegister.this.SalesforceTransferConfig)] = {
    transfer match {
      case s: SalesforceDataRegister.this.transfersDao.Transfer => {
        s.config.validate[SalesforceTransferConfig] match {
          case t: JsSuccess[SalesforceTransferConfig] =>
            Option((transferTaskDao.getTransferTaskList(transferType), t.value))
          case _ => None
        }
      }
      case _ => None
    }
  }

  def getPostData(task: (List[TransferTask], SalesforceTransferConfig)) = {
    //def getPostData[A: ClassTag](task: (List[A], SalesforceTransferConfig)) = {
    task match {
      case t: (List[TransferTask], SalesforceTransferConfig) => {
        t._1.foreach(s => {
          //val x1 = (s.config \ "formId").as[String]
          //val x2 = (s.config \ "sfObject").as[String]
          //val x3 = (s.config \ "columnConvertDefinition").as[List[JsObject]]
          //val taskConfig = SalesforceTransferTaskConfig(x1, x2, x3)

          s.config.validate[SalesforceTransferTaskConfig] match {
            case c: JsSuccess[SalesforceTransferTaskConfig] => {
              createSalesforceDataSet(t._2, c.get, s.id)
            }
            case e: JsError => println(e)
            case _ => print("***1")
          }

        })

      }
    }
    /*
    task match {
      case tx :(List=> {
        tx._1 match {
          case t: List[TransferTask @unchecked] if classTag[A] == classTag[TransferTask] => {
            print("aaa");
            t.foreach(s => {
              s.asInstanceOf[TransferTask].config.validate[SalesforceTransferTaskConfig] match {
                case c: JsSuccess[SalesforceTransferTaskConfig] => {
                  createSalesforceDataSet(tx._2, c.get, s.asInstanceOf[TransferTask].id)
                }
                case _ => print("***1")
              }
            })
          }
          case _ => print("***2")
        }
      }
      case _ => print("***3")
    }
    */
  }

  def createSalesforceDataSet(transferConfig: SalesforceTransferConfig, taskConfig: SalesforceTransferTaskConfig, taskId: Int) = {
    val columnConvertDefinition = taskConfig.columnConvertDefinition
      .map(s => (s.value.get("sfCol").toString, s.value.get("sformCol").toString))

    val posts = postdataDao.getPostdataByFormHashedId(taskConfig.formId, transferType)
    val log_id = transferLogDao.create(transferType)
    transferLogDao.start(log_id, taskId, posts.map(p => { p.postdata.toString() }).mkString("[", ",", "]"))
    val sfobjArray: Array[SalesforceDataSet] =
      posts.map(p => {
        val sfobj: SObject = new SObject()
        (p.postdata \ "col1").getOrElse(JsNull).as[String]
        sfobj.setType(taskConfig.sfObject)
        columnConvertDefinition.foreach(s => {
          sfobj.setField(s._1, (p.postdata \ s._2).getOrElse(JsNull).as[String])
        })
        /*
        sfobj.setField(
          columnConvertDefinition.get("sfCol").toString,
          columnConvertDefinition.get("sformCol").toString)
          */

        SalesforceDataSet(p.postdata_id, sfobj, p.postdata)
      }).toArray
    sendToSalesforce(sfobjArray, transferConfig, log_id)
  }

  def sendToSalesforce(sfobjArray: Array[SalesforceDataSet], transferConfig: SalesforceTransferConfig, log_id: Long) = {
    val connection = salesforceConnector.getConnection(transferConfig.user, transferConfig.password, transferConfig.securityToken).getOrElse(None)
    print(connection)
    connection match {
      case c: PartnerConnection => {
        val d = sfobjArray.map(b => b.sobject)
        val dt = sfobjArray.map(b => b)
        d.foreach(s => {
          println("***")
          println(s.getField("company"))
          println(s.getField("lastName"))
          println("***")
        })
        var result = salesforceConnector.create(c, d)
        print(result)
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

  def receive = {
    case msg: String => {
      println(msg)
      val transferTaskList = transfersDao
        .getTransfer(transferType).map(s => getTransferTask(s).getOrElse(None)).filter(s => s != None)
      transferTaskList.map(s => getPostData(s.asInstanceOf[(List[TransferTask], SalesforceTransferConfig)]))
    }
    case _ => {
      None
    }

    /*
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
    */
  }

}

