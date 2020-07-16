package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class TransferGetTransferConfigList(
                                          id: Int,
                                          type_code: String,
                                          config_index: Int,
                                          name: String,
                                          status: Int
                                        )

trait TransferGetTransferConfigListJson {
  implicit val TransferGetTransferConfigListWrites: Writes[TransferGetTransferConfigList] = (transferGetTransferConfigList: TransferGetTransferConfigList) => Json.obj(
    "id" -> transferGetTransferConfigList.id,
  "type_code" -> transferGetTransferConfigList.type_code,
    "config_index" -> transferGetTransferConfigList.config_index,
    "name" -> transferGetTransferConfigList.name,
    "status" -> transferGetTransferConfigList.status
  )
  implicit val transferGetTransferConfigListReads: Reads[TransferGetTransferConfigList] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "type_code").read[String] ~
      (JsPath \ "config_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "status").read[Int]
    )(TransferGetTransferConfigList.apply _)
}

