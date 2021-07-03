package net.macolabo.sform2.services.Form.list

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class FormResponse(
                            id: BigInt,
                            name: String,
                            form_index: Int,
                            title: String,
                            status: Int,
                            hashed_id: String,
                          )

case class FormListResponse(
                             forms: List[FormResponse],
                             data_count: Int
                           )

trait FormListResponseJson {
  implicit val FormResponseWrites: Writes[FormResponse] = (formResponse: FormResponse) => Json.obj(
    "id" -> formResponse.id,
    "name" -> formResponse.name,
    "form_index" -> formResponse.form_index,
    "title" -> formResponse.title,
    "status" -> formResponse.status,
    "hashed_id" -> formResponse.hashed_id
  )
  implicit val FormResponseReads: Reads[FormResponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "hashed_id").read[String]
    )(FormResponse.apply _)
  implicit val FormListResponseWrites: Writes[FormListResponse] = (formListResponse: FormListResponse) => Json.obj(
    "forms" -> formListResponse.forms,
    "data_count" -> formListResponse.data_count
  )
  implicit val FormListResponseReads: Reads[FormListResponse] = (
    (JsPath \ "forms").read[List[FormResponse]] ~
      (JsPath \ "data_count").read[Int]
    )(FormListResponse.apply _)
