package net.macolabo.sform2.domain.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferConfigMail(
  id: BigInt,
  transfer_config_id: BigInt,
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  user_group: String,
  created_user: String,
  modified_user: String,
  created: ZonedDateTime,
  modified: ZonedDateTime
)

object TransferConfigMail extends SQLSyntaxSupport[TransferConfigMail] {
  override val tableName = "d_transfer_config_mail"
  def apply(rs:WrappedResultSet): TransferConfigMail = {
    TransferConfigMail(
      rs.bigInt("id"),
      rs.bigInt("transfer_config_id"),
      rs.boolean("use_cc"),
      rs.boolean("use_bcc"),
      rs.boolean("use_replyto"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
