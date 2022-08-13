package net.macolabo.sform2.domain.models.entity.formtransfertask

import scalikejdbc._

import java.math.BigInteger
import java.time.ZonedDateTime

case class FormTransferTaskMail(
                                 id: BigInt,
                                 form_transfer_task_id: BigInt,
                                 from_address_id: BigInteger,
                                 to_address: Option[String],
                                 to_address_id: Option[BigInteger],
                                 to_address_field: Option[String],
                                 cc_address: Option[String],
                                 cc_address_id: Option[BigInteger],
                                 cc_address_field: Option[String],
                                 bcc_address_id: BigInteger,
                                 replyto_address_id: BigInteger,
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
      rs.stringOpt("to_address"),
      rs.bigIntOpt("to_address_id"),
      rs.stringOpt("to_address_field"),
      rs.stringOpt("cc_address"),
      rs.bigIntOpt("cc_address_id"),
      rs.stringOpt("cc_address_field"),
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
