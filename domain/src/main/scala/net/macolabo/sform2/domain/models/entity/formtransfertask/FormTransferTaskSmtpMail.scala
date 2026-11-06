package net.macolabo.sform2.domain.models.entity.formtransfertask

import scalikejdbc._

import java.time.ZonedDateTime

case class FormTransferTaskSmtpMail(
 id: BigInt,
 form_transfer_task_id: BigInt,
 subject: String,
 body: String,
 to_address: Option[String],
 to_address_field: Option[String],
 cc_address: Option[String],
 bcc_address: Option[String],
 user_group: String,
 created_user: String,
 modified_user: String,
 created: ZonedDateTime,
 modified: ZonedDateTime
)

object FormTransferTaskSmtpMail extends SQLSyntaxSupport[FormTransferTaskSmtpMail] {
  override val tableName = "d_form_transfer_task_smtp_mail"

  def apply(rs: WrappedResultSet): FormTransferTaskSmtpMail = {
    FormTransferTaskSmtpMail(
      rs.bigInt("id"),
      rs.bigInt("form_transfer_task_id"),
      rs.string("subject"),
      rs.string("body"),
      rs.stringOpt("to_address"),
      rs.stringOpt("to_address_field"),
      rs.stringOpt("cc_address"),
      rs.stringOpt("bcc_address"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
