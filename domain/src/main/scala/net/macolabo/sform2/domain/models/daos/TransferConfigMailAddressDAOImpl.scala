package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress
import scalikejdbc._

class TransferConfigMailAddressDAOImpl extends TransferConfigMailAddressDAO {
  def get(transferConfigMailAddressId: BigInt)(implicit session: DBSession): Option[TransferConfigMailAddress] = {
    val t = TransferConfigMailAddress.syntax("t")
    withSQL(
      select(
        t.id,
        t.transfer_config_mail_id,
        t.address_index,
        t.name,
        t.address,
        t.user_group,
        t.created_user,
        t.modified_user,
        t.created,
        t.modified
      )
        .from(TransferConfigMailAddress as t)
        .where
        .eq(t.id, transferConfigMailAddressId)
    ).map(rs=>TransferConfigMailAddress(rs)).single().apply()
  }
}
