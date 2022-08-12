package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.entity.formtransfertask.FormTransferTaskCondition
import scalikejdbc._

class FormTransferTaskConditionDAOImpl extends FormTransferTaskConditionDAO {
  def getList(formTransferTaskId: BigInt)(implicit session: DBSession): List[FormTransferTaskCondition] = {
    val f = FormTransferTaskCondition.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskCondition as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
    ).map(rs=>FormTransferTaskCondition(rs)).list().apply()
  }
}
