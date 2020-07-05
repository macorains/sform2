package net.macolabo.sform2.services.Form

import java.time.ZonedDateTime

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.FormsDAO
import net.macolabo.sform2.models.entity.{Form, FormCol, FormColSelect, FormColValidation}

import scala.concurrent.ExecutionContext

class FormService @Inject() (userDAO: FormsDAO)(implicit ex: ExecutionContext) {

  /**
   * フォーム詳細データ取得
   * @param identity 認証情報
   * @param hashed_form_id フォームhashed ID
   * @return フォームデータ
   */
  def getForm(identity: User, hashed_form_id: String): Option[FormGetFormResponse] = {
    val userGroup = identity.group.getOrElse("")
    Form.get(userGroup, hashed_form_id).map(f => {
      FormGetFormResponse(
        f.id,
        f.name,
        f.form_index,
        f.title,
        f.status,
        f.cancel_url,
        f.close_text.getOrElse(""),
        f.hashed_id,
        f.complete_url,
        f.input_header.getOrElse(""),
        f.complete_text.getOrElse(""),
        f.confirm_header.getOrElse(""),
        convertToFormGetFormResponseFormCol(userGroup, f.id)
      )
    })
  }

  /**
   * フォームリスト取得
   * @param identity 認証情報
   * @return フォームリスト
   */
  def getList(identity: User): FormGetListResponse = {
    val userGroup = identity.group.getOrElse("")
    val forms = Form.getList(userGroup).map(f => {
      FormGetListForm(
        f.id,
        f.name,
        f.form_index,
        f.title,
        f.status,
        f.hashed_id
      )
    })
    FormGetListResponse(forms, forms.length)
  }

  def getListForTransferJobManager: FormGetListForTransferJobManagerResponse = {
    ???
  }

  def getHtml: FormGetHtmlResponse = {
    ???
  }

  def insert(identity: User, formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    ???
  }

  /**
   * フォーム更新
   * @param identity 認証情報
   * @param formUpdateFormRequest フォーム更新リクエストデータ
   * @return フォーム更新結果レスポンス
   */
  def update(identity: User, formUpdateFormRequest: FormUpdateFormRequest): FormUpdateFormResponse = {
    val formId = updateForm(identity, formUpdateFormRequest)
    formUpdateFormRequest.form_cols.map(f => {
      updateFormCol(identity, f)
      f.select_list.map(s => {
        updateFormColSelect(identity, s)
      })
      f.validations.map(v =>updateFormColValidation(identity, v))
    })
    FormUpdateFormResponse(formId)
  }


  def deleteForm(identity: User, hashed_form_id: String): FormDeleteFormResponse = {
    ???
  }

  def validateForm(formValidateFormRequest: FormValidateFormRequest): FormValidateFormResponse = {
    ???
  }

  def getFormCols(hashed_form_id: String): FormGetFormColsResponse = {
    ???
  }

  //-------------------------------------------------
  //  フォーム詳細変換ロジック
  //-------------------------------------------------
  private def convertToFormGetFormResponseFormCol(userGroup: String, form_id: Int): List[FormGetFormResponseFormCol] = {
    FormCol.getList(userGroup, form_id).map(f => {
      FormGetFormResponseFormCol(
        f.id,
        f.form_id,
        f.name,
        f.col_id,
        f.col_index,
        f.col_type,
        f.default_value.getOrElse(""),
        convertToFormGetFormResponseFormColSelect(userGroup, form_id, f.id),
        convertToFormGetFormResponseFormColValidation(userGroup, form_id, f.id)
      )
    })
  }

  private def convertToFormGetFormResponseFormColSelect(userGroup: String, form_id: Int, form_col_id: Int): List[FormGetFormResponseFormColSelect] = {
    FormColSelect.getList(userGroup, form_id, form_col_id).map(f => {
      FormGetFormResponseFormColSelect(
        f.id,
        f.form_col_id,
        f.form_id,
        f.select_index,
        f.select_name,
        f.select_value,
        f.is_default,
        f.edit_style.getOrElse(""),
        f.view_style.getOrElse("")
      )
    })
  }

  private def convertToFormGetFormResponseFormColValidation(userGroup: String, form_id: Int, form_col_id: Int) = {
    FormColValidation.get(userGroup, form_id, form_col_id).map(f => {
      FormGetFormResponseFormColValidation(
        f.id,
        f.form_col_id,
        f.form_id,
        f.max_value,
        f.min_value,
        f.max_length,
        f.min_length,
        f.input_type
      )
    })
  }

  //-------------------------------------------------
  //  フォーム更新ロジック
  //-------------------------------------------------
  private def updateForm(identity: User, formUpdateFormRequest: FormUpdateFormRequest): Int = {
    val form = Form(
      formUpdateFormRequest.id,
      formUpdateFormRequest.hashed_id,
      formUpdateFormRequest.form_index,
      formUpdateFormRequest.name,
      formUpdateFormRequest.title,
      formUpdateFormRequest.status,
      formUpdateFormRequest.cancel_url,
      formUpdateFormRequest.complete_url,
      Option(formUpdateFormRequest.input_header),
      Option(formUpdateFormRequest.confirm_header),
      Option(formUpdateFormRequest.complete_text),
      Option(formUpdateFormRequest.close_text),
      "{}",
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    form.update
  }

  private def updateFormCol(identity: User, formUpdateFormRequestFormCol: FormUpdateFormRequestFormCol): Int = {
    val formCol = FormCol(
      formUpdateFormRequestFormCol.id,
      formUpdateFormRequestFormCol.form_id,
      formUpdateFormRequestFormCol.name,
      formUpdateFormRequestFormCol.col_id,
      formUpdateFormRequestFormCol.col_index,
      formUpdateFormRequestFormCol.col_type,
      Option(formUpdateFormRequestFormCol.default_value),
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formCol.update
  }

  private def updateFormColSelect(identity: User, formUpdateFormRequestFormColSelect: FormUpdateFormRequestFormColSelect): Int = {
    val formColSelect = FormColSelect(
      formUpdateFormRequestFormColSelect.id,
      formUpdateFormRequestFormColSelect.form_col_id,
      formUpdateFormRequestFormColSelect.form_id,
      formUpdateFormRequestFormColSelect.select_index,
      formUpdateFormRequestFormColSelect.select_name,
      formUpdateFormRequestFormColSelect.select_value,
      formUpdateFormRequestFormColSelect.is_default,
      Option(formUpdateFormRequestFormColSelect.edit_style),
      Option(formUpdateFormRequestFormColSelect.view_style),
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColSelect.update
  }

  private def updateFormColValidation(identity: User, formUpdateFormRequestFormColValidation: FormUpdateFormRequestFormColValidation): Int = {
    val formColValidation = FormColValidation(
      formUpdateFormRequestFormColValidation.id,
      formUpdateFormRequestFormColValidation.form_col_id,
      formUpdateFormRequestFormColValidation.form_id,
      formUpdateFormRequestFormColValidation.max_value,
      formUpdateFormRequestFormColValidation.min_value,
      formUpdateFormRequestFormColValidation.max_length,
      formUpdateFormRequestFormColValidation.min_length,
      formUpdateFormRequestFormColValidation.input_type,
      "",
      "",
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColValidation.update
  }
}
