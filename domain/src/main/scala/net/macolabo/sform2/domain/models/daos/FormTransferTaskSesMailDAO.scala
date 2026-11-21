package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskSesMail
import scalikejdbc.DBSession

trait FormTransferTaskSesMailDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskSesMail]
}
