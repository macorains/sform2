package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class TransferUpdateTransferConfigResponse(
                                                 id: BigInt
                                               )

object TransferUpdateTransferConfigResponse {
  implicit val format: Format[TransferUpdateTransferConfigResponse] = Json.format[TransferUpdateTransferConfigResponse]
}
