package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSmtpMail
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSmtpMail.autoSession
import scalikejdbc.DBSession

trait TransferConfigSmtpMailDAO {

  def get(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): Option[TransferConfigSmtpMail]

  def getList(userGroup: String, transferConfigId: BigInt)(implicit session: DBSession = autoSession): List[TransferConfigSmtpMail]

  def create(transferConfigSmtpMail: TransferConfigSmtpMail)(implicit session: DBSession = autoSession): BigInt

  def save(transferConfigSmtpMail: TransferConfigSmtpMail)(implicit session: DBSession = autoSession): BigInt

  def delete(userGroup: String, id: BigInt)(implicit session: DBSession = autoSession): Int

}
