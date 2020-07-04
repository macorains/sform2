package net.macolabo.sform2.services.Form

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.FormsDAO
import net.macolabo.sform2.models.entity.Form

import scala.concurrent.ExecutionContext

class FormService @Inject() (userDAO: FormsDAO)(implicit ex: ExecutionContext) {

  def getForm(hashed_form_id: String, identity: User): FormGetFormResponse = {
    ???
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
}
