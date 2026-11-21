package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigSmtpMail(
  id: BigInt,
  transfer_config_id: BigInt,
  smtp_host: String,
  smtp_port: Int,
  smtp_user: String,
  from_address: String,
  smtp_password: String,
  iv_smtp_password: String,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object TransferConfigSmtpMail extends SQLSyntaxSupport[TransferConfigSmtpMail] {
  override val tableName = "d_transfer_config_smtp_mail"
  def apply(rs: WrappedResultSet): TransferConfigSmtpMail = {
    TransferConfigSmtpMail(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.string("smtp_host"),
      rs.int("smtp_port"),
      rs.string("smtp_user"),
      rs.string("from_address"),
      rs.string("smtp_password"),
      rs.string("iv_smtp_password"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
