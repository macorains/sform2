package net.macolabo.sform2.domain.services.ApiToken.insert

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, OFormat, Reads}

case class ApiTokenInsertRequest(
                                expiry_days: Long
                                )

object ApiTokenInsertRequest {
  implicit val format: OFormat[ApiTokenInsertRequest] = Json.format[ApiTokenInsertRequest]
}
