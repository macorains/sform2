package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.form.FormCol
import scalikejdbc.DBSession

trait FormColDAO {

  /**
   * フォーム項目一覧
   * @param formId フォームID
   * @return フォーム項目のリスト
   */
  def getList(formId: BigInt)(implicit session: DBSession): List[FormCol]


}
