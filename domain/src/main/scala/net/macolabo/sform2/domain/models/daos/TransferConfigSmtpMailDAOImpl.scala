package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSmtpMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSmtpMail.autoSession
import scalikejdbc._

class TransferConfigSmtpMailDAOImpl extends TransferConfigSmtpMailDAO {

  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSmtpMail] = {
    val f = TransferConfigSmtpMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.smtp_host,
        f.smtp_port,
        f.smtp_user,
        f.from_address,
        f.smtp_password,
        f.iv_smtp_password,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigSmtpMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfigSmtpMail(rs)).list().apply().headOption
  }

  def getList(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigSmtpMail] = {
    val f = TransferConfigSmtpMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.smtp_host,
        f.smtp_port,
        f.smtp_user,
        f.from_address,
        f.smtp_password,
        f.iv_smtp_password,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfigSmtpMail as f)
        .where
        .eq(f.transfer_config_id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfigSmtpMail(rs)).list().apply()
  }

  def create(transferConfigSmtpMail: TransferConfigSmtpMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSmtpMail.column
      insert.into(TransferConfigSmtpMail).namedValues(
        c.transfer_config_id -> transferConfigSmtpMail.transfer_config_id,
        c.smtp_host          -> transferConfigSmtpMail.smtp_host,
        c.smtp_port          -> transferConfigSmtpMail.smtp_port,
        c.smtp_user          -> transferConfigSmtpMail.smtp_user,
        c.from_address       -> transferConfigSmtpMail.from_address,
        c.smtp_password      -> transferConfigSmtpMail.smtp_password,
        c.iv_smtp_password   -> transferConfigSmtpMail.iv_smtp_password,
        c.user_group         -> transferConfigSmtpMail.user_group,
        c.created_user       -> transferConfigSmtpMail.created_user,
        c.modified_user      -> transferConfigSmtpMail.modified_user,
        c.created            -> transferConfigSmtpMail.created,
        c.modified           -> transferConfigSmtpMail.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def save(transferConfigSmtpMail: TransferConfigSmtpMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigSmtpMail.column
      update(TransferConfigSmtpMail).set(
        c.transfer_config_id -> transferConfigSmtpMail.transfer_config_id,
        c.smtp_host          -> transferConfigSmtpMail.smtp_host,
        c.smtp_port          -> transferConfigSmtpMail.smtp_port,
        c.smtp_user          -> transferConfigSmtpMail.smtp_user,
        c.from_address       -> transferConfigSmtpMail.from_address,
        c.smtp_password      -> transferConfigSmtpMail.smtp_password,
        c.iv_smtp_password   -> transferConfigSmtpMail.iv_smtp_password,
        c.user_group         -> transferConfigSmtpMail.user_group,
        c.created_user       -> transferConfigSmtpMail.created_user,
        c.modified_user      -> transferConfigSmtpMail.modified_user,
        c.created            -> transferConfigSmtpMail.created,
        c.modified           -> transferConfigSmtpMail.modified
      ).where.eq(c.id, transferConfigSmtpMail.id)
    }.update().apply()
    transferConfigSmtpMail.id
  }

  def delete(userGroup: String, id: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigSmtpMail.column
      deleteFrom(TransferConfigSmtpMail)
        .where
        .eq(c.id, id)
        .and
        .eq(c.user_group, userGroup)
    }.update().apply()
  }
}
