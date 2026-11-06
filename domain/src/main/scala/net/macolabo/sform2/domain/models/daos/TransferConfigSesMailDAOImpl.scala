package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSesMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSesMail.autoSession
import scalikejdbc._

class TransferConfigSesMailDAOImpl extends TransferConfigSesMailDAO {

  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSesMail] = {
    val f = TransferConfigSesMail.syntax("f")
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
        .from(TransferConfigSesMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>TransferConfigSesMail(rs)).single().apply()
  }

  def create(transferConfigSesMail: TransferConfigSesMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSesMail.column
      insert.into(TransferConfigSesMail).namedValues(
        c.transfer_config_id -> transferConfigSesMail.transfer_config_id,
        c.use_cc -> transferConfigSesMail.use_cc,
        c.use_bcc -> transferConfigSesMail.use_bcc,
        c.use_replyto -> transferConfigSesMail.use_replyto,
        c.user_group -> transferConfigSesMail.user_group,
        c.created_user -> transferConfigSesMail.created_user,
        c.modified_user -> transferConfigSesMail.modified_user,
        c.created -> transferConfigSesMail.created,
        c.modified -> transferConfigSesMail.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def save(transferConfigSesMail: TransferConfigSesMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSesMail.column
      update(TransferConfigSesMail).set(
        c.transfer_config_id -> transferConfigSesMail.transfer_config_id,
        c.use_cc -> transferConfigSesMail.use_cc,
        c.use_bcc -> transferConfigSesMail.use_bcc,
        c.use_replyto -> transferConfigSesMail.use_replyto,
        c.user_group -> transferConfigSesMail.user_group,
        c.created_user -> transferConfigSesMail.created_user,
        c.modified_user -> transferConfigSesMail.modified_user,
        c.created -> transferConfigSesMail.created,
        c.modified -> transferConfigSesMail.modified
      ).where.eq(c.id, transferConfigSesMail.id)
    }.update().apply()
    transferConfigSesMail.id
  }

  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigSesMail.column
      deleteFrom(TransferConfigSesMail)
        .where
        .eq(c.id, transferConfigId)
    }.update().apply()
  }
}
