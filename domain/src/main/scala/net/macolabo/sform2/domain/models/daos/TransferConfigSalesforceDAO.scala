package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import scalikejdbc.DBSession

trait TransferConfigSalesforceDAO {
  def get(transferConfigId: BigInt)(implicit session: DBSession): Option[TransferConfigSalesforce]
}
