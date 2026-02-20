package net.macolabo.sform2.domain.services.Form.update

import play.api.libs.json.{Format, Json}

case class FormUpdateResponse(
                               id: BigInt
                             )

object FormUpdateResponse {
  implicit val format: Format[FormUpdateResponse] = Json.format[FormUpdateResponse]
}
