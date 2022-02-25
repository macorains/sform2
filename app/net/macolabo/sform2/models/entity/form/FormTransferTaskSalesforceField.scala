package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskSalesforceField(
                                            id: BigInt,
                                            form_transfer_task_salesforce_id: BigInt,
                                            form_column_id: String,
                                            field_name: String,
                                            user_group: String,
                                            created_user: String,
                                            modified_user: String,
                                            created: ZonedDateTime,
                                            modified: ZonedDateTime
                                          ){
}

object FormTransferTaskSalesforceField extends SQLSyntaxSupport[FormTransferTaskSalesforceField] {
  override val tableName = "d_form_transfer_task_salesforce_field"

  def apply(rs: WrappedResultSet): FormTransferTaskSalesforceField = {
    FormTransferTaskSalesforceField(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_salesforce_id"),
      rs.string("form_column_id"),
      rs.string("field_name"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}