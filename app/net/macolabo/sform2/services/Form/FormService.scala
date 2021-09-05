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
   * @param identity       認証情報
   * @param hashed_form_id フォームhashed ID
   * @return フォームデータ
   */
  def getForm(identity: User, hashed_form_id: String): Option[FormGetResponse] = {
    DB.localTx(implicit session => {
      formDAO.getByHashedId(identity, hashed_form_id)
    })
  }

  /**
   * フォームリスト取得
   *
   * @param identity 認証情報
   * @return フォームリスト
   */
  def getList(identity: User): FormListResponse = {
    DB.localTx(implicit session => {
      formDAO.getList(identity)
    })
  }

  /**
   * フォーム作成
   *
   * @param identity          認証情報
   * @param formUpdateRequest フォーム作成リクエストデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(identity: User, formUpdateRequest: FormUpdateRequest): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(identity, formUpdateRequest)
    })
  }

  /**
   * フォーム更新
   *
   * @param identity          認証情報
   * @param formUpdateRequest フォーム更新リクエストデータ
   * @return フォーム更新結果レスポンス
   */
  def update(identity: User, formUpdateRequest: FormUpdateRequest): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(identity, formUpdateRequest)
    })

  }

  /**
   * フォーム削除
   *
   * @param identity       認証情報
   * @param hashed_form_id ハッシュ化フォームID
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(identity: User, hashed_form_id: String): FormDeleteResponse = {
    DB.localTx(implicit session => {
      formDAO.deleteByHashedId(identity, hashed_form_id)
    })
  }
}