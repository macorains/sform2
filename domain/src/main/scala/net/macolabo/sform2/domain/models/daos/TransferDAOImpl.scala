package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.Transfer
import net.macolabo.sform2.domain.models.entity.transfer.Transfer.autoSession
import scalikejdbc._

class TransferDAOImpl extends TransferDAO {

  /**
   * Transfer取得
   * @param transferId Transfer ID
   * @param session DB Sessoin
   * @return Transfer
   */
  def get(transferId: BigInt)(implicit session: DBSession = autoSession) :Option[Transfer] = {
    val f = Transfer.syntax("f")
    withSQL (
      select(
        f.id,
        f.type_code,
        f.name,
        f.status,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(Transfer as f)
        .where
        .eq(f.id, transferId)
    ).map(rs => Transfer(rs)).single().apply()
  }

  /**
   * Transferのリスト取得
   * @param session DB Session
   * @return Transferのリスト
   */
  def getList()(implicit session: DBSession = autoSession): List[Transfer] = {
    val f = Transfer.syntax("f")
    withSQL (
      select(
        f.id,
        f.type_code,
        f.name,
        f.status,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(Transfer as f)
    ).map(rs => Transfer(rs)).list().apply()
  }
}
