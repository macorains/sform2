package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMail.autoSession
import scalikejdbc.DBSession

trait TransferConfigMailDAO {

  /**
   * TransferConfigMail取得
   *
   * @param userGroup        ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session          DB Session
   * @return TransferConfigMail
   */
  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigMail]

  /**
   * TransferConfigMail作成
   *
   * @param transferConfigMail TransferConfigMail
   * @param session            DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMail: TransferConfigMail)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigMail更新
   *
   * @param transferConfigMail TransferConfigMail
   * @param session            DB Session
   * @return Result
   */
  def save(transferConfigMail: TransferConfigMail)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigMail削除
   *
   * @param userGroup        ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @param session          DB Session
   * @return TransferConfigMail
   */
  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int

}

