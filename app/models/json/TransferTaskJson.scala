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
  implicit val jsonTransferTaskWrites: Writes[TransferTaskEntry] = (transferTaskEntry: TransferTaskEntry) => Json.obj(
    "id" -> transferTaskEntry.id,
    "transfer_type_id" -> transferTaskEntry.transfer_type_id,
    "name" -> transferTaskEntry.name,
    "status" -> transferTaskEntry.status,
    "config" -> transferTaskEntry.config,
    "created" -> transferTaskEntry.created,
    "modified" -> transferTaskEntry.modified,
    "del_flg" -> transferTaskEntry.del_flg
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