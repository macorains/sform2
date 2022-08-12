package net.macolabo.sform.api.models.daos

import java.util.Date

import net.macolabo.sform.api.models.Postdata
import play.api.libs.json.{JsValue, Json}
import scalikejdbc._

class PostdataDAOImpl extends PostdataDAO {
  /**
    * フォーム送信データ保存
    * @param form_id フォームID
    * @param dt Ajaxからの送信データ
    * @return
    */
  def save(form_id: String, dt: JsValue)(implicit session: DBSession): Long = {
    val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
    withSQL {
      val p = Postdata.column
      insertInto(Postdata).namedValues(
        p.form_hashed_id -> form_id,
        p.postdata -> Json.toJson(dt).toString,
        p.created -> now,
        p.modified -> now
      )
    }.updateAndReturnGeneratedKey().apply()
  }
}
