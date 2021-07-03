package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.User
import net.macolabo.sform2.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.services.Form.get.FormGetResponse
import net.macolabo.sform2.services.Form.list.FormListResponse
import net.macolabo.sform2.services.Form.update.{FormUpdateRequest, FormUpdateResponse}

trait FormDAO {
  /** フォーム取得 */
  def get(identity: User, id: BigInt): Option[FormGetResponse]

  /** HashedIdによるフォーム取得 */
  def getByHashedId(identity: User, hashed_id: String): Option[FormGetResponse]

  /** フォーム一覧取得 */
  def getList(identity: User): FormListResponse

//  /** フォーム作成 */
//  def insert(identity: User, request: FormInsertRequest): FormInsertResponse

  /** フォーム更新 */
  def update(identity: User, request: FormUpdateRequest): FormUpdateResponse

  /** フォーム削除 */
  def delete(identity: User, id: BigInt): FormDeleteResponse

  /** HashedIdによるフォーム削除 */
  def deleteByHashedId(identity: User, hashed_id: String): FormDeleteResponse
}
