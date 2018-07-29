package models.daos

import models.User
import play.api.libs.json._
import scalikejdbc._
import scalikejdbc.{ DB, WrappedResultSet }

class TransferTaskDAO {

  case class TransferTask(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsValue,
    created: Option[String], modified: Option[String])
  object TransferTask extends SQLSyntaxSupport[TransferTask] {
    override val tableName = "d_transfer_tasks"
    def apply(rs: WrappedResultSet): TransferTask = {
      TransferTask(rs.int("id"), rs.int("transfer_type_id"), rs.string("name"), rs.int("status"),
        Json.parse(rs.string("config")), rs.stringOpt("created"), rs.stringOpt("modified"))
    }
  }

  case class TransferTaskJson(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsObject,
    created: Option[String], modified: Option[String])
  object TransferTaskJson {
    implicit def jsonTransferTaskWrites: Writes[TransferTaskJson] = Json.writes[TransferTaskJson]
    implicit def jsonTransferTaskReads: Reads[TransferTaskJson] = Json.reads[TransferTaskJson]
  }

  def getTransferTaskList(transferType: Int) = {
    DB localTx { implicit l =>
      sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE TRANSFER_TYPE_ID=${transferType}"""
        .map(rs => TransferTask(rs)).list.apply()
    }
  }

  def getTransferTaskListByFormId(formId: Int) = {
    DB localTx { implicit l =>
      val transferTaskList = sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE FORM_ID=${formId}"""
        .map(rs => TransferTask(rs)).list.apply()
      val transferTaskListJson = transferTaskList.map(
        t => { TransferTaskJson(t.id, t.transfer_type_id, t.name, t.status, t.config.as[JsObject], t.created, t.modified) })
      Json.toJson(transferTaskListJson)
    }
  }

  def save(transfer_type_id: Int, name: String, status: Int, config: String, user: String, user_group: String) = {
    DB localTx { implicit l =>
      sql"""
      INSERT INTO D_TRANSFER_TASKS
      (TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,USER_GROUP,CREATED_USER,CREATED,MODIFIED_USER,MODIFIED)
      VALUES (${transfer_type_id},${name},${status},${config},${user_group},${user},NOW(),${user},NOW())"""
        .update.apply()
    }
  }

  def update(id: Int, transfer_type_id: Int, name: String, status: Int, config: String, user: String) = {
    DB localTx { implicit l =>
      sql"""
      UPDATE D_TRANSFER_TASKS
      SET TRANSFER_TYPE_ID=${transfer_type_id}, NAME=${name}, STATUS=${status}, CONFIG=${config},
      MODIFIED_USER=${user}, MODIFIED=NOW()
        WHERE ID = ${id}"""
        .update.apply()
    }
  }

  def bulkSave(dt: JsValue, identity: User) = {
    val transferTaskList = (dt \ "transferTasks").as[JsValue]
    println(transferTaskList)

    transferTaskList.validate[Array[TransferTaskJson]] match {
      case s: JsSuccess[Array[TransferTaskJson]] => {
        s.get.map(t => {
          t.id match {
            case 0 => {
              this.save(t.transfer_type_id, t.name, t.status, t.config.toString(), identity.userID.toString, identity.group.getOrElse(""))
            }
            case _ => {
              this.update(t.id, t.transfer_type_id, t.name, t.status, t.config.toString(), identity.userID.toString)
            }
          }
        })
      }
      case _ => {
        println("error!")
      }
    }

  }
}
