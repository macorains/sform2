package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTask
import scalikejdbc._

class FormTransferTaskDAOImpl extends FormTransferTaskDAO {
  def getList(formId: BigInt)(implicit session: DBSession): List[FormTransferTask] = {
    val f = FormTransferTask.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTask as f)
        .where
        .eq(f.form_id, formId)
    ).map(rs=>FormTransferTask(rs)).list().apply()
  }
}
