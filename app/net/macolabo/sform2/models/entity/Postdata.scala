package net.macolabo.sform2.models.entity

import play.api.libs.json.{ JsValue, Json }
import scalikejdbc._

case class Postdata(postdata_id: Int, form_hashed_id: String, postdata: JsValue)
object Postdata extends SQLSyntaxSupport[Postdata] {
  override val tableName = "D_POSTDATA"
  def apply(rs: WrappedResultSet): Postdata = {
    Postdata(rs.int("postdata_id"), rs.string("form_hashed_id"), Json.parse(rs.string("postdata")))
  }
}
