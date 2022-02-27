package net.macolabo.sform2.models.entity

import play.api.libs.json.{JsValue, Json}
import scalikejdbc._

import java.time.ZonedDateTime

case class Postdata(
                     postdata_id: Int,
                     form_hashed_id: String,
                     postdata: JsValue,
                     user_group: String,
                     created_user: String,
                     created: ZonedDateTime,
                     modified_user: String,
                     modified: ZonedDateTime
                   )

object Postdata extends SQLSyntaxSupport[Postdata] {
  override val tableName = "d_postdata"
  def apply(rs: WrappedResultSet): Postdata = {
    Postdata(
      rs.int("postdata_id"),
      rs.string("form_hashed_id"),
      Json.parse(rs.string("postdata")),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.dateTime("created"),
      rs.string("modified_user"),
      rs.dateTime("modified")
    )
  }
}
