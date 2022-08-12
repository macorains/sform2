package net.macolabo.sform.api.models.daos

import play.api.libs.json.JsValue
import scalikejdbc.DBSession

trait PostdataDAO {
  /**
  * フォーム送信データ保存
  * @param form_id フォームID
  * @param dt Ajaxからの送信データ
  * @return
  */
  def save(form_id: String, dt: JsValue)(implicit session: DBSession): Long

}
