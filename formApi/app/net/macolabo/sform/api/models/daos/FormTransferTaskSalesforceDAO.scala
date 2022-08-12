package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskSalesforce
import scalikejdbc._

trait FormTransferTaskSalesforceDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskSalesforce]
}
