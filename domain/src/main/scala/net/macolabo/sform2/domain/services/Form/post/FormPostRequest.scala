package net.macolabo.sform2.domain.services.Form.post

import play.api.libs.json.{Format, Json, JsValue}

/**
 * フォーム送信データ登録要求クラス
 * @param hashed_form_id  フォームのhashed_id
 * @param postdata フォーム送信データ
 * @param cache_id キャッシュID
 */
case class FormPostRequest(hashed_form_id: String, postdata: Option[JsValue], cache_id: Option[String])

object FormPostRequest {
  implicit val format: Format[FormPostRequest] = Json.format[FormPostRequest]
}
