package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormColValidation(
                              id: BigInt,
                              form_col_id: BigInt,
                              form_id: BigInt,
                              max_value: Option[Int],
                              min_value: Option[Int],
                              max_length: Option[Int],
                              min_length: Option[Int],
                              input_type: Int,
                              required: Boolean,
                              user_group: String,
                              created_user: String,
                              modified_user: String,
                              created: ZonedDateTime,
                              modified: ZonedDateTime
                            ){
}

object FormColValidation extends SQLSyntaxSupport[FormColValidation] {
  override val tableName = "d_form_col_validation"

  def apply(rs: WrappedResultSet): FormColValidation = {
    FormColValidation(
      rs.bigInt("id"),
      rs.bigInt("form_col_id"),
      rs.bigInt("form_id"),
      rs.intOpt("max_value"),
      rs.intOpt("min_value"),
      rs.intOpt("max_length"),
      rs.intOpt("min_length"),
      rs.int("input_type"),
      rs.boolean("required"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}