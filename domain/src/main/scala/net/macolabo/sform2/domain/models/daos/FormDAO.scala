package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.domain.services.Form.get.FormGetResponse
import net.macolabo.sform2.domain.services.Form.list.FormListResponse
import net.macolabo.sform2.domain.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import net.macolabo.sform2.domain.models.entity.form.Form
import scalikejdbc.DBSession

trait FormDAO {
  /** フォーム取得 */
  def get(userGroup: String, id: BigInt)(implicit session: DBSession): Option[FormGetResponse]

  /**
   * フォームデータ取得
   * @param hashedFormId フォームのhashed_id
   * @return フォームデータ
   */
  def get(hashedFormId: String)(implicit session: DBSession) :Option[Form]

  /** HashedIdによるフォーム取得 */
  def getByHashedId(userGroup: String, hashed_id: String)(implicit session: DBSession): Option[FormGetResponse]

  /** フォーム一覧取得 */
  def getList(userGroup: String)(implicit session: DBSession): FormListResponse

  /** フォーム作成更新 */
  def update(userId: String, userGroup: String, request: FormUpdateRequest)(implicit session: DBSession): FormUpdateResponse

  /** フォーム削除 */
  def delete(userGroup: String, id: BigInt)(implicit session: DBSession): FormDeleteResponse

  /** HashedIdによるフォーム削除 */
  def deleteByHashedId(userGroup: String, hashed_id: String)(implicit session: DBSession): FormDeleteResponse
}
