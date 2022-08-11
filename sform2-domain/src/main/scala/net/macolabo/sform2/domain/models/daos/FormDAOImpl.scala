package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}
import net.macolabo.sform2.domain.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.domain.services.Form.get.{FormColGetReponse, FormColSelectGetReponse, FormColValidationGetReponse, FormGetResponse, FormTransferTaskConditionGetReponse, FormTransferTaskGetResponse, FormTransferTaskMailGetReponse, FormTransferTaskSalesforceFieldGetReponse, FormTransferTaskSalesforceGetReponse}
import net.macolabo.sform2.domain.services.Form.list.{FormListResponse, FormResponse}
import net.macolabo.sform2.domain.services.Form.update.{FormColSelectUpdateRequest, FormColUpdateRequest, FormColValidationUpdateRequest, FormTransferTaskConditionUpdateRequest, FormTransferTaskMailUpdateRequest, FormTransferTaskSalesforceFieldUpdateRequest, FormTransferTaskSalesforceUpdateRequest, FormTransferTaskUpdateRequest, FormUpdateRequest, FormUpdateResponse}
import scalikejdbc._

import java.time.ZonedDateTime
import java.util.UUID

/**
 * FormDAO
 *
 */
class FormDAOImpl extends FormDAO {

  /** フォーム取得 */
  def get(userGroup: String, id: BigInt)(implicit session: DBSession): Option[FormGetResponse] = {
    selectForm(userGroup, id).map(form => {
      convertToFormGetResponse(userGroup, form)
    })
  }

  /** HashedIdによるフォーム取得 */
  def getByHashedId(userGroup: String, hashed_id: String)(implicit session: DBSession): Option[FormGetResponse] = {
    selectFormByHashedId(userGroup, hashed_id).map(form => {
      convertToFormGetResponse(userGroup, form)
    })
  }

  /** フォーム一覧取得 */
  def getList(userGroup: String)(implicit session: DBSession): FormListResponse = {
      val formList = selectFormList(userGroup).map(form => convertToFormResponse(form))
      FormListResponse(
        formList,
        formList.length
      )
  }

  /** フォーム作成・更新 */
  def update(userId: String, userGroup: String, request: FormUpdateRequest)(implicit session: DBSession): FormUpdateResponse = {
    val formId = request.id
      .map(_ => updateForm(userId, request))
      .getOrElse(insertForm(userGroup, userId, request))

    // FormColの処理
    val formColIds = request.form_cols.map(formCol => {
      val formColId = formCol.id
        .map(_ => updateFormCol(userId, formCol))
        .getOrElse(insertFormCol(userGroup, userId, formCol, formId))
      val formColSelectIds = formCol.select_list.map(select => {
        select.id
          .map(_ => updateFormColSelect(userId, select))
          .getOrElse(insertFormColSelect(userGroup, userId, select, formId, formColId))
      })
      formCol.validations.id
        .map(_ => updateFormColValidation(userId, formCol.validations))
        .getOrElse(insertFormColValidation(userGroup, userId, formCol.validations, formId, formColId))
      bulkDeleteFormColSelect(formId, formColId, formColSelectIds, userGroup)
      formColId
    })
    bulkDeleteFormCol(formId, formColIds, userGroup)

    // FormTransferTaskの処理
    val formTransferTaskIds = request.form_transfer_tasks.map(formTransferTask => {
      val formTransferTaskId = formTransferTask.id
        .map(_ => updateFormTransferTask(userId, formTransferTask))
        .getOrElse(insertFormTransferTask(userGroup, userId, formTransferTask, formId))
      formTransferTask.form_transfer_task_conditions.foreach(condition => {
        condition.id
          .map(_ => updateFormTransferTaskCondition(userId, condition))
          .getOrElse(insertFormTransferTaskCondition(userGroup, userId, condition, formId, formTransferTaskId))
      })

      formTransferTask.salesforce.foreach(sf => {
        val sfId = sf.id
          .map(_ => updateFormTransferTaskSalesforce(userId, sf))
          .getOrElse(insertFormTransferTaskSalesforce(userGroup, userId, sf, formTransferTaskId))
        val sffIds = sf.fields.map(sff => {
          sff.id
            .map(_ => updateFormTransferTaskSalesforceField(userId, sff))
            .getOrElse(insertFormTransferTaskSalesforceField(userGroup, userId, sff, sfId))
        })
        bulkDeleteFormTransferTaskSalesforceField(sfId, sffIds, userGroup)
      })

      formTransferTask.mail.map(mail => {
        mail.id
          .map(_ => updateFormTransferTaskMail(userId, mail))
          .getOrElse(insertFormTransferTaskMail(userGroup, userId, mail, formTransferTaskId))
      })
      formTransferTaskId
    })
    bulkDeleteFormTransferTask(formId, formTransferTaskIds, userGroup)

    FormUpdateResponse(formId)
  }

  /** フォーム削除 */
  def delete(userGroup: String, id: BigInt)(implicit session: DBSession): FormDeleteResponse = {
    FormDeleteResponse(deleteForm(userGroup, id))
  }

  /** HashedIdによるフォーム削除 */
  def deleteByHashedId(userGroup: String, hashed_id: String)(implicit session: DBSession): FormDeleteResponse = {
    FormDeleteResponse(deleteFormByHashedId(userGroup, hashed_id))
  }

  /** 更新時のSalesforceTransferTaskSalesforceField削除 */
  private def bulkDeleteFormTransferTaskSalesforceField(formTransferTaskSalesforceId: BigInt, fieldIds: List[BigInt], userGroup: String)(implicit session: DBSession): Unit = {
    val targetIds = selectFormTransferTaskSalesforceFieldList(userGroup, formTransferTaskSalesforceId).map(field => field.id).filter(field => !fieldIds.contains(field))
    targetIds.foreach(id => deleteFormTransferTaskSalesforceField(userGroup, id))
  }

  /** 更新時のTransferTask削除 */
  private def bulkDeleteFormTransferTask(formId: BigInt, taskIds: List[BigInt], userGroup: String)(implicit session: DBSession): Unit = {
    val targetIds = selectFormTransferTaskList(userGroup, formId).map(task => task.id).filter(task => !taskIds.contains(task))
    targetIds.foreach(id => deleteFormTransferTask(userGroup, id))
  }

  /** 更新時のFormCol削除 */
  private def bulkDeleteFormCol(formId: BigInt, colIds: List[BigInt], userGroup: String)(implicit session: DBSession): Unit = {
    val targetIds = selectFormColList(userGroup, formId).map(col => col.id).filter(col => !colIds.contains(col))
    targetIds.foreach(id => deleteFormCol(userGroup, id))
  }

  /** 更新時のFormColSelect削除 */
  private def bulkDeleteFormColSelect(formId: BigInt, formColId: BigInt, colSelectIds: List[BigInt], userGroup: String)(implicit session: DBSession): Unit = {
    val targetIds = selectFormColSelectList(userGroup, formId, formColId).map(colSelect => colSelect.id).filter(colSelect => !colSelectIds.contains(colSelect))
    targetIds.foreach(id => deleteFormColSelect(userGroup, id))
  }
  // ----------------------------------------------
  // レスポンス作成
  // ----------------------------------------------
  private def convertToFormGetResponse(userGroup: String, form: Form)(implicit session: DBSession): FormGetResponse = {
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

  private def convertToFormCol(userGroup: String, formCol: FormCol)(implicit session: DBSession): FormColGetReponse = {
    FormColGetReponse(
      formCol.id,
      formCol.form_id,
      formCol.name,
      formCol.col_id,
      formCol.col_index,
      formCol.col_type,
      formCol.default_value.getOrElse(""),
      selectFormColSelectList(userGroup, formCol.form_id, formCol.id).map(col => convertToFormColSelect(col)),
      selectFormColValidation(userGroup, formCol.form_id, formCol.id).map(validation => convertToFormColValidation(validation))
    )
  }

  private def convertToFormTransferTaskSalesforceField(formTransferTaskSalesforceField: FormTransferTaskSalesforceField)(implicit session: DBSession): FormTransferTaskSalesforceFieldGetReponse = {
    FormTransferTaskSalesforceFieldGetReponse(
      formTransferTaskSalesforceField.id,
      formTransferTaskSalesforceField.form_transfer_task_salesforce_id,
      formTransferTaskSalesforceField.form_column_id,
      formTransferTaskSalesforceField.field_name
    )
  }

  private def convertToFormTransferTaskSalesforce(userGroup: String, formTransferTaskSalesforce: FormTransferTaskSalesforce)(implicit session: DBSession): FormTransferTaskSalesforceGetReponse = {
    FormTransferTaskSalesforceGetReponse(
      formTransferTaskSalesforce.id,
      formTransferTaskSalesforce.form_transfer_task_id,
      formTransferTaskSalesforce.object_name,
      selectFormTransferTaskSalesforceFieldList(userGroup, formTransferTaskSalesforce.id).map(salesforceField => convertToFormTransferTaskSalesforceField(salesforceField))
    )
  }

  private def convertToFormTransferTaskMail(formTransferTaskMail: FormTransferTaskMail)(implicit session: DBSession): FormTransferTaskMailGetReponse = {
    FormTransferTaskMailGetReponse(
      formTransferTaskMail.id,
      formTransferTaskMail.form_transfer_task_id,
      formTransferTaskMail.from_address_id,
      formTransferTaskMail.to_address,
      formTransferTaskMail.cc_address,
      formTransferTaskMail.bcc_address_id,
      formTransferTaskMail.replyto_address_id,
      formTransferTaskMail.subject,
      formTransferTaskMail.body
    )
  }

  private def convertToFormTransferTaskCondition(formTransferTaskCondition: FormTransferTaskCondition)(implicit session: DBSession): FormTransferTaskConditionGetReponse = {
    FormTransferTaskConditionGetReponse(
      formTransferTaskCondition.id,
      formTransferTaskCondition.form_transfer_task_id,
      formTransferTaskCondition.form_id,
      formTransferTaskCondition.form_col_id,
      formTransferTaskCondition.operator,
      formTransferTaskCondition.cond_value
    )
  }

  private def convertToFormTransferTask(userGroup: String, formTransferTask: FormTransferTask)(implicit session: DBSession): FormTransferTaskGetResponse = {
    FormTransferTaskGetResponse(
      formTransferTask.id,
      formTransferTask.transfer_config_id,
      formTransferTask.form_id,
      formTransferTask.task_index,
      formTransferTask.name,
      selectFormTransferTaskConditionList(userGroup, formTransferTask.form_id, formTransferTask.id).map(transferTaskCondition => convertToFormTransferTaskCondition(transferTaskCondition)),
      selectFormTransferTaskMail(userGroup, formTransferTask.id).map(transferTaskMail => convertToFormTransferTaskMail(transferTaskMail)),
      selectFormTransferTaskSalesforce(userGroup, formTransferTask.id).map(formTransferTaskSalesforce => convertToFormTransferTaskSalesforce(userGroup, formTransferTaskSalesforce))
    )
  }

  private def convertToFormColSelect(formColSelect: FormColSelect)(implicit session: DBSession): FormColSelectGetReponse = {
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

  private def convertToFormColValidation(formColValidation: FormColValidation)(implicit session: DBSession): FormColValidationGetReponse = {
    FormColValidationGetReponse(
      formColValidation.id,
      formColValidation.form_col_id,
      formColValidation.form_id,
      formColValidation.max_value,
      formColValidation.min_value,
      formColValidation.max_length,
      formColValidation.min_length,
      formColValidation.input_type,
      formColValidation.required
    )
  }

  private def convertToFormResponse(form: Form): FormResponse = {
    FormResponse(
      form.id,
      form.name,
      form.form_index,
      form.title,
      form.status,
      form.hashed_id
    )
  }

  // ----------------------------------------------
  // selectクエリ実行
  // ----------------------------------------------
  private def selectForm(uesrGroup: String, id: BigInt)(implicit session: DBSession): Option[Form] = {
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

  private def selectFormByHashedId(userGroup: String, hashedId: String)(implicit session: DBSession): Option[Form] = {
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

  private def selectFormList(userGroup: String)(implicit session: DBSession): List[Form] = {
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

  private def selectFormColList(userGroup: String, formId: BigInt)(implicit session: DBSession): List[FormCol] = {
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

  private def selectFormTransferTaskList(userGroup: String, formId: BigInt)(implicit session: DBSession): List[FormTransferTask] = {
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

  private def selectFormColSelectList(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession): List[FormColSelect] = {
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

  private def selectFormColValidation(userGroup: String, formId: BigInt, formColId: BigInt)(implicit session: DBSession): Option[FormColValidation] = {
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

  private def selectFormTransferTaskConditionList(userGroup: String, formId: BigInt, formTransferTaskId: BigInt)(implicit session: DBSession): List[FormTransferTaskCondition] = {
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

  private def selectFormTransferTaskMail(userGroup: String, formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskMail] = {
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

  private def selectFormTransferTaskSalesforce(userGroup: String, formTransferTaskId: BigInt)(implicit session: DBSession): Option[FormTransferTaskSalesforce] = {
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

  private def selectFormTransferTaskSalesforceFieldList(userGroup: String, formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): List[FormTransferTaskSalesforceField] = {
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
  // insertクエリ実行
  // ----------------------------------------------
  def insertForm(userGroup: String, user: String, form: FormUpdateRequest)(implicit session: DBSession): BigInt = {
    val formHashedId = UUID.randomUUID().toString
    withSQL {
      val c = Form.column
      insertInto(Form).namedValues(
        c.hashed_id -> formHashedId,
        c.form_index -> form.form_index,
        c.name -> form.name,
        c.title -> form.title,
        c.status -> form.status,
        c.cancel_url -> form.cancel_url,
        c.complete_url -> form.complete_url,
        c.input_header -> form.input_header,
        c.confirm_header -> form.confirm_header,
        c.complete_text -> form.complete_text,
        c.close_text -> form.close_text,
        c.form_data -> "{}",
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertFormCol(userGroup: String, user: String, formCol: FormColUpdateRequest, formId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormCol.column
      insertInto(FormCol).namedValues(
        c.form_id -> formId,
        c.name -> formCol.name,
        c.col_id -> formCol.col_id,
        c.col_index -> formCol.col_index,
        c.col_type -> formCol.col_type,
        c.default_value -> formCol.default_value,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  def insertFormColValidation(userGroup: String, user: String, formColValidation: FormColValidationUpdateRequest, formId: BigInt, formColId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormColValidation.column
      insertInto(FormColValidation).namedValues(
        c.form_col_id -> formColId,
        c.form_id -> formId,
        c.max_value -> formColValidation.max_value,
        c.min_value -> formColValidation.min_value,
        c.max_length -> formColValidation.max_length,
        c.min_length -> formColValidation.min_length,
        c.input_type -> formColValidation.input_type,
        c.required -> formColValidation.required,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  def insertFormColSelect(userGroup: String, user: String, formColSelect: FormColSelectUpdateRequest, formId: BigInt, formColId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormColSelect.column
      insertInto(FormColSelect).namedValues(
        c.form_col_id -> formColId,
        c.form_id -> formId,
        c.select_index -> formColSelect.select_index,
        c.select_name -> formColSelect.select_name,
        c.select_value -> formColSelect.select_value,
        c.is_default -> formColSelect.is_default,
        c.edit_style -> formColSelect.edit_style,
        c.view_style -> formColSelect.view_style,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertFormTransferTask(userGroup: String, user: String, formTransferTask: FormTransferTaskUpdateRequest, formId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTask.column
      insertInto(FormTransferTask)
        .namedValues(
          c.transfer_config_id -> formTransferTask.transfer_config_id,
          c.form_id -> formId,
          c.task_index -> formTransferTask.task_index,
          c.name -> formTransferTask.name,
          c.user_group -> userGroup,
          c.created_user -> user,
          c.modified_user -> user,
          c.created -> ZonedDateTime.now(),
          c.modified -> ZonedDateTime.now()
        )
    }.updateAndReturnGeneratedKey().apply()
  }

  def insertFormTransferTaskCondition(userGroup: String, user: String, formTransferTaskCondition: FormTransferTaskConditionUpdateRequest, formId: BigInt, formTransferTaskId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTaskCondition.column
      insertInto(FormTransferTaskCondition)
        .namedValues(
          c.form_transfer_task_id -> formTransferTaskId,
          c.form_id -> formId,
          c.form_col_id -> formTransferTaskCondition.form_col_id,
          c.operator -> formTransferTaskCondition.operator,
          c.cond_value -> formTransferTaskCondition.cond_value,
          c.user_group -> userGroup,
          c.created_user -> user,
          c.modified_user -> user,
          c.created -> ZonedDateTime.now(),
          c.modified -> ZonedDateTime.now()
        )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertFormTransferTaskMail(userGroup: String, user: String, formTransferTaskMail: FormTransferTaskMailUpdateRequest, formTransferTaskId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormTransferTaskMail.column
      insertInto(FormTransferTaskMail).namedValues(
        c.form_transfer_task_id -> formTransferTaskId,
        c.from_address_id -> formTransferTaskMail.from_address_id,
        c.to_address -> formTransferTaskMail.to_address,
        c.cc_address -> formTransferTaskMail.cc_address,
        c.bcc_address_id -> formTransferTaskMail.bcc_address_id,
        c.replyto_address_id -> formTransferTaskMail.replyto_address_id,
        c.subject -> formTransferTaskMail.subject,
        c.body -> formTransferTaskMail.body,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertFormTransferTaskSalesforce(userGroup: String, user: String, formTransferTaskSalesforce: FormTransferTaskSalesforceUpdateRequest, formTransferTaskId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      insertInto(FormTransferTaskSalesforce).namedValues(
        c.form_transfer_task_id -> formTransferTaskId,
        c.object_name -> formTransferTaskSalesforce.object_name,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  def insertFormTransferTaskSalesforceField(userGroup: String, user: String, formTransferTaskSalesforceField: FormTransferTaskSalesforceFieldUpdateRequest, formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTaskSalesforceField.column
      insertInto(FormTransferTaskSalesforceField).namedValues(
        c.form_transfer_task_salesforce_id -> formTransferTaskSalesforceId,
        c.form_column_id -> formTransferTaskSalesforceField.form_column_id,
        c.field_name -> formTransferTaskSalesforceField.field_name,
        c.user_group -> userGroup,
        c.created_user -> user,
        c.modified_user -> user,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply().toInt
  }

  // ----------------------------------------------
  // updateクエリ実行
  // ----------------------------------------------
  def updateForm(user: String, form: FormUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = Form.column
      QueryDSL.update(Form).set(
        c.form_index -> form.form_index,
        c.name -> form.name,
        c.title -> form.title,
        c.status -> form.status,
        c.cancel_url -> form.cancel_url,
        c.complete_url -> form.complete_url,
        c.input_header -> form.input_header,
        c.confirm_header -> form.confirm_header,
        c.complete_text -> form.complete_text,
        c.close_text -> form.close_text,
        c.form_data -> "{}",
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, form.id)
    }.update().apply()
    form.id.get
  }

  def updateFormCol(user: String, formCol: FormColUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormCol.column
      QueryDSL.update(FormCol).set(
        c.form_id -> formCol.form_id,
        c.name -> formCol.name,
        c.col_id -> formCol.col_id,
        c.col_index -> formCol.col_index,
        c.col_type -> formCol.col_type,
        c.default_value -> formCol.default_value,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formCol.id)
    }.update().apply()
    formCol.id.get
  }

  def updateFormColValidation(user: String, formColValidation: FormColValidationUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormColValidation.column
      QueryDSL.update(FormColValidation).set(
        c.form_col_id -> formColValidation.form_col_id,
        c.form_id -> formColValidation.form_id,
        c.max_value -> formColValidation.max_value,
        c.min_value -> formColValidation.min_value,
        c.max_length -> formColValidation.max_length,
        c.min_length -> formColValidation.min_length,
        c.input_type -> formColValidation.input_type,
        c.required -> formColValidation.required,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formColValidation.id)
    }.update().apply()
    formColValidation.id.get
  }

  def updateFormColSelect(user: String, formColSelect: FormColSelectUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormColSelect.column
      QueryDSL.update(FormColSelect).set(
        c.form_col_id -> formColSelect.form_col_id,
        c.form_id -> formColSelect.form_id,
        c.select_index -> formColSelect.select_index,
        c.select_name -> formColSelect.select_name,
        c.select_value -> formColSelect.select_value,
        c.is_default -> formColSelect.is_default,
        c.edit_style -> formColSelect.edit_style,
        c.view_style -> formColSelect.view_style,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formColSelect.id)
    }.update().apply()
    formColSelect.id.get
  }

  def updateFormTransferTask(user: String, formTransferTask: FormTransferTaskUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTask.column
      QueryDSL.update(FormTransferTask)
        .set(
          c.transfer_config_id -> formTransferTask.transfer_config_id,
          c.form_id -> formTransferTask.form_id,
          c.task_index -> formTransferTask.task_index,
          c.name -> formTransferTask.name,
          c.modified_user -> user,
          c.modified -> ZonedDateTime.now()
        ).where.eq(c.id, formTransferTask.id)
    }.update().apply()
    formTransferTask.id.get
  }

  def updateFormTransferTaskCondition(user: String, formTransferTaskCondition: FormTransferTaskConditionUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTaskCondition.column
      QueryDSL.update(FormTransferTaskCondition)
        .set(
          c.form_transfer_task_id -> formTransferTaskCondition.form_transfer_task_id,
          c.form_id -> formTransferTaskCondition.form_id,
          c.form_col_id -> formTransferTaskCondition.form_col_id,
          c.operator -> formTransferTaskCondition.operator,
          c.cond_value -> formTransferTaskCondition.cond_value,
          c.modified_user -> user,
          c.modified -> ZonedDateTime.now()
        ).where.eq(c.id, formTransferTaskCondition.id)
    }.update().apply()
    formTransferTaskCondition.id.get
  }

  def updateFormTransferTaskMail(user: String, formTransferTaskMail: FormTransferTaskMailUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormTransferTaskMail.column
      QueryDSL.update(FormTransferTaskMail).set(
        c.form_transfer_task_id -> formTransferTaskMail.form_transfer_task_id,
        c.from_address_id -> formTransferTaskMail.from_address_id,
        c.to_address -> formTransferTaskMail.to_address,
        c.cc_address -> formTransferTaskMail.cc_address,
        c.bcc_address_id -> formTransferTaskMail.bcc_address_id,
        c.replyto_address_id -> formTransferTaskMail.replyto_address_id,
        c.subject -> formTransferTaskMail.subject,
        c.body -> formTransferTaskMail.body,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formTransferTaskMail.id)
    }.update().apply()
    formTransferTaskMail.id.get
  }

  def updateFormTransferTaskSalesforce(user: String, formTransferTaskSalesforce: FormTransferTaskSalesforceUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      QueryDSL.update(FormTransferTaskSalesforce).set(
        c.form_transfer_task_id -> formTransferTaskSalesforce.form_transfer_task_id,
        c.object_name -> formTransferTaskSalesforce.object_name,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formTransferTaskSalesforce.id)
    }.update().apply()
    formTransferTaskSalesforce.id.get
  }

  def updateFormTransferTaskSalesforceField(user: String, formTransferTaskSalesforceField: FormTransferTaskSalesforceFieldUpdateRequest)(implicit session: DBSession): BigInt = {
    withSQL {
      val c = FormTransferTaskSalesforceField.column
      QueryDSL.update(FormTransferTaskSalesforceField).set(
        c.form_transfer_task_salesforce_id -> formTransferTaskSalesforceField.form_transfer_task_salesforce_id,
        c.form_column_id -> formTransferTaskSalesforceField.form_column_id,
        c.field_name -> formTransferTaskSalesforceField.field_name,
        c.modified_user -> user,
        c.modified -> ZonedDateTime.now()
      ).where.eq(c.id, formTransferTaskSalesforceField.id)
    }.update().apply()
    formTransferTaskSalesforceField.id.get
  }

  // ----------------------------------------------
  // deleteクエリ実行
  // ----------------------------------------------
  def deleteForm(userGroup:String, formId: BigInt)(implicit session: DBSession): Int = {
    withSQL {
      val c = Form.column
      QueryDSL.delete.from(Form).where.eq(c.id, formId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormByHashedId(userGroup:String, hashedFormId: String)(implicit session: DBSession): Int = {
    withSQL {
      val c = Form.column
      QueryDSL.delete.from(Form).where.eq(c.hashed_id, hashedFormId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormCol(userGroup: String, formColId: BigInt)(implicit session: DBSession): Int = {
    withSQL {
      val c = FormCol.column
      QueryDSL.delete.from(FormCol).where.eq(c.id, formColId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormColValidation(userGroup: String, formColValidationId: BigInt)(implicit session: DBSession): Int = {
    withSQL {
      val c = FormColValidation.column
      QueryDSL.delete.from(FormColValidation).where.eq(c.id, formColValidationId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormColSelect(userGroup: String, formColSelectId: BigInt)(implicit session: DBSession): Int = {
    withSQL {
      val c = FormColSelect.column
      QueryDSL.delete.from(FormColSelect).where.eq(c.id, formColSelectId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormTransferTask(userGroup: String, formTransferTaskId: BigInt)(implicit session: DBSession): Int = {
    withSQL{
      val c = FormTransferTask.column
      QueryDSL.delete.from(FormTransferTask).where.eq(c.id, formTransferTaskId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
  def deleteFormTransferTaskCondition(userGroup: String, formTransferTaskConditionId: BigInt)(implicit session: DBSession): Int = {
    withSQL{
      val c = FormTransferTaskCondition.column
      QueryDSL.delete.from(FormTransferTaskCondition).where.eq(c.id, formTransferTaskConditionId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormTransferTaskMail(userGroup: String, formTransferTaskMailId: BigInt)(implicit session: DBSession): Int = {
    withSQL{
      val c = FormTransferTaskMail.column
      QueryDSL.delete.from(FormTransferTaskMail).where.eq(c.id, formTransferTaskMailId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormTransferTaskSalesforce(userGroup: String, formTransferTaskSalesforceId: BigInt)(implicit session: DBSession): Int = {
    withSQL{
      val c = FormTransferTaskSalesforce.column
      QueryDSL.delete.from(FormTransferTaskSalesforce).where.eq(c.id, formTransferTaskSalesforceId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }

  def deleteFormTransferTaskSalesforceField(userGroup: String, formTransferTaskSalesforceFieldId: BigInt)(implicit session: DBSession): Int = {
    withSQL{
      val c = FormTransferTaskSalesforceField.column
      QueryDSL.delete.from(FormTransferTaskSalesforceField).where.eq(c.id, formTransferTaskSalesforceFieldId).and.eq(c.user_group, userGroup)
    }.update().apply()
  }
}
