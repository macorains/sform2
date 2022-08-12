package net.macolabo.sform.api.models.daos

import net.macolabo.sform.api.models.form.Form
import scalikejdbc._

trait FormDAO {
  /**
   * フォームデータ取得
   * @param hashedFormId フォームのhashed_id
   * @return フォームデータ
   */
  def get(hashedFormId: String)(implicit session: DBSession) :Option[Form]
}
