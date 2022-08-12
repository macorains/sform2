package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskSalesforceField
import scalikejdbc._

class FormTransferTaskSalesforceFieldDAOImpl extends FormTransferTaskSalesforceFieldDAO {
  def getList(formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): List[FormTransferTaskSalesforceField] = {
    val f = FormTransferTaskSalesforceField.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_salesforce_id,
        f.form_column_id,
        f.field_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforceField as f)
        .where
        .eq(f.form_transfer_task_salesforce_id, formTransferTaskSalesforceId)
    ).map(rs=>FormTransferTaskSalesforceField(rs)).list().apply()
  }
}
