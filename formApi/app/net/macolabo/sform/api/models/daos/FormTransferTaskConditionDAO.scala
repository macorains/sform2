package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskCondition
import scalikejdbc.DBSession

trait FormTransferTaskConditionDAO {
  def getList(formTransferTaskId: BigInt)(implicit session: DBSession): List[FormTransferTaskCondition]
}
