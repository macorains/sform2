package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskCondition
import scalikejdbc.DBSession

trait FormTransferTaskConditionDAO {
  def getList(formTransferTaskId: BigInt)(implicit session: DBSession): List[FormTransferTaskCondition]
}
