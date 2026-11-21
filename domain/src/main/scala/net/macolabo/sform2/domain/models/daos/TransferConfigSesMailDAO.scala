package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSesMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSesMail.autoSession
import scalikejdbc.DBSession

trait TransferConfigSesMailDAO {

  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSesMail]

  def create(transferConfigSesMail: TransferConfigSesMail)(implicit session: DBSession = autoSession): BigInt

  def save(transferConfigSesMail: TransferConfigSesMail)(implicit session: DBSession = autoSession): BigInt

  def delete(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Int

}
