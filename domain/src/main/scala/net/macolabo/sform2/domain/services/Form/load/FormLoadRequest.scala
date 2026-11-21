package net.macolabo.sform2.domain.services.Form.load

import play.api.libs.json.{Format, Json}

/**
 * フォーム呼び出し要求クラス
 * @param hashed_form_id フォームのhashed_id
 * @param receiver_path フォームレシーバ―のURI
 * @param cache_id キャッシュID
 */
case class FormLoadRequest(
                            hashed_form_id:String,
                            receiver_path:String,
                            cache_id: Option[String]
                          )

object FormLoadRequest {
  implicit val format: Format[FormLoadRequest] = Json.format[FormLoadRequest]
}
