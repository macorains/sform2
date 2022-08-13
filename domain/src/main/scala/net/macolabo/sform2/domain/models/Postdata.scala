package net.macolabo.sform2.domain.models
import scalikejdbc._

import java.time.{LocalDateTime, ZonedDateTime}

case class Postdata(id: Int, form_hashed_id: String, postdata: String, user_group: String, created: ZonedDateTime, modified:ZonedDateTime)
object Postdata extends SQLSyntaxSupport[Postdata] {
  override val tableName = "d_postdata"
  def apply(rs: WrappedResultSet): Postdata = {
    Postdata(
      rs.int("id"),
      rs.string("form_hashed_id"),
      rs.string("postdata"),
      rs.string("user_group"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}


