package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.FormColSelect
import scalikejdbc._

class FormColSelectDAOImpl extends FormColSelectDAO {

  /**
   * フォーム項目選択項目リスト取得
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @return フォーム項目選択項目のリスト
   */
  def getList(formId: BigInt, formColId: BigInt)(implicit session: DBSession): List[FormColSelect] = {
    val f = FormColSelect.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.select_index,
        f.select_name,
        f.select_value,
        f.is_default,
        f.edit_style,
        f.view_style,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColSelect as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
    ).map(rs => FormColSelect(rs)).list().apply()
  }
}
