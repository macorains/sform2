package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.form.FormColSelect
import scalikejdbc.DBSession

trait FormColSelectDAO {
  /**
   * フォーム項目選択項目リスト取得
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @return フォーム項目選択項目のリスト
   */
  def getList(formId: BigInt, formColId: BigInt)(implicit session: DBSession): List[FormColSelect]
}
