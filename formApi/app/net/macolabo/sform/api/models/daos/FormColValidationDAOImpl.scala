package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.form.FormColValidation
import scalikejdbc._

class FormColValidationDAOImpl extends FormColValidationDAO {
  /**
   * フォーム項目バリデーションの取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @return フォーム項目バリデーションのリスト
   */
  def get(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession): Option[FormColValidation] = {
    val f = FormColValidation.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.max_value,
        f.min_value,
        f.max_length,
        f.min_length,
        f.input_type,
        f.required,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColValidation as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormColValidation(rs)).single().apply()
  }
}
