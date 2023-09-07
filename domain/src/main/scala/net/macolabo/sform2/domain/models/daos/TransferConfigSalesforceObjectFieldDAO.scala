package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObjectField
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObjectField.autoSession
import scalikejdbc.DBSession

trait TransferConfigSalesforceObjectFieldDAO {

  /**
   * TransferConfigSalesforceObjectFieldのリスト取得
   *
   * @param userGroup                        ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param session                          DB Session
   * @return TransferConfigSalesforceObjectFieldのリスト
   */
  def getList(userGroup: String, transferConfigSalesforceObjectId: Option[BigInt])(implicit session: DBSession = autoSession): List[TransferConfigSalesforceObjectField]

  /**
   * TransferConfigSalesforceObjectField作成
   *
   * @param transferConfigSalesforceObjectField TransferConfigSalesforceObjectField
   * @param session                             DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigSalesforceObjectField: TransferConfigSalesforceObjectField)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforceObjectField保存
   *
   * @param transferConfigSalesforceObjectField TransferConfigSalesforceObjectField
   * @param session                             DB Session
   * @return result
   */
  def save(transferConfigSalesforceObjectField: TransferConfigSalesforceObjectField)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigSalesforceObjectField削除
   *
   * @param userGroup                             ユーザーグループ
   * @param transferConfigSalesforceObjectFieldId TransferConfigSalesforceObjectField ID
   * @param session                               DB Session
   * @return Result
   */
  def delete(userGroup: String, transferConfigSalesforceObjectFieldId: BigInt)(implicit session: DBSession = autoSession): Int
}
