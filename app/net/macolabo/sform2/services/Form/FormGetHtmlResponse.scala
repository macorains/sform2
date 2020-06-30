package net.macolabo.sform2.services.Form

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

/**
  * HTML取得のレスポンス
  *
  * @param id
  * @param hashed_form_id
  * @param html
  */
case class FormGetHtmlResponse(
                              id: Long,
                              hashed_form_id: String,
                              html: String
                              ) {

}

trait FormGetHtmlResponseJson {
  implicit val FormGetHtmlResponseWrites: Writes[FormGetHtmlResponse] = (formGetHtmlResponse: FormGetHtmlResponse) => Json.obj(
  "id" -> formGetHtmlResponse.id,
    "hashed_form_id" -> formGetHtmlResponse.hashed_form_id,
    "html" -> formGetHtmlResponse.html
  )
  implicit val FormGetHtmlResponseReads: Reads[FormGetHtmlResponse] = (
    (JsPath \ "id").read[Long] ~
      (JsPath \ "hashed_form_id").read[String] ~
      (JsPath \ "html").read[String]
  )(FormGetHtmlResponse.apply _)

}