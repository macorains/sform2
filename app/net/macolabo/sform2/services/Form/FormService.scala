package net.macolabo.sform2.services.Form

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.FormDAO
import net.macolabo.sform2.models.entity.user.User
import net.macolabo.sform2.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.services.Form.get.FormGetResponse
import net.macolabo.sform2.services.Form.list.FormListResponse
import net.macolabo.sform2.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import scalikejdbc.DB

import scala.concurrent.ExecutionContext

class FormService @Inject()(
  formDAO: FormDAO
) (implicit ex: ExecutionContext) {

  /**
   * フォーム詳細データ取得
   *
   * @param userGroup ユーザーグループ
   * @param hashed_form_id フォームhashed ID
   * @return フォームデータ
   */
  def getForm(userGroup: String, hashed_form_id: String): Option[FormGetResponse] = {
    DB.localTx(implicit session => {
      formDAO.getByHashedId(userGroup, hashed_form_id)
    })
  }

  /**
   * フォームリスト取得
   *
   * @param userGroup ユーザーグループ
   * @return フォームリスト
   */
  def getList(userGroup: String): FormListResponse = {
    DB.localTx(implicit session => {
      formDAO.getList(userGroup)
    })
  }

  /**
   * フォーム作成
   *
   * @param userGroup ユーザーグループ
   * @param formUpdateRequest フォーム作成リクエストデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(userId: String, userGroup: String, formUpdateRequest: FormUpdateRequest): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(userId, userGroup, formUpdateRequest)
    })
  }

  /**
   * フォーム更新
   *
   * @param userGroup ユーザーグループ
   * @param formUpdateRequest フォーム更新リクエストデータ
   * @return フォーム更新結果レスポンス
   */
  def update(userId: String, userGroup: String, formUpdateRequest: FormUpdateRequest): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(userId, userGroup, formUpdateRequest)
    })

  }

  /**
   * フォーム削除
   *
   * @param userGroup ユーザーグループ
   * @param hashed_form_id ハッシュ化フォームID
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(userGroup: String, hashed_form_id: String): FormDeleteResponse = {
    DB.localTx(implicit session => {
      formDAO.deleteByHashedId(userGroup, hashed_form_id)
    })
  }
}