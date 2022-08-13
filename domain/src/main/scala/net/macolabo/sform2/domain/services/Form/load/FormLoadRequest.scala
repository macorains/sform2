package net.macolabo.sform2.domain.services.Form.load

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

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

trait FormLoadRequestJson {
  implicit def jsonFormLoadRequestReads: Reads[FormLoadRequest] = (
    (JsPath \ "hashed_form_id").read[String] ~
      (JsPath \ "receiver_path").read[String] ~
      (JsPath \ "cache_id").readNullable[String]
    )(FormLoadRequest.apply _)
  implicit def jsonFormLoadRequestWrites: Writes[FormLoadRequest] = (formLoadRequest: FormLoadRequest) => Json.obj(
    "hashed_form_id" -> formLoadRequest.hashed_form_id,
    "receiver_path" -> formLoadRequest.receiver_path,
    "cache_id" -> formLoadRequest.cache_id
  )
}
