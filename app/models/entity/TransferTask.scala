package models.entity

import play.api.libs.json.{ JsValue, Json }
import scalikejdbc._

case class TransferTask(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsValue,
  created: Option[String], modified: Option[String])

object TransferTask extends SQLSyntaxSupport[TransferTask] {
  override val tableName = "D_TRANSFER_TASKS"

  def apply(rs: WrappedResultSet): TransferTask = {
    TransferTask(rs.int("id"), rs.int("transfer_type_id"), rs.string("name"), rs.int("status"),
      Json.parse(rs.string("config")), rs.stringOpt("created"), rs.stringOpt("modified"))
  }

}
