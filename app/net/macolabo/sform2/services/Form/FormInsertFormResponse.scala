package net.macolabo.sform2.services.Form

import play.api.libs.json.{Json, Writes}

case class FormInsertFormResponse(
                                 id: Int,
                                 hashed_id: String
                                 ) {
}

trait FormInsertFormResponseJson {
  implicit val FormInsertFormResponseWrites: Writes[FormInsertFormResponse] = (formInsertFormResponse: FormInsertFormResponse) => Json.obj(
    "id"-> formInsertFormResponse.id,
    "hashed_id" -> formInsertFormResponse.hashed_id
  )
}


