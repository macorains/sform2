package net.macolabo.sform2.domain.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

case class FormTransferTaskMail(
                                 id: BigInt,
                                 form_transfer_task_id: BigInt,
                                 from_address_id: BigInt,
                                 to_address: String,
                                 cc_address: String,
                                 bcc_address_id: BigInt,
                                 replyto_address_id: BigInt,
                                 subject: String,
                                 body: String,
                                 user_group: String,
                                 created_user: String,
                                 modified_user: String,
                                 created: ZonedDateTime,
                                 modified: ZonedDateTime
                               ){
}

object FormTransferTaskMail extends SQLSyntaxSupport[FormTransferTaskMail] {
  override val tableName = "d_form_transfer_task_mail"

  def apply(rs: WrappedResultSet): FormTransferTaskMail = {
    FormTransferTaskMail(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.bigInt("from_address_id"),
      rs.string("to_address"),
      rs.string("cc_address"),
      rs.bigInt("bcc_address_id"),
      rs.bigInt("replyto_address_id"),
      rs.string("subject"),
      rs.string("body"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
