package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskSalesforce
import scalikejdbc._

trait FormTransferTaskSalesforceDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskSalesforce]
}
