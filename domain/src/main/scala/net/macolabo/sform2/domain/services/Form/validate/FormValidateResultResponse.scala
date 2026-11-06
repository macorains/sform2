package net.macolabo.sform2.domain.services.Form.validate

import play.api.libs.json.{Format, Json}

case class FormValidateResultResponse(
                                     cache_id: Option[String],
                                     validate_result: Map[String,String]
                                     )

object FormValidateResultResponse {
  implicit val format: Format[FormValidateResultResponse] = Json.format[FormValidateResultResponse]
}
