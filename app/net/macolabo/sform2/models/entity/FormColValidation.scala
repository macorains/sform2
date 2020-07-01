package net.macolabo.sform2.models.entity

import java.time.ZonedDateTime

import scalikejdbc._

case class FormColValidation(
                              id: Int,
                              form_col_id: Int,
                              form_id: Int,
                              max_value: Int,
                              min_value: Int,
                              max_length: Int,
                              min_Length: Int,
                              input_type: Int,
                              user_group: String,
                              created_user: String,
                              modified_user: String,
                              created: ZonedDateTime,
                              modified: ZonedDateTime
                            )

object FormColValidation extends SQLSyntaxSupport[FormColValidation] {
  override val tableName = "D_FORM_COL_VALIDATION"
  def apply(rs: WrappedResultSet) :FormColValidation = {
    FormColValidation(
      rs.int("id"),
      rs.int("form_col_id"),
      rs.int("form_id"),
      rs.int("max_value"),
      rs.int("min_value"),
      rs.int("max_length"),
      rs.int("min_length"),
      rs.int("input_type"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
