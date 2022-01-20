package net.macolabo.sform2.models.daos

import net.macolabo.sform2.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.services.Form.get.FormGetResponse
import net.macolabo.sform2.services.Form.list.FormListResponse
import net.macolabo.sform2.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import scalikejdbc.DBSession

trait FormDAO {
  /** フォーム取得 */
  def get(userGroup: String, id: BigInt)(implicit session: DBSession): Option[FormGetResponse]

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
