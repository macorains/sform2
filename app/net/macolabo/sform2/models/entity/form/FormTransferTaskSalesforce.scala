package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskSalesforce(
                                       id: BigInt,
                                       form_transfer_task_id: BigInt,
                                       object_name: String,
                                       user_group: String,
                                       created_user: String,
                                       modified_user: String,
                                       created: ZonedDateTime,
                                       modified: ZonedDateTime
                                     ){
}

object FormTransferTaskSalesforce extends SQLSyntaxSupport[FormTransferTaskSalesforce] {
  override val tableName = "D_FORM_TRANSFER_TASK_SALESFORCE"

  def apply(rs: WrappedResultSet): FormTransferTaskSalesforce = {
    FormTransferTaskSalesforce(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.string("object_name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}