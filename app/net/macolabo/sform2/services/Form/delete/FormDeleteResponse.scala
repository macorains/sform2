package net.macolabo.sform2.services.Form.delete

import play.api.libs.json.{Json, Writes}

case class FormDeleteResponse(
                               result: Int
                             )

trait FormDeleteResponseJson {
  implicit val FormDeleteResponseWrites: Writes[FormDeleteResponse] = (formDeleteResponse: FormDeleteResponse) => Json.obj(
    "result" -> formDeleteResponse.result
  )
}