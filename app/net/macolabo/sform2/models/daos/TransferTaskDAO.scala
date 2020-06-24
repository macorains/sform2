package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.json.{TransferTaskEntry, TransferTaskJson}
import net.macolabo.sform2.models.entity.TransferTask
import play.api.libs.json._
import scalikejdbc._
import scalikejdbc.{DB, WrappedResultSet}

class TransferTaskDAO extends TransferTaskJson {

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
        t => { TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, Json.toJson(t.config), t.created, t.modified, 0) })
      Json.toJson(transferTaskEntityList)
    }
  }

  def getTransferTask(id: Int): JsValue = {
    DB localTx { implicit l =>
      val t = sql"""SELECT ID,TRANSFER_TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED
      FROM D_TRANSFER_TASKS
      WHERE ID=$id"""
        .map(rs => TransferTask(rs)).single.apply().get
      val transferTaskEntity = TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, Json.toJson(t.config), t.created, t.modified, 0)
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
