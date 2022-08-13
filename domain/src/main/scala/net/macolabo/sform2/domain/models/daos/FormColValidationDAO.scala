package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.FormColValidation
import scalikejdbc.DBSession

trait FormColValidationDAO {
  /**
   * フォーム項目バリデーションの取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @return フォーム項目バリデーションのリスト
   */
  def get(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession): Option[FormColValidation]

}
