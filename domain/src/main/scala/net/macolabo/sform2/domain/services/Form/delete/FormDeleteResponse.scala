package net.macolabo.sform2.domain.services.Form.delete

import play.api.libs.json.{Format, Json}

case class FormDeleteResponse(
                               result: Int
                             )

object FormDeleteResponse {
  implicit val format: Format[FormDeleteResponse] = Json.format[FormDeleteResponse]
}
