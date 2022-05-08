package net.macolabo.sform2.services.ApiToken.insert

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}

case class ApiTokenInsertRequest(
                                token: String,
                                expiry_days: Long
                                )

trait ApiTokenInsertRequestJson {
  implicit val ApiTokenInsertRequestReads: Reads[ApiTokenInsertRequest] = (
      (JsPath \ "token").read[String] ~
      (JsPath \ "expiry_days").read[Long]
  )(ApiTokenInsertRequest.apply _)
}
