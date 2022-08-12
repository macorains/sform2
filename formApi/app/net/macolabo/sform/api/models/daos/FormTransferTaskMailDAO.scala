package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskMail
import scalikejdbc.DBSession

trait FormTransferTaskMailDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskMail]
}
