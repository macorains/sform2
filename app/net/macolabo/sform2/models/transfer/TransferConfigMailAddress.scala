package net.macolabo.sform2.models.transfer

import java.time.ZonedDateTime

import scalikejdbc._

case class TransferConfigMailAddress(
                                    id: Int,
                                    transfer_config_mail_id: Int,
                                    address_index: Int,
                                    name: String,
                                    address: String,
                                    user_group: String,
                                    created_user: String,
                                    modified_user: String,
                                    created: ZonedDateTime,
                                    modified: ZonedDateTime
                                    )

object TransferConfigMailAddress extends SQLSyntaxSupport[TransferConfigMailAddress] {
  override val tableName = "D_TRANSFER_CONFIG_MAIL_ADDRESS"
  def apply(rs: WrappedResultSet): TransferConfigMailAddress = {
    TransferConfigMailAddress(
      rs.int("id"),
      rs.int("transfer_config_mail_id"),
      rs.int("address_index"),
      rs.string("name"),
      rs.string("address"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  def getList(userGroup: String, transferConfigMailId: Int)(implicit session: DBSession = autoSession): List[TransferConfigMailAddress] = {
    val f = TransferConfigMailAddress.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_mail_id,
        f.address_index,
        f.name,
        f.address,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigMailAddress as f)
        .where
        .eq(f.transfer_config_mail_id, transferConfigMailId)
        .and
        .eq(f.user_group, userGroup)
        .orderBy(f.address_index)
    ).map(rs => TransferConfigMailAddress(rs)).list().apply()
  }
}