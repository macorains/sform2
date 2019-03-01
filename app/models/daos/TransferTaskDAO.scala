package models.daos

import models.User
import models.json.{ TransferTaskEntry, TransferTaskJson }
import models.entity.TransferTask
import play.api.libs.json._
import scalikejdbc._
import scalikejdbc.{ DB, WrappedResultSet }

class TransferTaskDAO extends TransferTaskJson {

  //  case class TransferTask(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsValue,
  //    created: Option[String], modified: Option[String])
  //  object TransferTask extends SQLSyntaxSupport[TransferTask] {
  //    override val tableName = "D_TRANSFER_TASKS"
  //    def apply(rs: WrappedResultSet): TransferTask = {
  //      TransferTask(rs.int("id"), rs.int("transfer_type_id"), rs.string("name"), rs.int("status"),
  //        Json.parse(rs.string("config")), rs.stringOpt("created"), rs.stringOpt("modified"))
  //    }
  //  }
  //  case class TransferTaskJson(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsObject,
  //    created: Option[String], modified: Option[String], del_flg: Int)
  //  object TransferTaskJson {
  //    implicit def jsonTransferTaskWrites: Writes[TransferTaskJson] = Json.writes[TransferTaskJson]
  //    implicit def jsonTransferTaskReads: Reads[TransferTaskJson] = Json.reads[TransferTaskJson]
  //  }

  def getTransferTaskList(transferType: Int): List[TransferTask] = {
    DB localTx { implicit l =>
      sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,FORM_ID,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE TRANSFER_TYPE_ID=$transferType"""
        .map(rs => TransferTask(rs)).list.apply()
    }
  }

  def getTransferTaskListByFormId(hashed_form_id: String): List[TransferTask] = {
    DB localTx { implicit l =>
      sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE FORM_ID=$hashed_form_id"""
        .map(rs => TransferTask(rs)).list.apply()
    }
  }

  def getTransferTaskListJsonByFormId(hashed_form_id: String): JsValue = {
    DB localTx { implicit l =>
      val transferTaskList = sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE FORM_ID=$hashed_form_id"""
        .map(rs => TransferTask(rs)).list.apply()
      val transferTaskEntityList = transferTaskList.map(
        t => { TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, t.config.as[JsObject], t.created, t.modified, t.del_flg) })
      Json.toJson(transferTaskEntityList)
    }
  }

  def getTransferTask(id: Int): JsValue = {
    DB localTx { implicit l =>
      val t = sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE ID=$id"""
        .map(rs => TransferTask(rs)).single.apply().get
      val transferTaskEntity = TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, t.config.as[JsObject], t.created, t.modified, t.del_flg)
      Json.toJson(transferTaskEntity)
    }
  }

  def save(transfer_type_id: Int, name: String, status: Int, config: String, user: String, user_group: String): Int = {
    DB localTx { implicit l =>
      sql"""
      INSERT INTO D_TRANSFER_TASKS
      (TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,USER_GROUP,CREATED_USER,CREATED,MODIFIED_USER,MODIFIED)
      VALUES ($transfer_type_id,$name,$status,$config,$user_group,$user,NOW(),$user,NOW())"""
        .update.apply()
    }
  }

  def update(id: Int, transfer_type_id: Int, name: String, status: Int, config: String, user: String): Int = {
    DB localTx { implicit l =>
      sql"""
      UPDATE D_TRANSFER_TASKS
      SET TRANSFER_TYPE_ID=$transfer_type_id, NAME=$name, STATUS=$status, CONFIG=$config,
      MODIFIED_USER=$user, MODIFIED=NOW()
        WHERE ID = $id"""
        .update.apply()
    }
  }

  def delete(id: Int): Int = {
    DB localTx { implicit l =>
      sql"""
      DELETE FROM D_TRANSFER_TASKS
      WHERE ID = $id"""
        .update.apply()
    }
  }

  def bulkSave(dt: JsValue, identity: User): Any = {
    val transferTaskList = (dt \ "transferTasks").as[JsValue]
    println(transferTaskList)

    transferTaskList.validate[Array[TransferTaskEntry]] match {
      case s: JsSuccess[Array[TransferTaskEntry]] =>
        s.get.map(t => {
          t.id match {
            case 0 =>
              this.save(t.transfer_type_id, t.name, t.status, t.config.toString(), identity.userID.toString, identity.group.getOrElse(""))
            case _ =>
              t.del_flg match {
                case f1: Int if f1 != 1 =>
                  this.update(t.id, t.transfer_type_id, t.name, t.status, t.config.toString(), identity.userID.toString)
                case f2: Int if f2 == 1 =>
                  this.delete(t.id)
              }
          }
        })
      case e: JsError =>
        println("Error: TransferTaskDao.bulkSave failed.")
        println(e.toString)
    }

  }
}
