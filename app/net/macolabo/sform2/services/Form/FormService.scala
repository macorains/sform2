package net.macolabo.sform2.services.Form

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.FormsDAO

import scala.concurrent.ExecutionContext

class FormService @Inject() (userDAO: FormsDAO)(implicit ex: ExecutionContext) {

  def getForm(hashed_form_id: String): FormGetFormResponse = {
    ???
  }

  def getList(request: FormGetListRequest): FormGetListResponse = {
    ???
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
