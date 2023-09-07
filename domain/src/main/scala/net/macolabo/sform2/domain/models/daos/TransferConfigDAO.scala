package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfig
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfig.autoSession
import scalikejdbc.DBSession

trait TransferConfigDAO {

  /**
   * TransferConfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session DB Session
   * @return TransferConfig
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfig]

  /**
   * TransferConfig リスト取得
   * @param userGroup ユーザーグループ
   * @param session DB Session
   * @return TransferConfigのリスト
   */
  def getList(userGroup: String)(implicit session: DBSession = autoSession): List[TransferConfig]

  /**
   * TransferConfig作成
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfig更新
   * @param transferConfig TransferConfig
   * @param session DB Session
   * @return Result
   */
  def save(transferConfig: TransferConfig)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfig削除
   *
   * @param transferConfigId TransferConfig ID
   * @param session          DB Session
   * @return TransferConfig
   */
  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int

}


