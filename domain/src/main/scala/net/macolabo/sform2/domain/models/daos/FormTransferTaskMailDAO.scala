package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskMail
import scalikejdbc.DBSession

trait FormTransferTaskMailDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskMail]
}
