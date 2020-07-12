package net.macolabo.sform2.services.Form

import java.time.ZonedDateTime
import java.util.UUID

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition}

import scala.concurrent.ExecutionContext

class FormService @Inject() (implicit ex: ExecutionContext) {

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
        convertToFormGetFormResponseFormCol(userGroup, f.id),
        convertToFormGetFormResponseTransferTask(userGroup, f.id)
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

  /**
   * フォーム作成
   * @param identity 認証情報
   * @param formInsertFormRequest フォーム作成リクエストデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(identity: User, formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    val response = insertForm(identity, formInsertFormRequest)
    formInsertFormRequest.form_cols.map(f => {
      val formColId = insertFormCol(identity, f, response.id)
      f.select_list.map(s => {
        insertFormColSelect(identity, s, response.id, formColId)
      })
      f.validations.map(v => insertFormColValidation(identity, v, response.id, formColId))
    })
    response
  }

  /**
   * フォーム更新
   * @param identity 認証情報
   * @param formUpdateFormRequest フォーム更新リクエストデータ
   * @return フォーム更新結果レスポンス
   */
  def update(identity: User, formUpdateFormRequest: FormUpdateFormRequest): FormUpdateFormResponse = {
    val formId = updateForm(identity, formUpdateFormRequest)
    // ToDo DB上のフォーム項目とリクエスト内のフォーム項目を比較して、DB上にあってリクエストに無いものはあらかじめ削除する
    formUpdateFormRequest.form_cols.map(f => {
      f.id match {
        case Some(i: Int) if i > 0 => updateFormCol(identity, f)
        case _ => insertFormCol(identity, FormUpdateFormRequestFormColToFormInsertFormRequestFormCol(f), formUpdateFormRequest.id)
      }
    })
    FormUpdateFormResponse(formId)
  }

  /**
   * フォーム削除
   * @param identity 認証情報
   * @param hashed_form_id ハッシュ化フォームID
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(identity: User, hashed_form_id: String): FormDeleteFormResponse = {
    val userGroup = identity.group.getOrElse("")
    val result = Form.get(identity.group.get, hashed_form_id).map(f => {
      FormCol.getList(userGroup, f.id).map(c => {
        FormColSelect.getList(userGroup, f.id, c.id).map(s => {
          FormColSelect.erase(userGroup, s.id)
        })
        FormColValidation.get(userGroup, f.id, c.id).map(v => {
          FormColValidation.erase(userGroup, v.id)
        })
        FormCol.erase(userGroup, c.id)
      })
      Form.erase(userGroup, f.id)
    })
    FormDeleteFormResponse(result)
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

  private def convertToFormGetFormResponseTransferTask(userGroup: String, formId: Int) = {
    FormTransferTask.getList(userGroup, formId).map(f => {
      FormGetFormResponseFormTransferTask(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        convertToFormGetFormResponseTransferTaskCondition(userGroup, formId, f.id)
      )
    })
  }

  private def convertToFormGetFormResponseTransferTaskCondition(userGroup: String, formId: Int, formTransferTaskId: Int) = {
    FormTransferTaskCondition.getList(userGroup, formId, formTransferTaskId).map(f => {
      FormGetFormResponseFormTransferTaskCondition(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value
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
      formUpdateFormRequestFormCol.id.getOrElse(0),
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
    val result = formCol.update
    // ToDo DB上の選択項目とリクエスト内の選択項目を比較して、DB上にあってリクエストに無いものはあらかじめ削除する
    formUpdateFormRequestFormCol.select_list.map(s => {
      s.id match {
        case Some(i: Int) if i > 0 => updateFormColSelect(identity, s)
        case _ => insertFormColSelect(identity, FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(s), formCol.form_id, formCol.id)
      }
    })
    formUpdateFormRequestFormCol.validations.map(v => {
      v.id match {
        case Some(i: Int) if i > 0 => updateFormColValidation(identity, v)
        case _ => insertFormColValidation(identity, FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(v), formCol.form_id, formCol.id)
      }
    })
    result
  }

  private def updateFormColSelect(identity: User, formUpdateFormRequestFormColSelect: FormUpdateFormRequestFormColSelect): Int = {
    val formColSelect = FormColSelect(
      formUpdateFormRequestFormColSelect.id.getOrElse(0),
      formUpdateFormRequestFormColSelect.form_col_id.getOrElse(0),
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
      formUpdateFormRequestFormColValidation.id.getOrElse(0),
      formUpdateFormRequestFormColValidation.form_col_id.getOrElse(0),
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

  //-------------------------------------------------
  //  フォーム作成ロジック
  //-------------------------------------------------
  private def insertForm(identity: User, formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    val hashedId = UUID.randomUUID().toString

    val form = Form(
      0,
      hashedId,
      formInsertFormRequest.form_index,
      formInsertFormRequest.name,
      formInsertFormRequest.title,
      formInsertFormRequest.status,
      formInsertFormRequest.cancel_url,
      formInsertFormRequest.complete_url,
      Option(formInsertFormRequest.input_header),
      Option(formInsertFormRequest.confirm_header),
      Option(formInsertFormRequest.complete_text),
      Option(formInsertFormRequest.close_text),
      "{}",
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    val formId = form.insert
    FormInsertFormResponse(formId, hashedId)
  }

  private def insertFormCol(identity: User, formInsertFormRequestFormCol: FormInsertFormRequestFormCol, formId: Int): Int = {
    val formCol = FormCol(
      0,
      formId,
      formInsertFormRequestFormCol.name,
      formInsertFormRequestFormCol.col_id,
      formInsertFormRequestFormCol.col_index,
      formInsertFormRequestFormCol.col_type,
      Option(formInsertFormRequestFormCol.default_value),
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formCol.insert
  }

  private def insertFormColSelect(identity: User, formInsertFormRequestFormColSelect: FormInsertFormRequestFormColSelect, formId: Int, formColId: Int): Int = {
    val formColSelect = FormColSelect(
      0,
      formColId,
      formId,
      formInsertFormRequestFormColSelect.select_index,
      formInsertFormRequestFormColSelect.select_name,
      formInsertFormRequestFormColSelect.select_value,
      formInsertFormRequestFormColSelect.is_default,
      Option(formInsertFormRequestFormColSelect.edit_style),
      Option(formInsertFormRequestFormColSelect.view_style),
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColSelect.insert
  }

  private def insertFormColValidation(identity: User, formInsertFormRequestFormColValidation: FormInsertFormRequestFormColValidation, formId: Int, formColId: Int): Int = {
    val formColValidation = FormColValidation(
      0,
      formColId,
      formId,
      formInsertFormRequestFormColValidation.max_value,
      formInsertFormRequestFormColValidation.min_value,
      formInsertFormRequestFormColValidation.max_length,
      formInsertFormRequestFormColValidation.min_length,
      formInsertFormRequestFormColValidation.input_type,
      identity.group.getOrElse(""),
      identity.userID.toString,
      identity.userID.toString,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    )
    formColValidation.insert
  }

  //-------------------------------------------------
  //  更新リクエスト→作成リクエスト変換ロジック
  //-------------------------------------------------
  private def FormUpdateFormRequestFormColToFormInsertFormRequestFormCol(src: FormUpdateFormRequestFormCol): FormInsertFormRequestFormCol = {
    FormInsertFormRequestFormCol(
      src.name,
      src.col_id,
      src.col_index,
      src.col_type,
      src.default_value,
      src.select_list.map(s => FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(s)),
      src.validations.map(v => FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(v))
    )
  }

  private def FormUpdateFormRequestFormColSelectToFormInsertFormRequestFormColSelect(src: FormUpdateFormRequestFormColSelect): FormInsertFormRequestFormColSelect = {
    FormInsertFormRequestFormColSelect(
      src.select_index,
      src.select_name,
      src.select_value,
      src.is_default,
      src.edit_style,
      src.view_style
    )
  }

  private def FormUpdateFormRequestFormColValidationToFormInsertFormRequestFormColValidation(src: FormUpdateFormRequestFormColValidation): FormInsertFormRequestFormColValidation = {
    FormInsertFormRequestFormColValidation(
      src.max_value,
      src.min_value,
      src.max_length,
      src.min_length,
      src.input_type
    )
  }

}
