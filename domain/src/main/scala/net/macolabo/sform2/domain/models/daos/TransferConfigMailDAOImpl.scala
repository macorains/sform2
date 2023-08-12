package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMail.autoSession
import scalikejdbc._

class TransferConfigMailDAOImpl extends TransferConfigMailDAO {

  /**
   * TransferConfigMail取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfigMail
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigMail] = {
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

  /**
   * TransferConfigMail作成
   * @param transferConfigMail TransferConfigMail
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMail:TransferConfigMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMail.column
      insert.into(TransferConfigMail).namedValues(
        c.transfer_config_id -> transferConfigMail.transfer_config_id,
        c.use_cc -> transferConfigMail.use_cc,
        c.use_bcc -> transferConfigMail.use_bcc,
        c.use_replyto -> transferConfigMail.use_replyto,
        c.user_group -> transferConfigMail.user_group,
        c.created_user -> transferConfigMail.created_user,
        c.modified_user -> transferConfigMail.modified_user,
        c.created -> transferConfigMail.created,
        c.modified -> transferConfigMail.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigMail更新
   * @param transferConfigMail TransferConfigMail
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigMail:TransferConfigMail)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMail.column
      update(TransferConfigMail).set(
        c.transfer_config_id -> transferConfigMail.transfer_config_id,
        c.use_cc -> transferConfigMail.use_cc,
        c.use_bcc -> transferConfigMail.use_bcc,
        c.use_replyto -> transferConfigMail.use_replyto,
        c.user_group -> transferConfigMail.user_group,
        c.created_user -> transferConfigMail.created_user,
        c.modified_user -> transferConfigMail.modified_user,
        c.created -> transferConfigMail.created,
        c.modified -> transferConfigMail.modified
      ).where.eq(c.id, transferConfigMail.id)
    }.update().apply()
  }

  /**
   * TransferConfigMail削除
   *
   * @param userGroup        ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session          DB Session
   * @return TransferConfigMail
   */
  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      val c = TransferConfigMail.column
      deleteFrom(TransferConfigMail)
        .where
        .eq(c.id, transferConfigId)
    }.update().apply()
  }
}
