package net.macolabo.sform2.domain.services.Form.post

import play.api.libs.json.{JsPath, JsValue, Reads}
import play.api.libs.functional.syntax._

/**
 * フォーム送信データ登録要求クラス
 * @param hashed_form_id  フォームのhashed_id
 * @param postdata フォーム送信データ
 * @param cache_id キャッシュID
 */
case class FormPostRequest(hashed_form_id: String, postdata: Option[JsValue], cache_id: Option[String])

trait FormPostRequestJson {
  implicit def jsonFormSaveRequestReads: Reads[FormPostRequest] = (
    (JsPath \ "hashed_form_id").read[String] ~
      (JsPath \ "postdata").readNullable[JsValue] ~
      (JsPath \ "cache_id").readNullable[String]
    )(FormPostRequest.apply _)
}
