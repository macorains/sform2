package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTask
import scalikejdbc.DBSession

trait FormTransferTaskDAO {
  def getList(formId: BigInt)(implicit session: DBSession): List[FormTransferTask]
}
