package net.macolabo.sform2.domain.services.Form.update

import play.api.libs.json.{Json, Writes}

case class FormUpdateResponse(
                               id: BigInt
                             )

trait FormUpdateResponseJson {
  implicit val FormUpdateResponseWrites: Writes[FormUpdateResponse] = (formUpdateResponse: FormUpdateResponse) => Json.obj(
    "id"-> formUpdateResponse.id
  )
}
