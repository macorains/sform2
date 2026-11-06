package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class TransferGetTransferConfigListResponse(
                                          id: BigInt,
                                          type_code: String,
                                          config_index: Int,
                                          name: String,
                                          status: Int
                                        )

object TransferGetTransferConfigListResponse {
  implicit val format: Format[TransferGetTransferConfigListResponse] = Json.format[TransferGetTransferConfigListResponse]
}
