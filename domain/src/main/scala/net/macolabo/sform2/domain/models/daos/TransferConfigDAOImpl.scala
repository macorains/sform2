package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfig
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfig.autoSession
import scalikejdbc._

class TransferConfigDAOImpl extends TransferConfigDAO {

  /**
   * TransferConfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfig
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfig] = {
    val f = TransferConfig.syntax("f")
    withSQL(
      select(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfig as f)
        .where
        .eq(f.id, transferConfigId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfig(rs)).single().apply()
  }

  /**
   * TransferConfig リスト取得
   * @param userGroup ユーザーグループ
   * @param session DB Session
   * @return TransferConfigのリスト
   */
  def getList(userGroup: String)(implicit session: DBSession = autoSession): List[TransferConfig] = {
    val f = TransferConfig.syntax("f")
    withSQL(
      select(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(TransferConfig as f)
        .where
        .eq(f.user_group, userGroup)
    ).map(rs => TransferConfig(rs)).list().apply()
  }

  /**
   * TransferConfig作成
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfig.column
      insert.into(TransferConfig).namedValues(
        c.type_code -> transferConfig.type_code,
        c.config_index -> transferConfig.config_index,
        c.name -> transferConfig.name,
        c.status -> transferConfig.status,
        c.user_group -> transferConfig.user_group,
        c.created_user -> transferConfig.created_user,
        c.modified_user -> transferConfig.modified_user,
        c.created -> transferConfig.created,
        c.modified -> transferConfig.modified
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * TransferConfig更新
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return Result
   */
  def save(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt = {
    withSQL{
      val c = TransferConfig.column
      update(TransferConfig).set(
        c.type_code -> transferConfig.type_code,
        c.config_index -> transferConfig.config_index,
        c.name -> transferConfig.name,
        c.status -> transferConfig.status,
        c.user_group -> transferConfig.user_group,
        c.modified_user -> transferConfig.modified_user,
        c.modified -> transferConfig.modified
      ).where.eq(c.id, transferConfig.id)
    }.update().apply()
  }

  /**
   * TransferConfig削除
   *
   * @param transferConfigId TransferConfig ID
   * @param session          DB Session
   * @return TransferConfig
   */
  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): BigInt = {
    withSQL {
      val c = TransferConfig.column
      deleteFrom(TransferConfig)
        .where
        .eq(c.id, transferConfigId)
        .and
        .eq(c.user_group, userGroup)
    }.update().apply()
  }
}
