package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigSalesforce(
                                   id: Int,
                                   transfer_config_id: Int,
                                   sf_user_name: String,
                                   sf_password: String,
                                   sf_security_token: String,
                                   user_group: String,
                                   created_user: String,
                                   modified_user: String,
                                   created: ZonedDateTime,
                                   modified: ZonedDateTime
                                   )

object TransferConfigSalesforce extends SQLSyntaxSupport[TransferConfigSalesforce] {
  override val tableName = "D_TRANSFER_CONFIG_SALESFORCE"
  def apply(rs:WrappedResultSet): TransferConfigSalesforce = {
    TransferConfigSalesforce(
      rs.int("id"),
      rs.int("transfer_config_id"),
      rs.string("sf_user_name"),
      rs.string("sf_password"),
      rs.string("sf_security_token"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  def get(userGroup: String, transferConfigId: Int)(implicit session: DBSession = autoSession): Option[TransferConfigSalesforce] = {
    val f = TransferConfigSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.sf_user_name,
        f.sf_password,
        f.sf_security_token
      )
        .from(TransferConfigSalesforce as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigSalesforce(rs)).single().apply()
  }
}
