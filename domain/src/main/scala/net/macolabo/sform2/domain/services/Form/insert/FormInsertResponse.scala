package net.macolabo.sform2.domain.services.Form.insert

import play.api.libs.json.{Format, Json}

case class FormInsertResponse(
                               id: BigInt,
                               hashed_id: String
                             )

object FormInsertResponse {
  implicit val format: Format[FormInsertResponse] = Json.format[FormInsertResponse]
}
