package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{Json, Writes}

case class TransferUpdateTransferConfigResponse(
                                                 id: Int
                                               )

trait TransferUpdateTransferConfigResponseJson {
  implicit val TransferUpdateTransferConfigResponseWrites: Writes[TransferUpdateTransferConfigResponse] = (transferUpdateTransferConfigResponse:TransferUpdateTransferConfigResponse) => Json.obj(
    "id" -> transferUpdateTransferConfigResponse.id
  )
}