package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObject
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObject.autoSession
import scalikejdbc.DBSession

trait TransferConfigSalesforceObjectDAO {

  /**
   * TransferConfigSalesforceObjectのリスト取得
   *
   * @param userGroup                  ユーザーグループ
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @param session                    DB Session
   * @return TransferConfigSalesforceObjectのリスト
   */
  def getList(userGroup: String, transferConfigSalesforceId: Option[BigInt])(implicit session: DBSession = autoSession): List[TransferConfigSalesforceObject]

  /**
   * TransferConfigSalesforceObject作成
   *
   * @param transferConfigSalesforceObject  　TransferConfigSalesforceObject
   * @param session                        DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforceObject保存
   *
   * @param transferConfigSalesforceObject  　TransferConfigSalesforceObject
   * @param session                        DB Session
   * @return result
   */
  def save(transferConfigSalesforceObject: TransferConfigSalesforceObject)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforceObject削除
   *
   * @param userGroup                        ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param session                          DB Session
   * @return Result
   */
  def delete(userGroup: String, transferConfigSalesforceObjectId: BigInt)(implicit session: DBSession = autoSession): Int
}
