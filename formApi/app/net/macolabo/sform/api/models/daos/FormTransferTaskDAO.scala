package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTask
import scalikejdbc.DBSession

trait FormTransferTaskDAO {
  def getList(formId: BigInt)(implicit session: DBSession): List[FormTransferTask]
}
