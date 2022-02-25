package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._
case class FormTransferTaskCondition(
                                      id: BigInt,
                                      form_transfer_task_id: BigInt,
                                      form_id: BigInt,
                                      form_col_id: BigInt,
                                      operator: String,
                                      cond_value: String,
                                      user_group: String,
                                      created_user: String,
                                      modified_user: String,
                                      created: ZonedDateTime,
                                      modified: ZonedDateTime
                                    ){
}

object FormTransferTaskCondition extends SQLSyntaxSupport[FormTransferTaskCondition] {
  override val tableName = "d_form_transfer_task_condition"

  def apply(rs: WrappedResultSet): FormTransferTaskCondition = {
    FormTransferTaskCondition(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.bigInt("form_id"),
      rs.bigInt("form_col_id"),
      rs.string("operator"),
      rs.string("cond_value"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}