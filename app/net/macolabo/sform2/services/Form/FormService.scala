package net.macolabo.sform2.services.Form

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.FormsDAO
import net.macolabo.sform2.models.entity.{Form, FormCol, FormColSelect, FormColValidation}

import scala.concurrent.ExecutionContext

class FormService @Inject() (userDAO: FormsDAO)(implicit ex: ExecutionContext) {

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

  def insertForm(formInsertFormRequest: FormInsertFormRequest): FormInsertFormResponse = {
    ???
  }

  def updateForm(formUpdateFormRequest: FormUpdateFormRequest): FormUpdateFormResponse = {
    ???
  }

  def deleteForm(hashed_form_id: String): FormDeleteFormResponse = {
    ???
  }

  def validateForm(formValidateFormRequest: FormValidateFormRequest): FormValidateFormResponse = {
    ???
  }

  def getFormCols(hashed_form_id: String): FormGetFormColsResponse = {
    ???
  }

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

}
