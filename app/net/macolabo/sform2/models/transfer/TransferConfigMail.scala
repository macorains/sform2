package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigMail(
                             id: Int,
                             transfer_config_id: Int,
                             subject: String,
                             reply_to: String,
                             from_address: String,
                             to_address: String,
                             cc_address: String,
                             bcc_address: String,
                             body: String,
                             user_group: String,
                             created_user: String,
                             modified_user: String,
                             created: ZonedDateTime,
                             modified: ZonedDateTime
                             )

object TransferConfigMail extends SQLSyntaxSupport[TransferConfigMail] {
  override val tableName = "D_TRANSFER_CONFIG_MAIL"
  def apply(rs:WrappedResultSet): TransferConfigMail = {
    TransferConfigMail(
      rs.int("id"),
      rs.int("transfer_config_id"),
      rs.string("subject"),
      rs.string("reply_to"),
      rs.string("from_address"),
      rs.string("to_address"),
      rs.string("cc_address"),
      rs.string("bcc_address"),
      rs.string("body"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  def get(userGroup: String, transferConfigId: Int)(implicit session: DBSession = autoSession): Option[TransferConfigMail] = {
    val f = TransferConfigMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.subject,
        f.reply_to,
        f.from_address,
        f.to_address,
        f.cc_address,
        f.bcc_address,
        f.body
      )
        .from(TransferConfigMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigMail(rs)).single().apply()
  }
}
