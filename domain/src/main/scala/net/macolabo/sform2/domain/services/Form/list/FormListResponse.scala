package net.macolabo.sform2.domain.services.Form.list

import play.api.libs.json.{Format, Json}

case class FormResponse(
                            id: BigInt,
                            name: String,
                            form_index: Int,
                            title: String,
                            status: Int,
                            hashed_id: String,
                          )

object FormResponse {
  implicit val format: Format[FormResponse] = Json.format[FormResponse]
}

case class FormListResponse(
                             forms: List[FormResponse],
                             data_count: Int
                           )

object FormListResponse {
  implicit val format: Format[FormListResponse] = Json.format[FormListResponse]
}
