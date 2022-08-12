package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskSalesforceField
import scalikejdbc.DBSession

trait FormTransferTaskSalesforceFieldDAO {
  def getList(formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): List[FormTransferTaskSalesforceField]
}
