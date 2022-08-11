package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class TransferGetTransferConfigSelectList(
                                                id: BigInt,
                                                name: String,
                                                type_code: String
                                              )

trait TransferGetTransferConfigSelectListJson {
  implicit val TransferGetTransferConfigSelectListWrites: Writes[TransferGetTransferConfigSelectList] = (transferGetTransferConfigSelectList: TransferGetTransferConfigSelectList) => Json.obj(
    "id" -> transferGetTransferConfigSelectList.id,
    "name" -> transferGetTransferConfigSelectList.name,
    "type_code" -> transferGetTransferConfigSelectList.type_code
  )
  implicit val TransferGetTransferConfigSelectListReads: Reads[TransferGetTransferConfigSelectList] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "type_Code").read[String]
  )(TransferGetTransferConfigSelectList.apply _)
}
