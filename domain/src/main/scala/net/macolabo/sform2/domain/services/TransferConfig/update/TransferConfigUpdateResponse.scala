package net.macolabo.sform2.domain.services.TransferConfig.update

import play.api.libs.json.{Json, Writes}

case class TransferUpdateTransferConfigResponse(
                                                 id: BigInt
                                               )

trait TransferUpdateTransferConfigResponseJson {
  implicit val TransferUpdateTransferConfigResponseWrites: Writes[TransferUpdateTransferConfigResponse] = (transferUpdateTransferConfigResponse:TransferUpdateTransferConfigResponse) => Json.obj(
    "id" -> transferUpdateTransferConfigResponse.id
  )
}
