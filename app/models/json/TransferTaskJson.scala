package models.json

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class TransferTaskEntry(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsValue,
  created: Option[String], modified: Option[String], del_flg: Int)

object TransferTaskEntry {
  def apply(id: Int, transfer_type_id: Int, name: String, status: Int, config: JsObject, created: Option[String], modified: Option[String], del_flg: Int): TransferTaskEntry = {
    TransferTaskEntry(id, transfer_type_id, name, status, config, created, modified, del_flg)
  }
}

trait TransferTaskJson {
  implicit val jsonTransferTaskWrites: Writes[TransferTaskEntry] = (transferTask: TransferTaskEntry) => Json.obj(
    "id" -> transferTask.id,
    "transfer_type_id" -> transferTask.transfer_type_id,
    "name" -> transferTask.name,
    "status" -> transferTask.status,
    "config" -> transferTask.config,
    "created" -> transferTask.created,
    "modified" -> transferTask.modified,
    "del_flg" -> transferTask.del_flg
  )

  implicit val jsonTransferTaskReads: Reads[TransferTaskEntry] = (
    (JsPath \ "id").read[Int] ~
    (JsPath \ "transfer_type_id").read[Int] ~
    (JsPath \ "name").read[String] ~
    (JsPath \ "status").read[Int] ~
    (JsPath \ "config").read[JsObject] ~
    (JsPath \ "created").readNullable[String] ~
    (JsPath \ "modified").readNullable[String] ~
    (JsPath \ "del_flg").read[Int]
  )(TransferTaskEntry.apply _)
}