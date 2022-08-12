package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.form.Form
import scalikejdbc._

class FormDAOImpl extends FormDAO {
  /**
   * フォームデータ取得
   * @param hashedFormId フォームのhashed_id
   * @return フォームデータ
   */
  def get(hashedFormId: String)(implicit session: DBSession) :Option[Form] = {
    val f = Form.syntax("f")
    withSQL(
      select(
        f.id,
        f.hashed_id,
        f.form_index,
        f.name,
        f.title,
        f.status,
        f.cancel_url,
        f.complete_url,
        f.input_header,
        f.confirm_header,
        f.complete_text,
        f.close_text,
        f.form_data,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
      .from(Form as f)
      .where
      .eq(f.hashed_id, hashedFormId)
    ).map(rs => Form(rs)).single().apply()
  }
}
