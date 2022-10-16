package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.Transfer
import net.macolabo.sform2.domain.models.entity.transfer.Transfer.autoSession
import scalikejdbc.DBSession

trait TransferDAO {

  /**
   * Transfer取得
   *
   * @param transferId Transfer ID
   * @param session    DB Sessoin
   * @return Transfer
   */
  def get(transferId: BigInt)(implicit session: DBSession = autoSession): Option[Transfer]

  /**
   * Transferのリスト取得
   *
   * @param session DB Session
   * @return Transferのリスト
   */
  def getList()(implicit session: DBSession = autoSession): List[Transfer]
}
