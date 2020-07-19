package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigMail(
                             id: Int,
                             transfer_config_id: Int,
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
  override val tableName = "D_TRANSFER_CONFIG_MAIL"
  def apply(rs:WrappedResultSet): TransferConfigMail = {
    TransferConfigMail(
      rs.int("id"),
      rs.int("transfer_config_id"),
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

  def get(userGroup: String, transferConfigId: Int)(implicit session: DBSession = autoSession): Option[TransferConfigMail] = {
    val f = TransferConfigMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.use_cc,
        f.use_bcc,
        f.use_replyto,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigMail(rs)).single().apply()
  }
}
