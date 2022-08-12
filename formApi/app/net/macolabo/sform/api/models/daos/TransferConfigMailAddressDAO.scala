package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.transferconfig.TransferConfigMailAddress
import scalikejdbc.DBSession

trait TransferConfigMailAddressDAO {
  def get(transferConfigMailAddressId: BigInt)(implicit session: DBSession): Option[TransferConfigMailAddress]
}
