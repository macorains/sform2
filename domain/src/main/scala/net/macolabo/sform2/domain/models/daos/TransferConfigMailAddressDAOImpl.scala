package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress.autoSession
import scalikejdbc._

class TransferConfigMailAddressDAOImpl extends TransferConfigMailAddressDAO {

  /**
   * TansferConfigMailAddress取得
   * @param transferConfigMailAddressId ID
   * @param session DB Session
   * @return TransferCongirMailAddress
   */
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

  /**
   * TansferConfigMailAddressのリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @param session DB Session
   * @return TransferCongirMailAddressのリスト
   */
  def getList(userGroup: String, transferConfigMailId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigMailAddress] = {
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

  /**
   * TransferCOnfigMailAddress作成
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMailAddress.column
      insert.into(TransferConfigMailAddress).namedValues(
        c.transfer_config_mail_id -> transferConfigMailAddress.transfer_config_mail_id,
        c.address_index -> transferConfigMailAddress.address_index,
        c.name -> transferConfigMailAddress.name,
        c.address -> transferConfigMailAddress.address,
        c.user_group -> transferConfigMailAddress.user_group,
        c.created_user -> transferConfigMailAddress.created_user,
        c.modified_user -> transferConfigMailAddress.modified_user,
        c.created -> transferConfigMailAddress.created,
        c.modified-> transferConfigMailAddress.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfigMailAddress更新
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session DB Session
   * @return Result
   */
  def save(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfigMailAddress.column
      update(TransferConfigMailAddress).set(
        c.transfer_config_mail_id -> transferConfigMailAddress.transfer_config_mail_id,
        c.address_index -> transferConfigMailAddress.address_index,
        c.name -> transferConfigMailAddress.name,
        c.address -> transferConfigMailAddress.address,
        c.user_group -> transferConfigMailAddress.user_group,
        c.created_user -> transferConfigMailAddress.created_user,
        c.modified_user -> transferConfigMailAddress.modified_user,
        c.created -> transferConfigMailAddress.created,
        c.modified-> transferConfigMailAddress.modified
      ).where.eq(c.id, transferConfigMailAddress.id)
    }.update().apply()
  }

  /**
   * TransferConfigMailAddress削除
   * @param userGroup ユーザーグループ
   * @param transferConfigMailAddressId TransferConfigMailAddress ID
   * @param session DB Session
   * @return Result
   */
  def delete(userGroup: String, transferConfigMailAddressId: BigInt)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      deleteFrom(TransferConfigMailAddress)
        .where
        .eq(TransferConfigMailAddress.column.id, transferConfigMailAddressId)
        .and
        .eq(TransferConfigMailAddress.column.user_group, userGroup)
    }.update().apply()
  }
}
