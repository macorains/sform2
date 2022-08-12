package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskSalesforce
import scalikejdbc._

class FormTransferTaskSalesforceDAOImpl extends FormTransferTaskSalesforceDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskSalesforce] = {
    val f = FormTransferTaskSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.object_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforce as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
    ).map(rs=>FormTransferTaskSalesforce(rs)).list().apply().headOption
  }
}
