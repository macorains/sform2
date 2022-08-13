package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.formtransfertask.FormTransferTaskMail
import scalikejdbc._

class FormTransferTaskMailDAOImpl extends FormTransferTaskMailDAO {
  def get(formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskMail] = {
    val f = FormTransferTaskMail.syntax("f")
    withSQL (
      select(
        f.id,
        f.form_transfer_task_id,
        f.from_address_id,
        f.to_address,
        f.to_address_id,
        f.to_address_field,
        f.cc_address,
        f.cc_address_id,
        f.cc_address_field,
        f.bcc_address_id,
        f.replyto_address_id,
        f.subject,
        f.body,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskMail as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
    ).map(rs=>FormTransferTaskMail(rs)).list().apply().headOption
  }
}
