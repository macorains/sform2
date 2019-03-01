package models.daos

import models.User
import org.joda.time.DateTime
import play.api.libs.json._
import scalikejdbc._
import scalikejdbc.{ DB, WrappedResultSet }

case class Transfer(id: Int, type_id: Int, name: String, status: Int, config: JsValue, modified: DateTime)
object Transfer extends SQLSyntaxSupport[Transfer] {
  override val tableName = "M_TRANSFERS"
  def apply(rs: WrappedResultSet): Transfer = {
    Transfer(rs.int("id"), rs.int("type_Id"), rs.string("name"), rs.int("status"), Json.parse(rs.string("config")), rs.jodaDateTime("modified"))
  }
}

class TransfersDAO {

  //  case class Transfer(id: Int, type_id: Int, name: String, status: Int, config: JsValue, modified: DateTime)
  //  object Transfer extends SQLSyntaxSupport[Transfer] {
  //    override val tableName = "M_TRANSFERS"
  //    def apply(rs: WrappedResultSet): Transfer = {
  //      Transfer(rs.int("id"), rs.int("type_Id"), rs.string("name"), rs.int("status"), Json.parse(rs.string("config")), rs.jodaDateTime("modified"))
  //    }
  //  }
  case class TransferList(id: Int, type_id: Int, name: String)
  object TransferList extends SQLSyntaxSupport[Transfer] {
    override val tableName = "M_TRANSFERS"
    def apply(rs: WrappedResultSet): TransferList = {
      TransferList(rs.int("id"), rs.int("type_Id"), rs.string("name"))
    }
  }

  case class TransferJson(id: Int, type_id: Int, name: String, status: Int, config: JsObject, user_group: String, modified: Option[DateTime])
  object TransferJson {
    implicit def jsonTransferWrites: Writes[TransferJson] = Json.writes[TransferJson]
    implicit def jsonTransferReads: Reads[TransferJson] = Json.reads[TransferJson]
    implicit def jsonJodaDateTimeWrites: Writes[DateTime] = JodaWrites.jodaDateWrites("yyyy-MM-dd HH:mm:ss")
    implicit def jsonJodaDateTimeReads: Reads[DateTime] = JodaReads.jodaDateReads("yyyy-MM-dd HH:mm:ss")
  }

  case class TransferListJson(type_id: Int, name: String)
  object TransferListJson {
    implicit def jsonTransferListWrites: Writes[TransferListJson] = Json.writes[TransferListJson]
    implicit def jsonTransferListReads: Reads[TransferListJson] = Json.reads[TransferListJson]
  }

  def getTransfer(transferType: Int): List[Transfer] = {
    DB localTx { implicit l =>
      sql"SELECT ID,TYPE_ID,NAME,STATUS,CONFIG,USER_GROUP,MODIFIED FROM M_TRANSFERS WHERE TYPE_ID=$transferType"
        .map(rs => Transfer(rs)).list.apply()
      //.map(rs => Transfer(rs)).single.apply()
    }
  }

  def getTransfetList: List[Transfer] = {
    DB localTx { implicit l =>
      sql"SELECT ID,TYPE_ID,NAME,STATUS,CONFIG,USER_GROUP,MODIFIED FROM M_TRANSFERS"
        .map(rs => Transfer(rs)).list.apply()
    }
  }

  def getTransferListJson: JsValue = {
    DB localTx { implicit l =>
      val transferList = sql"SELECT ID,TYPE_ID,NAME FROM M_TRANSFERS"
        .map(rs => TransferList(rs)).list.apply
      val transferListJson = transferList.map(t => { TransferListJson(t.type_id, t.name) })
      Json.toJson(transferListJson)
    }
  }

  def save(type_id: Int, name: String, status: Int, config: String): Int = {
    DB localTx { implicit l =>
      sql"""
      INSERT INTO M_TRANSFES
      (TYPE_ID,NAME,STATUS,CONFIG,CREATED,MODIFIED)
      VALUES ($type_id,$name,$status,$config,NOW(),NOW())"""
        .update.apply()
    }
  }

  def update(id: Int, type_id: Int, name: String, status: Int, config: String): Int = {
    DB localTx { implicit l =>
      sql"""
      UPDATE M_TRANSFERS
      SET TYPE_ID=$type_id, NAME=$name, STATUS=$status, CONFIG=$config, MODIFIED=NOW()
        WHERE ID = $id"""
        .update.apply()
    }
  }

  def updateByUserIdentity(identity: User, type_id: Int, config: String): Int = {
    DB localTx { implicit l =>
      sql"""
      UPDATE M_TRANSFERS
      SET CONFIG=$config, MODIFIED_USER=${identity.userID.toString}, MODIFIED=NOW()
        WHERE TYPE_ID=$type_id AND USER_GROUP = ${identity.group}"""
        .update.apply()
    }
  }

}

object TransfersDAO {

}