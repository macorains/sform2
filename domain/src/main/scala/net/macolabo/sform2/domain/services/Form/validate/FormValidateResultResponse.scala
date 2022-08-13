package net.macolabo.sform2.domain.services.Form.validate

import play.api.libs.json.{Json, Writes}

case class FormValidateResultResponse(
                                     cache_id: Option[String],
                                     validate_result: Map[String,String]
                                     )

trait FormValidationResultJson {
  implicit def FormValidationResultWrites: Writes[FormValidateResultResponse] = (formValidationResulteResponse: FormValidateResultResponse) => Json.obj(
    "cache_id" -> formValidationResulteResponse.cache_id,
    "validate_result" -> formValidationResulteResponse.validate_result
  )
}

