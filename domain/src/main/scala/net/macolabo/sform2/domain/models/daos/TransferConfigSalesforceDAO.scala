package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce.autoSession
import scalikejdbc.DBSession

trait TransferConfigSalesforceDAO {
  def get(transferConfigId: BigInt)(implicit session: DBSession): Option[TransferConfigSalesforce]

  def save(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt

  def create(transferConfigSalesforce: TransferConfigSalesforce)(implicit session: DBSession = autoSession): BigInt
}


