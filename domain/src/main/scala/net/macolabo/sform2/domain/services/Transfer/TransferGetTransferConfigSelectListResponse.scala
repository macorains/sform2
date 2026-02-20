package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class TransferGetTransferConfigSelectList(
                                                id: BigInt,
                                                name: String,
                                                type_code: String
                                              )

object TransferGetTransferConfigSelectList {
  implicit val format: Format[TransferGetTransferConfigSelectList] = Json.format[TransferGetTransferConfigSelectList]
}
