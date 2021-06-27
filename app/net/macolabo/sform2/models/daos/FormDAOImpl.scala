package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}
import net.macolabo.sform2.models.{SFDBConf, User}
import net.macolabo.sform2.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.services.Form.get.{FormColGetReponse, FormColSelectGetReponse, FormGetResponse, FormTransferTaskGetReponse}
import net.macolabo.sform2.services.Form.insert.{FormInsertRequest, FormInsertResponse}
import net.macolabo.sform2.services.Form.list.FormListResponse
import net.macolabo.sform2.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import scalikejdbc._

class FormDAOImpl extends FormDAO with SFDBConf {

  /** フォーム取得 */
  def get(identity: User, id: BigInt): Option[FormGetResponse] = {
    val userGroup = identity.group.getOrElse("")
    selectForm(userGroup, id).map(form => {
      convertToFormGetResponse(userGroup, form)
    })
  }

  /** HashedIdによるフォーム取得 */
  def getByHashedId(identity: User, hashed_id: String): Option[FormGetResponse] = {
    val userGroup = identity.group.getOrElse("")
    selectFormByHashedId(userGroup, hashed_id).map(form => {
      convertToFormGetResponse(userGroup, form)
    })
  }

  /** フォーム一覧取得 */
  def getList(identity: User): FormListResponse = {
    val userGroup = identity.group.getOrElse("")
    ???
  }

  /** フォーム作成 */
  def insert(identity: User, request: FormInsertRequest): FormInsertResponse = {
    ???
  }

  /** フォーム更新 */
  def update(identity: User, request: FormUpdateRequest): FormUpdateResponse = {
    ???
  }

  /** フォーム削除 */
  def delete(identity: User, id: BigInt): FormDeleteResponse = {
    ???
  }

  /** HashedIdによるフォーム削除 */
  def deleteByHashedId(identity: User, hashed_id: String): FormDeleteResponse = {
    ???
  }

  // ----------------------------------------------
  // レスポンス作成
  // ----------------------------------------------
  private def convertToFormGetResponse(userGroup: String, form: Form): FormGetResponse = {
    FormGetResponse(
      form.id,
      form.name,
      form.form_index,
      form.title,
      form.status,
      form.cancel_url,
      form.close_text.getOrElse(""),
      form.hashed_id,
      form.complete_url,
      form.input_header.getOrElse(""),
      form.complete_text.getOrElse(""),
      form.confirm_header.getOrElse(""),
      selectFormColList(userGroup, form.id).map(fc => convertToFormCol(userGroup, fc)),
      selectFormTransferTaskList(userGroup, form.id).map(ft => convertToFormTransferTask(userGroup, ft))
    )
  }

  private def convertToFormCol(userGroup: String, formCol: FormCol): FormColGetReponse = {
    FormColGetReponse(
      formCol.id,
      formCol.form_id,
      formCol.name,
      formCol.col_id,
      formCol.col_index,
      formCol.col_type,
      formCol.default_value.getOrElse(""),
      selectFormColSelectList(userGroup, formCol.form_id, formCol.id).map(col => convertToFormColSelect(col)),
      selectFormColValidation(userGroup, formCol.form_id, formCol.id).map(validation => convertToFormColValidation(userGroup, validation))
    )
  }

  private def convertToFormTransferTaskSalesforceField() = {
    ???
  }

  private def convertToFormTransferTaskSalesforce() = {
    ???
  }

  private def convertToFormTransferTaskMail() = {
    ???
  }

  private def convertToFormTransferTaskCondition() = {
    ???
  }

  private def convertToFormTransferTask(userGroup: String, formTransferTask: FormTransferTask): FormTransferTaskGetReponse = {
    ???
  }

  private def convertToFormColSelect(formColSelect: FormColSelect) = {
    FormColSelectGetReponse (
      formColSelect.id,
      formColSelect.form_col_id,
      formColSelect.form_id,
      formColSelect.select_index,
      formColSelect.select_name,
      formColSelect.select_value,
      formColSelect.is_default,
      formColSelect.edit_style.getOrElse(""),
      formColSelect.view_style.getOrElse("")
    )
  }

  private def convertToFormColValidation(userGroup: String, formColValidation: FormColValidation) = {
    ???
  }

  // ----------------------------------------------
  // selectクエリ実行
  // ----------------------------------------------
  private def selectForm(uesrGroup: String, id: BigInt): Option[Form] = {
    val f = Form.syntax("f")
    withSQL(
      select(
        f.id,
        f.hashed_id,
        f.form_index,
        f.name,
        f.title,
        f.status,
        f.cancel_url,
        f.complete_url,
        f.input_header,
        f.confirm_header,
        f.complete_text,
        f.close_text,
        f.form_data,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified)
        .from(Form as f)
        .where
        .eq(f.id, id)
        .and
        .eq(f.user_group, uesrGroup)
    ).map(rs => Form(rs)).single().apply()
  }

  private def selectFormByHashedId(userGroup: String, hashedId: String): Option[Form] = {
    val f = Form.syntax("f")
    withSQL(
      select(
        f.id,
        f.hashed_id,
        f.form_index,
        f.name,
        f.title,
        f.status,
        f.cancel_url,
        f.complete_url,
        f.input_header,
        f.confirm_header,
        f.complete_text,
        f.close_text,
        f.form_data,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified)
        .from(Form as f)
        .where
        .eq(f.hashed_id, hashedId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => Form(rs)).single().apply()
  }

  private def selectFormList(userGroup: String): List[Form] = {
    val f = Form.syntax("f")
    withSQL(
      select(
        f.id,
        f.hashed_id,
        f.form_index,
        f.name,
        f.title,
        f.status,
        f.cancel_url,
        f.complete_url,
        f.input_header,
        f.confirm_header,
        f.complete_text,
        f.close_text,
        f.form_data,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified)
        .from(Form as f)
        .where
        .eq(f.user_group, userGroup)
    ).map(rs => Form(rs)).list().apply()
  }

  private def selectFormColList(userGroup: String, formId: BigInt): List[FormCol] = {
    val f = FormCol.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_id,
        f.name,
        f.col_id,
        f.col_index,
        f.col_type,
        f.default_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormCol as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.user_group, userGroup)
        .orderBy(f.col_index)
        .asc
    ).map(rs => FormCol(rs)).list().apply()
  }

  private def selectFormTransferTaskList(userGroup: String, formId: BigInt): List[FormTransferTask] = {
    val f = FormTransferTask.syntax("f")
    withSQL(
      select(
        f.id,
        f.transfer_config_id,
        f.form_id,
        f.task_index,
        f.name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTask as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormTransferTask(rs)).list().apply()
  }

  private def selectFormColSelectList(userGroup: String, formId: BigInt, formColId: BigInt): List[FormColSelect] = {
    val f = FormColSelect.syntax("f")
    withSQL (
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.select_index,
        f.select_name,
        f.select_value,
        f.is_default,
        f.edit_style,
        f.view_style,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColSelect as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormColSelect(rs)).list().apply()
  }

  private def selectFormColValidation(userGroup: String, formId: BigInt, formColId: BigInt): Option[FormColValidation] = {
    val f = FormColValidation.syntax("f")
    withSQL (
      select(
        f.id,
        f.form_col_id,
        f.form_id,
        f.max_value,
        f.min_value,
        f.max_length,
        f.min_length,
        f.input_type,
        f.required,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormColValidation as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormColValidation(rs)).single().apply()
  }

  private def selectFormTransferTaskConditionList(userGroup: String, formId: BigInt, formTransferTaskId: BigInt): List[FormTransferTaskCondition] = {
    val f = FormTransferTaskCondition.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.form_id,
        f.form_col_id,
        f.operator,
        f.cond_value,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskCondition as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_transfer_task_id, formTransferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormTransferTaskCondition(rs)).list().apply()
  }

  private def selectFormTransferTaskMail(userGroup: String, formTransferTaskId: BigInt): Option[FormTransferTaskMail] = {
    val f = FormTransferTaskMail.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.from_address_id,
        f.to_address,
        f.cc_address,
        f.bcc_address_id,
        f.replyto_address_id,
        f.subject,
        f.body,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskMail as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskMail(rs)).single().apply()
  }

  private def selectFormTransferTaskSalesforce(userGroup: String, formTransferTaskId: BigInt): Option[FormTransferTaskSalesforce] = {
    val f = FormTransferTaskSalesforce.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_id,
        f.object_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforce as f)
        .where
        .eq(f.form_transfer_task_id, formTransferTaskId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskSalesforce(rs)).single().apply()
  }

  private def selectFormTransferTaskSalesforceFieldList(userGroup: String, formTransferTaskSalesforceId: BigInt): List[FormTransferTaskSalesforceField] = {
    val f = FormTransferTaskSalesforceField.syntax("f")
    withSQL(
      select(
        f.id,
        f.form_transfer_task_salesforce_id,
        f.form_column_id,
        f.field_name,
        f.user_group,
        f.created_user,
        f.modified_user,
        f.created,
        f.modified
      )
        .from(FormTransferTaskSalesforceField as f)
        .where
        .eq(f.form_transfer_task_salesforce_id, formTransferTaskSalesforceId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs=>FormTransferTaskSalesforceField(rs)).list().apply()
  }

  // ----------------------------------------------
  // insert / updateクエリ実行
  // ----------------------------------------------

  // ----------------------------------------------
  // deleteクエリ実行
  // ----------------------------------------------

}
