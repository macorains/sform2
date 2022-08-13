package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.FormCol
import scalikejdbc._

class FormColDAOImpl extends FormColDAO {

  /**
   * フォーム項目一覧
   * @param formId フォームID
   * @return フォーム項目のリスト
   */
  def getList(formId: BigInt)(implicit session: DBSession): List[FormCol] = {
    val f = FormCol.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_id,
        f.name,
        f.col_id,
        f.col_index,
        f.col_type,
        f.default_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormCol as f)
        .where
        .eq(f.form_id, formId)
        .orderBy(f.col_index)
        .asc
    ).map(rs => FormCol(rs)).list().apply()
  }
}
