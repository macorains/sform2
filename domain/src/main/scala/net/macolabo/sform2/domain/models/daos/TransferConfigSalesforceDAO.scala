package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce.autoSession
import scalikejdbc.DBSession

trait TransferConfigSalesforceDAO {

  /**
   * TransferConfigSalesforce取得
   *
   * @param transferConfigId Id
   * @param session          DB Session
   * @return 作成したレコードのID
   */
  def get(transferConfigId: BigInt)(implicit session: DBSession): Option[TransferConfigSalesforce]

  /**
   * TransferConfigSalesforce更新
   *
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session                  DB Session
   * @return 作成したレコードのID
   */
  def save(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforce作成
   *
   * @param transferConfigSalesforce TransferConfigSalesforce
   * @param session                  DB Session
   * @return Result
   */
  def create(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforce削除
   *
   * @param userGroup        ユーザーグループ
   * @param transferConfigId Id
   * @param session          DB Session
   * @return 作成したレコードのID
   */
  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession): Int
}


