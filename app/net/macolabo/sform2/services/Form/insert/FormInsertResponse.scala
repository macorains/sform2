package net.macolabo.sform2.services.Form.insert

import play.api.libs.json.{Json, Writes}

case class FormInsertResponse(
                               id: BigInt,
                               hashed_id: String
                             )

trait FormInsertResponseJson {
  implicit val FormInsertResponseWrites: Writes[FormInsertResponse] = (formInsertResponse: FormInsertResponse) => Json.obj(
    "id"-> formInsertResponse.id,
    "hashed_id" -> formInsertResponse.hashed_id
  )
}
