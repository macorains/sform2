package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigMailAddress
import scalikejdbc.DBSession

trait TransferConfigMailAddressDAO {
  def get(transferConfigMailAddressId: BigInt)(implicit session: DBSession): Option[TransferConfigMailAddress]
}
