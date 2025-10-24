package net.macolabo.sform2.domain.services.ApiToken.insert

import play.api.libs.json.{Json, OFormat}

case class ApiTokenInsertResponse(token: String)

object ApiTokenInsertResponse {
  implicit val format: OFormat[ApiTokenInsertResponse] = Json.format[ApiTokenInsertResponse]
}

