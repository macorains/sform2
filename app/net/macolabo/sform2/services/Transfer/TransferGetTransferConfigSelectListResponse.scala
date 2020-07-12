package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class TransferGetTransferConfigSelectList(
                                                id: Int,
                                                name: String
                                              )

trait TransferGetTransferConfigSelectListJson {
  implicit val transferGetTransferConfigSelectListWrites: Writes[TransferGetTransferConfigSelectList] = (transferGetTransferConfigSelectList: TransferGetTransferConfigSelectList) => Json.obj(
    "id" -> transferGetTransferConfigSelectList.id,
    "name" -> transferGetTransferConfigSelectList.name
  )
  implicit val TransferGetTransferConfigSelectListReads: Reads[TransferGetTransferConfigSelectList] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "name").read[String]
  )(TransferGetTransferConfigSelectList.apply _)
}