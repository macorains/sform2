package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress.autoSession
import scalikejdbc.DBSession

trait TransferConfigMailAddressDAO {

  /**
   * TansferConfigMailAddress取得
   *
   * @param transferConfigMailAddressId ID
   * @param session                     DB Session
   * @return TransferCongirMailAddress
   */
  def get(transferConfigMailAddressId: BigInt)(implicit session: DBSession): Option[TransferConfigMailAddress]

  /**
   * TansferConfigMailAddressのリスト取得
   *
   * @param userGroup            ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @param session              DB Session
   * @return TransferCongirMailAddressのリスト
   */
  def getList(userGroup: String, transferConfigMailId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigMailAddress]

  /**
   * TransferCOnfigMailAddress作成
   *
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session                   DB Session
   * @return 作成したレコードのID
   */
  def create(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigMailAddress更新
   *
   * @param transferConfigMailAddress TransferConfigMailAddress
   * @param session                   DB Session
   * @return Result
   */
  def save(transferConfigMailAddress: TransferConfigMailAddress)(implicit session: DBSession = autoSession): BigInt

  /**
   * TransferConfigMailAddress削除
   *
   * @param userGroup                   ユーザーグループ
   * @param transferConfigMailAddressId TransferConfigMailAddress ID
   * @param session                     DB Session
   * @return Result
   */
  def erase(userGroup: String, transferConfigMailAddressId: BigInt)(implicit session: DBSession = autoSession): BigInt
}





