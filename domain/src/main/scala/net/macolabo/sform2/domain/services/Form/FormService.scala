package net.macolabo.sform2.domain.services.Form

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.daos.FormDAO
import net.macolabo.sform2.domain.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.domain.services.Form.get.FormGetResponse
import net.macolabo.sform2.domain.services.Form.list.FormListResponse
import net.macolabo.sform2.domain.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import play.api.mvc.Session
import scalikejdbc.DB

import scala.concurrent.ExecutionContext

class FormService @Inject()(
  formDAO: FormDAO
) (implicit ex: ExecutionContext) {

  /**
   * フォーム詳細データ取得
   *
   * @param hashed_form_id フォームhashed ID
   * @param sessionInfo セッションデータ
   * @return フォームデータ
   */
  def getForm(hashed_form_id: String, sessionInfo: SessionInfo): Option[FormGetResponse] = {
    DB.localTx(implicit session => {
      formDAO.getByHashedId(sessionInfo.user_group, hashed_form_id)
    })
  }

  /**
   * フォームリスト取得
   *
   * @param sessionInfo セッションデータ
   * @return フォームリスト
   */
  def getList(sessionInfo: SessionInfo): FormListResponse = {
    DB.localTx(implicit session => {
      formDAO.getList(sessionInfo.user_group)
    })
  }

  /**
   * フォーム作成
   *
   * @param formUpdateRequest フォーム作成リクエストデータ
   * @param sessionInfo セッションデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(formUpdateRequest: FormUpdateRequest, sessionInfo: SessionInfo): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(sessionInfo.user_id, sessionInfo.user_group, formUpdateRequest)
    })
  }

  /**
   * フォーム更新
   *
   * @param formUpdateRequest フォーム更新リクエストデータ
   * @param sessionInfo セッションデータ
   * @return フォーム更新結果レスポンス
   */
  def update(formUpdateRequest: FormUpdateRequest, sessionInfo: SessionInfo): FormUpdateResponse = {
    DB.localTx(implicit session => {
      formDAO.update(sessionInfo.user_id, sessionInfo.user_group, formUpdateRequest)
    })
  }

  /**
   * フォーム削除
   *
   * @param hashed_form_id ハッシュ化フォームID
   * @param sessionInfo セッションデータ
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(hashed_form_id: String, sessionInfo: SessionInfo): FormDeleteResponse = {
    DB.localTx(implicit session => {
      formDAO.deleteByHashedId(sessionInfo.user_group, hashed_form_id)
    })
  }
}
