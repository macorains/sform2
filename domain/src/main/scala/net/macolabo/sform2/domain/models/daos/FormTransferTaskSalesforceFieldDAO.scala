package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskSalesforceField
import scalikejdbc.DBSession

trait FormTransferTaskSalesforceFieldDAO {
  def getList(formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): List[FormTransferTaskSalesforceField]
}
