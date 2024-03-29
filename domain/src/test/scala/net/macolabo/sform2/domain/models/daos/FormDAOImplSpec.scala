package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol, FormColSelect, FormColValidation}
import net.macolabo.sform2.domain.models.entity.formtransfertask.{FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfig
import net.macolabo.sform2.domain.models.entity.user.User
import net.macolabo.sform2.domain.services.Form.get.{FormColGetReponse, FormColSelectGetReponse, FormColValidationGetReponse, FormTransferTaskConditionGetReponse, FormTransferTaskGetResponse, FormTransferTaskMailGetReponse, FormTransferTaskSalesforceFieldGetReponse, FormTransferTaskSalesforceGetReponse}
import net.macolabo.sform2.domain.services.Form.update.{FormColSelectUpdateRequest, FormColUpdateRequest, FormColValidationUpdateRequest, FormTransferTaskConditionUpdateRequest, FormTransferTaskMailUpdateRequest, FormTransferTaskSalesforceFieldUpdateRequest, FormTransferTaskSalesforceUpdateRequest, FormTransferTaskUpdateRequest, FormUpdateRequest}
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc._
import scalikejdbc.interpolation.SQLSyntax.count
import scalikejdbc.scalatest.AutoRollback

import java.time.ZonedDateTime
import java.util.UUID
import scala.collection.compat.Factory

// TODO FormDAO分割後に書き直す
class FormDAOImplSpec extends FixtureAnyFlatSpec with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {

  private val userId = UUID.randomUUID()
  private val user = User(userId, "hoge", "hoge", Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge@hoge.com"), None, activated = true, deletable = false)
  val formId: BigInt = BigInt(100)
  val transferConfigId: BigInt = BigInt(100)

  behavior of "Form"

  it should "select form detail" in { implicit session =>

    val formDAO = new FormDAOImpl()
    val form = formDAO.get(user.user_group.get, formId)

    assert(form.nonEmpty)
    form.map(f => {
      assert(f.id.isValidLong)
      assert(f.hashed_id.nonEmpty)
      assert(f.form_index.isValidInt)
      assert(f.name.nonEmpty)
      assert(f.title.nonEmpty)
      assert(f.status.isValidInt)
      assert(f.cancel_url.nonEmpty)
      assert(f.complete_url.nonEmpty)
      assert(f.input_header.nonEmpty)
      assert(f.confirm_header.nonEmpty)
      assert(f.complete_text.nonEmpty)
      assert(f.close_text.nonEmpty)
      assert(f.form_cols.nonEmpty)
      assert(f.form_transfer_tasks.nonEmpty)

      val formCol = f.form_cols.head
      assert(formCol.id.isValidLong)
      assert(formCol.form_id.isValidLong)
      assert(formCol.name.nonEmpty)
      assert(formCol.col_id.nonEmpty)
      assert(formCol.col_index.isValidInt)
      assert(formCol.col_type.isValidInt)
      assert(formCol.default_value.nonEmpty)
      assert(formCol.select_list.nonEmpty)
      assert(formCol.validations.nonEmpty)

      val formColSelect = formCol.select_list.head
      assert(formColSelect.id.isValidLong)
      assert(formColSelect.form_col_id.isValidLong)
      assert(formColSelect.form_id.isValidLong)
      assert(formColSelect.select_index.isValidInt)
      assert(formColSelect.select_name.nonEmpty)
      assert(formColSelect.select_value.nonEmpty)
      assert(formColSelect.is_default)

      val formColValidation = formCol.validations.get
      assert(formColValidation.id.isValidLong)
      assert(formColValidation.form_col_id.isValidLong)
      assert(formColValidation.form_id.isValidLong)
      assert(formColValidation.max_value.nonEmpty)
      assert(formColValidation.min_value.nonEmpty)
      assert(formColValidation.max_length.nonEmpty)
      assert(formColValidation.min_length.nonEmpty)
      assert(formColValidation.input_type.isValidInt)
      assert(!formColValidation.required)

      val formTransferTask = f.form_transfer_tasks.head
      assert(formTransferTask.id.isValidLong)
      assert(formTransferTask.transfer_config_id.isValidLong)
      assert(formTransferTask.form_id.isValidLong)
      assert(formTransferTask.task_index.isValidInt)
      assert(formTransferTask.name.nonEmpty)
      assert(formTransferTask.form_transfer_task_conditions.nonEmpty)
      assert(formTransferTask.mail.nonEmpty)
      assert(formTransferTask.salesforce.nonEmpty)

      val formTransferTaskCondition = formTransferTask.form_transfer_task_conditions.head
      assert(formTransferTaskCondition.id.isValidLong)
      assert(formTransferTaskCondition.form_transfer_task_id.isValidLong)
      assert(formTransferTaskCondition.form_id.isValidLong)
      assert(formTransferTaskCondition.form_col_id.isValidLong)
      assert(formTransferTaskCondition.operator.nonEmpty)
      assert(formTransferTaskCondition.cond_value.nonEmpty)

      val formTransferTaskMail = formTransferTask.mail.get
      assert(formTransferTaskMail.id.isValidLong)
      assert(formTransferTaskMail.form_transfer_task_id.isValidLong)
      assert(formTransferTaskMail.from_address_id.isValidLong)
      assert(formTransferTaskMail.to_address.nonEmpty)
      assert(formTransferTaskMail.to_address_id.nonEmpty)
      assert(formTransferTaskMail.to_address_field.nonEmpty)
      assert(formTransferTaskMail.cc_address.nonEmpty)
      assert(formTransferTaskMail.cc_address_id.nonEmpty)
      assert(formTransferTaskMail.cc_address_field.nonEmpty)
      assert(formTransferTaskMail.bcc_address_id.get.isValidLong)
      assert(formTransferTaskMail.replyto_address_id.get.isValidLong)
      assert(formTransferTaskMail.subject.nonEmpty)
      assert(formTransferTaskMail.body.nonEmpty)

      val formTransferTaskSalesforce = formTransferTask.salesforce.get
      assert(formTransferTaskSalesforce.id.isValidLong)
      assert(formTransferTaskSalesforce.form_transfer_task_id.isValidLong)
      assert(formTransferTaskSalesforce.object_name.nonEmpty)
      assert(formTransferTaskSalesforce.fields.nonEmpty)

      val formTransferTaskSalesforceField = formTransferTaskSalesforce.fields.head
      assert(formTransferTaskSalesforceField.id.isValidLong)
      assert(formTransferTaskSalesforceField.form_transfer_task_salesforce_id.isValidLong)
      assert(formTransferTaskSalesforceField.form_column_id.nonEmpty)
      assert(formTransferTaskSalesforceField.field_name.nonEmpty)
    })
  }

  it should "select form list" in { implicit session =>
    val formDAO = new FormDAOImpl()
    val formList = formDAO.getList(user.user_group.get)
    assert(formList.forms.nonEmpty)
    val form = formList.forms.head
    assert(form.id.isValidLong)
    assert(form.hashed_id.nonEmpty)
    assert(form.form_index.isValidInt)
    assert(form.name.nonEmpty)
    assert(form.status.isValidInt)
    assert(form.title.nonEmpty)
  }

  it should "create form" in { implicit session =>
    val formColSelectUpdateRequest = createFormColSelectUpdateRequest()
    val formColValidationUpdateRequest = createFormColValidationUpdateRequest()
    val formColUpdateRequest = createFormColUpdateRequest(List(formColSelectUpdateRequest), formColValidationUpdateRequest)
    val formUpdateRequest = createFormUpdateRequest(None, List(formColUpdateRequest), List.empty)

    val formDAO = new FormDAOImpl()

    // フォームを作成する（作成時にはFormTransferTaskは存在しない）
    val response = formDAO.update(user.id.toString, user.user_group.get, formUpdateRequest)
    val newFormId = response.id
    assert(newFormId.isValidLong)

    // 作成したフォームを取得
    val newForm = formDAO.get(user.user_group.get, newFormId).get
    assert(newForm.id.equals(newFormId))
    assert(newForm.name.equals(formUpdateRequest.name))
    assert(newForm.form_index.equals(formUpdateRequest.form_index))
    assert(newForm.status.equals(formUpdateRequest.status))
    assert(newForm.title.equals(formUpdateRequest.title))
    assert(newForm.input_header.equals(formUpdateRequest.input_header))
    assert(newForm.confirm_header.equals(formUpdateRequest.confirm_header))
    assert(newForm.complete_text.equals(formUpdateRequest.complete_text))
    assert(newForm.complete_url.equals(formUpdateRequest.complete_url))
    assert(newForm.close_text.equals(formUpdateRequest.close_text))
    assert(newForm.cancel_url.equals(formUpdateRequest.cancel_url))
    assert(newForm.form_cols.nonEmpty)
    assert(newForm.form_transfer_tasks.isEmpty)

    val newFormCol = newForm.form_cols.head
    assert(newFormCol.form_id.equals(newFormId))
    assert(newFormCol.id.isValidLong)
    assert(newFormCol.col_id.equals(formColUpdateRequest.col_id))
    assert(newFormCol.name.equals(formColUpdateRequest.name))
    assert(newFormCol.col_index.equals(formColUpdateRequest.col_index))
    assert(newFormCol.default_value.equals(formColUpdateRequest.default_value))
    assert(newFormCol.col_type.equals(formColUpdateRequest.col_type))
    assert(newFormCol.validations.nonEmpty)

    val newFormColValidation = newForm.form_cols.head.validations.get
    assert(newFormColValidation.id.isValidLong)
    assert(newFormColValidation.form_col_id.isValidLong)
    assert(newFormColValidation.form_id.equals(newFormId))
    assert(newFormColValidation.input_type.equals(formColValidationUpdateRequest.input_type))
    assert(newFormColValidation.min_length.equals(formColValidationUpdateRequest.min_length))
    assert(newFormColValidation.max_length.equals(formColValidationUpdateRequest.max_length))
    assert(newFormColValidation.min_value.equals(formColValidationUpdateRequest.min_value))
    assert(newFormColValidation.max_value.equals(formColValidationUpdateRequest.max_value))

    val newFormColSelect = newForm.form_cols.head.select_list.head
    assert(newFormColSelect.id.isValidLong)
    assert(newFormColSelect.form_id.equals(newFormId))
    assert(newFormColSelect.form_col_id.isValidLong)
    assert(newFormColSelect.select_index.equals(formColSelectUpdateRequest.select_index))
    assert(newFormColSelect.select_name.equals(formColSelectUpdateRequest.select_name))
    assert(newFormColSelect.select_value.equals(formColSelectUpdateRequest.select_value))
    assert(newFormColSelect.is_default.equals(formColSelectUpdateRequest.is_default))
    assert(newFormColSelect.view_style.equals(formColSelectUpdateRequest.view_style))
    assert(newFormColSelect.edit_style.equals(formColSelectUpdateRequest.edit_style))
  }

  it should "update form" in { implicit session =>
    val formDAO = new FormDAOImpl()

    // DBにあるフォームを取得
    val form = formDAO.get(user.user_group.get, formId).get

    // 5つあるFormColSelectのうち最後尾を削除
    val formColSelectUpdateRequest = form.form_cols.head.select_list.dropRight(1).map(formColSelect => createFormColSelectUpdateRequest(formColSelect))

    // 5つあるFormColのうち最後尾を削除
    val headFormCol = form.form_cols.head
    val formColUpdateRequest =
      List(createFormColUpdateRequest(headFormCol, formColSelectUpdateRequest, createFormColValidationUpdateRequest(headFormCol.validations.get))) :::
      form.form_cols.drop(1).dropRight(1).map(formCol => createFormColUpdateRequest(formCol, List.empty, createFormColValidationUpdateRequest(formCol.validations.get)))

    // 5つあるFormTransferTaskSalesforceFieldのうち最後尾を削除
    val formTransferTaskSalesforceFieldUpdateRequest =
      form.form_transfer_tasks.head.salesforce.get.fields.dropRight(1).map(field => createFormTransferTaskSalesforceFieldUpdateRequest(field))

    val formTransferTaskSalesforceUpdateRequest =
      form.form_transfer_tasks.head.salesforce.map(sf => createFormTransferTaskSalesforceUpdateRequest(sf, formTransferTaskSalesforceFieldUpdateRequest))
    val formTransferTaskMailUpdateRequest =
      form.form_transfer_tasks.head.mail.map(mail => createFormTransferTaskMailUpdateRequest(mail))
    val formTransferTaskConditionUpdateRequest =
      form.form_transfer_tasks.head.form_transfer_task_conditions.map(condition => createFormTransferTaskConditionUpdateRequest(condition))

    // 5つあるFormTransferTaskのうち最後尾を削除
    val formTransferTaskUpdateRequest = form.form_transfer_tasks.dropRight(1)
      .map(formTransfer => createFormTransferTaskUpdateRequest(formTransfer, formTransferTaskConditionUpdateRequest, formTransferTaskMailUpdateRequest, formTransferTaskSalesforceUpdateRequest))
    val formUpdateRequest = createFormUpdateRequest(Some(form.id), formColUpdateRequest, formTransferTaskUpdateRequest)

    // フォームを更新する
    val response = formDAO.update(user.id.toString, user.user_group.get, formUpdateRequest)
    val newFormId = response.id
    assert(newFormId.equals(formId))

    // 作成したフォームを取得
    val newForm = formDAO.get(user.user_group.get, newFormId).get

    // Formのチェック
    assert(newForm.name.equals("hoge2"))
    assert(newForm.form_index.equals(2))
    assert(newForm.title.equals("hoge2"))
    assert(newForm.status.equals(1))
    assert(newForm.cancel_url.equals("hoge2"))
    assert(newForm.close_text.equals("hoge2"))
    assert(newForm.complete_url.equals("hoge2"))
    assert(newForm.input_header.equals("hoge2"))
    assert(newForm.complete_text.equals("hoge2"))
    assert(newForm.confirm_header.equals("hoge2"))
    // FormColを一つ削除したので4つになるはず
    assert(newForm.form_cols.length.equals(4))
    // FormTransferTaskを一つ削除したので4つになるはず
    assert(newForm.form_transfer_tasks.length.equals(4))

    // FormColのチェック
    val newFormCol = newForm.form_cols.head
    assert(newFormCol.name.equals("fuga2"))
    assert(newFormCol.col_id.equals("fuga2"))
    assert(newFormCol.col_index.equals(2))
    assert(newFormCol.col_type.equals(2))
    assert(newFormCol.default_value.equals("x"))
    // FormColSelectを一つ削除したので4つになるはず
    assert(newFormCol.select_list.length.equals(4))

    // FormColSelectのチェック
    val newFormColSelect = newFormCol.select_list.head
    assert(newFormColSelect.select_index.equals(2))
    assert(newFormColSelect.select_name.equals("hoge2"))
    assert(newFormColSelect.select_value.equals("hoge2"))
    assert(newFormColSelect.is_default.equals(true))
    assert(newFormColSelect.edit_style.equals("hogehoge"))
    assert(newFormColSelect.view_style.equals("hogehoge"))

    // FormColValidationのチェック
    val newFormColValidation = newFormCol.validations.get
    assert(newFormColValidation.max_value.get.equals(11))
    assert(newFormColValidation.min_value.get.equals(2))
    assert(newFormColValidation.max_length.get.equals(11))
    assert(newFormColValidation.min_length.get.equals(3))
    assert(newFormColValidation.input_type.equals(2))
    assert(newFormColValidation.required.equals(true))

    // FormTransferTaskのチェック
    val newFormTransferTask = newForm.form_transfer_tasks.head
    assert(newFormTransferTask.task_index.equals(2))
    assert(newFormTransferTask.name.equals("hoge2"))
    assert(newFormTransferTask.form_transfer_task_conditions.nonEmpty)
    assert(newFormTransferTask.mail.nonEmpty)
    assert(newFormTransferTask.salesforce.nonEmpty)

    // FormTransferTaskConditionのチェック
    val newFormTransferTaskCondition = newFormTransferTask.form_transfer_task_conditions.head
    assert(newFormTransferTaskCondition.operator.equals("eq"))
    assert(newFormTransferTaskCondition.cond_value.equals("x"))

    // FormTransferTaskMailのチェック
    val newFormTransferTaskMail = newFormTransferTask.mail.get
    println(newFormTransferTaskMail)
    assert(newFormTransferTaskMail.from_address_id.equals(2))
    assert(newFormTransferTaskMail.to_address.get.equals("hoge2@hoge.com"))
    assert(newFormTransferTaskMail.to_address_id.get.equals(11L))
    assert(newFormTransferTaskMail.to_address_field.get.equals("mail2"))
    assert(newFormTransferTaskMail.cc_address.get.equals("fuga2@hoge.com"))
    assert(newFormTransferTaskMail.cc_address_id.get.equals(22L))
    assert(newFormTransferTaskMail.cc_address_field.get.equals("email2"))
    assert(newFormTransferTaskMail.bcc_address_id.get.equals(2))
    assert(newFormTransferTaskMail.replyto_address_id.get.equals(2))
    assert(newFormTransferTaskMail.subject.equals("hogehoge2"))
    assert(newFormTransferTaskMail.body.equals("fugafuga2"))

    // FormTransferTaskSalesforceのチェック
    val newFormTransferTaskSalesforce = newFormTransferTask.salesforce.get
    assert(newFormTransferTaskSalesforce.object_name.equals("fuga"))
    // FormTransferTaskSalesforceFieldを一つ削除したので4つになるはず
    assert(newFormTransferTask.salesforce.get.fields.length.equals(4))

    // FormTransferTaskSalesforceFieldのチェック
    val newFormTransferTaskSalesforceField = newFormTransferTaskSalesforce.fields.head
    assert(newFormTransferTaskSalesforceField.form_column_id.equals("hoge2"))
    assert(newFormTransferTaskSalesforceField.field_name.equals("fuga2"))
  }

  it should "delete form" in { implicit session =>
    val formDAO = new FormDAOImpl()

    // 削除前にIDを調べておく
    val col = FormCol.syntax("col")
    val colId = withSQL {
      select(
        col.id
      )
        .from(FormCol as col)
        .where
        .eq(col.form_id, formId)
    }.map(_.long(1)).list().apply().head

    val validation = FormColValidation.syntax("validation")
    val formColValidationId = withSQL {
      select(
        validation.id
      )
        .from(FormColValidation as validation)
        .where
        .eq(validation.form_col_id, colId)
    }.map(_.long(1)).single().apply().get

    val colSelect = FormColSelect.syntax("colSelect")
    val formColSelectId = withSQL {
      select(
        colSelect.id
      )
        .from(FormColSelect as colSelect)
        .where
        .eq(colSelect.form_col_id, colId)
    }.map(_.long(1)).list().apply().head

    val transferTask = FormTransferTask.syntax("transferTask")
    val transferTaskCondition = FormTransferTaskCondition.syntax("transferTaskCondition")
    val transferTaskMail = FormTransferTaskMail.syntax("transferTaskMail")
    val transferTaskSalesforce = FormTransferTaskSalesforce.syntax("transferTaskSalesforce")
    val transferTaskSalesforceField = FormTransferTaskSalesforceField.syntax("transferTaskSalesforceField")

    // 削除実行
    formDAO.deleteForm("hoge", formId)

    // assert
    val f = Form.syntax("f")
    val formCount = withSQL {
      select(
        count
      )
        .from(Form as f)
        .where
        .eq(f.id, formId)
    }.map(_.int(1)).single().apply().get
    assert(formCount.equals(0))

    val formColCount = withSQL {
      select(
        count
      )
        .from(FormCol as col)
        .where
        .eq(col.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formColCount.equals(0))

    val formColValidationCount = withSQL {
      select(
        count
      )
        .from(FormColValidation as validation)
        .where
        .eq(validation.id, formColValidationId)
    }.map(_.int(1)).single().apply().get
    assert(formColValidationCount.equals(0))

    val formColSelectCount = withSQL {
      select(
        count
      )
        .from(FormColSelect as colSelect)
        .where
        .eq(colSelect.id, formColSelectId)
    }.map(_.int(1)).single().apply().get
    assert(formColSelectCount.equals(0))

    val formTransferTaskCount = withSQL {
      select(
        count
      )
        .from(FormTransferTask as transferTask)
        .where
        .eq(transferTask.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formTransferTaskCount.equals(0))

    val formTransferTaskConditionCount = withSQL {
      select(
        count
      )
        .from(FormTransferTaskCondition as transferTaskCondition)
        .where
        .eq(transferTaskCondition.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formTransferTaskConditionCount.equals(0))

    val formTransferTaskMailCount = withSQL {
      select(
        count
      )
        .from(FormTransferTaskMail as transferTaskMail)
        .where
        .eq(transferTaskMail.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formTransferTaskMailCount.equals(0))

    val formTransferTaskSalesforceCount = withSQL {
      select(
        count
      )
        .from(FormTransferTaskSalesforce as transferTaskSalesforce)
        .where
        .eq(transferTaskSalesforce.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formTransferTaskSalesforceCount.equals(0))

    val formTransferTaskSalesforceFieldCount = withSQL {
      select(
        count
      )
        .from(FormTransferTaskSalesforceField as transferTaskSalesforceField)
        .where
        .eq(transferTaskSalesforceField.id, colId)
    }.map(_.int(1)).single().apply().get
    assert(formTransferTaskSalesforceFieldCount.equals(0))

  }

  // Form作成・更新リクエストの作成
  private def createFormColSelectUpdateRequest() = {
    FormColSelectUpdateRequest(
      id = None,
      form_col_id = None,
      form_id = Some(0),
      select_index = 1,
      select_name = "hoge2",
      select_value = "hoge2",
      is_default = true,
      edit_style = "",
      view_style = ""
    )
  }

  private def createFormColSelectUpdateRequest(formColSelectGetResponse: FormColSelectGetReponse) = {
    FormColSelectUpdateRequest(
      id = Some(formColSelectGetResponse.id),
      form_col_id = Some(formColSelectGetResponse.form_col_id),
      form_id = Some(formColSelectGetResponse.form_id),
      select_index = 2,
      select_name = "hoge2",
      select_value = "hoge2",
      is_default = true,
      edit_style = "hogehoge",
      view_style = "hogehoge"
    )
  }

  private def createFormColValidationUpdateRequest() = {
    FormColValidationUpdateRequest(
      id = None,
      form_col_id = None,
      form_id = Some(0),
      max_value = Some(10),
      min_value = Some(1),
      max_length = Some(10),
      min_length = Some(2),
      input_type = 1,
      required = true
    )
  }

  private def createFormColValidationUpdateRequest(formColValidationGetResponse: FormColValidationGetReponse) = {
    FormColValidationUpdateRequest(
      id = Some(formColValidationGetResponse.id),
      form_col_id = Some(formColValidationGetResponse.form_col_id),
      form_id = Some(formColValidationGetResponse.form_id),
      max_value = Some(11),
      min_value = Some(2),
      max_length = Some(11),
      min_length = Some(3),
      input_type = 2,
      required = true
    )
  }

  private def createFormColUpdateRequest(formColSelectUpdateRequestList: List[FormColSelectUpdateRequest], formColValidationUpdateRequest: FormColValidationUpdateRequest) = {
    FormColUpdateRequest(
      id = None,
      form_id = Some(0),
      name = "fuga2",
      col_id = "fuga2",
      col_index = 1,
      col_type = 1,
      default_value = "x",
      select_list = formColSelectUpdateRequestList,
      validations = formColValidationUpdateRequest
    )
  }

  private def createFormColUpdateRequest(formColGetResponse: FormColGetReponse, formColSelectUpdateRequestList: List[FormColSelectUpdateRequest], formColValidationUpdateRequest: FormColValidationUpdateRequest) = {
    FormColUpdateRequest(
      id = Some(formColGetResponse.id),
      form_id = Some(formColGetResponse.form_id),
      name = "fuga2",
      col_id ="fuga2",
      col_index = 2,
      col_type = 2,
      default_value = "x",
      select_list = formColSelectUpdateRequestList,
      validations = formColValidationUpdateRequest
    )
  }

  private def createFormUpdateRequest(id: Option[BigInt], formColUpdateRequestList: List[FormColUpdateRequest], formTransferTaskList:  List[FormTransferTaskUpdateRequest]) = {
    FormUpdateRequest(
      id = id,
      name = "hoge2",
      form_index = 2,
      title = "hoge2",
      status = 1,
      cancel_url = "hoge2",
      close_text = "hoge2",
      hashed_id = "hoge2",
      complete_url = "hoge2",
      input_header = "hoge2",
      complete_text = "hoge2",
      confirm_header = "hoge2",
      form_cols = formColUpdateRequestList,
      form_transfer_tasks = formTransferTaskList
      )
  }

  private def createFormTransferTaskConditionUpdateRequest(condition: FormTransferTaskConditionGetReponse) = {
    FormTransferTaskConditionUpdateRequest(
      id = Some(condition.id),
      form_transfer_task_id = Some(condition.form_transfer_task_id),
      form_id = Some(condition.form_id),
      form_col_id = condition.form_col_id,
      operator = "eq",
      cond_value = "x"
    )
  }

  private def createFormTransferTaskMailUpdateRequest(mail: FormTransferTaskMailGetReponse) = {
    FormTransferTaskMailUpdateRequest(
      id = Some(mail.id),
      form_transfer_task_id = Some(mail.form_transfer_task_id),
      from_address_id = 2,
      to_address = Some("hoge2@hoge.com"),
      to_address_id = Some(11L),
      to_address_field = Some("mail2"),
      cc_address = Some("fuga2@hoge.com"),
      cc_address_id = Some(22L),
      cc_address_field = Some("email2"),
      bcc_address_id = Some(2),
      replyto_address_id = Some(2),
      subject = "hogehoge2",
      body = "fugafuga2"
    )
  }

  private def createFormTransferTaskSalesforceFieldUpdateRequest(field:  FormTransferTaskSalesforceFieldGetReponse) = {
    FormTransferTaskSalesforceFieldUpdateRequest(
      id = Some(field.id),
      form_transfer_task_salesforce_id = Some(field.form_transfer_task_salesforce_id),
      form_column_id = "hoge2",
      field_name = "fuga2"
    )
  }

  private def createFormTransferTaskSalesforceUpdateRequest(salesforce: FormTransferTaskSalesforceGetReponse,salesforceFieldUpdateRequestList: List[FormTransferTaskSalesforceFieldUpdateRequest]) = {
    FormTransferTaskSalesforceUpdateRequest(
      id = Some(salesforce.id),
      form_transfer_task_id = Some(salesforce.form_transfer_task_id),
      object_name = "fuga",
      fields = salesforceFieldUpdateRequestList
    )
  }

  private def createFormTransferTaskUpdateRequest(formTransferTask: FormTransferTaskGetResponse, conditionUpdateRequestList: List[FormTransferTaskConditionUpdateRequest], mailUpdateRequest: Option[FormTransferTaskMailUpdateRequest], salesforceUpdateRequest: Option[FormTransferTaskSalesforceUpdateRequest]) = {
    FormTransferTaskUpdateRequest(
      id = Some(formTransferTask.id),
      transfer_config_id = formTransferTask.transfer_config_id,
      form_id = formTransferTask.form_id,
      task_index = 2,
      name = "hoge2",
      form_transfer_task_conditions = conditionUpdateRequestList,
      mail = mailUpdateRequest,
      salesforce = salesforceUpdateRequest
    )
  }

  /*
   Fixture
   */
  override def fixture(implicit session: DBSession): Unit = {

    // TransferConfig
    withSQL {
      val c = TransferConfig.column
      insertInto(TransferConfig).namedValues(
        c.id -> transferConfigId.longValue,
        c.type_code -> "Salesforce",
        c.config_index -> 1,
        c.name -> "hoge",
        c.status -> 1,
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.update().apply()

    // Form
    val formParams: Seq[Seq[Any]] = (formId to formId + 1)
      .map(i => Seq(i,s"hoge$i", i, s"hoge$i", s"hoge$i", 1, "hoge", "hoge", "Header", "Header", "Complete", "Close", "{}", "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))

    withSQL {
      val c = Form.column
      insertInto(Form).namedValues(
        c.id -> sqls.?,
        c.hashed_id -> sqls.?,
        c.form_index -> sqls.?,
        c.name -> sqls.?,
        c.title -> sqls.?,
        c.status -> sqls.?,
        c.cancel_url -> sqls.?,
        c.complete_url -> sqls.?,
        c.input_header -> sqls.?,
        c.confirm_header -> sqls.?,
        c.complete_text -> sqls.?,
        c.close_text -> sqls.?,
        c.form_data -> sqls.?,
        c.user_group -> sqls.?,
        c.created_user -> sqls.?,
        c.modified_user -> sqls.?,
        c.created -> sqls.?,
        c.modified -> sqls.?
      )
    }.batch(formParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])

    // FormCol
    val formColParams: Seq[Seq[Any]] = (1 to 5)
      .map(i => Seq(BigInt(i + 1000), formId, s"foo$i", s"foo$i", i, 1, "x", "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))

    withSQL {
      val c = FormCol.column
      insertInto(FormCol).namedValues(
        c.id -> sqls.?,
        c.form_id -> sqls.?,
        c.name -> sqls.?,
        c.col_id -> sqls.?,
        c.col_index -> sqls.?,
        c.col_type -> sqls.?,
        c.default_value -> sqls.?,
        c.user_group -> sqls.?,
        c.created_user -> sqls.?,
        c.modified_user -> sqls.?,
        c.created -> sqls.?,
        c.modified -> sqls.?
      )
    }.batch(formColParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])

    val formColId = BigInt(1001)

    // FormColSelect
    val formColSelectParams: Seq[Seq[Any]] = (1 to 5)
      .map(i => Seq(BigInt(i + 2000), formColId, formId, 1, "foo", "foo", true, "", "", "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))

    withSQL {
      val c = FormColSelect.column
      insertInto(FormColSelect).namedValues(
        c.id -> sqls.?,
        c.form_col_id -> sqls.?,
        c.form_id -> sqls.?,
        c.select_index -> sqls.?,
        c.select_name -> sqls.?,
        c.select_value -> sqls.?,
        c.is_default -> sqls.?,
        c.edit_style -> sqls.?,
        c.view_style -> sqls.?,
        c.user_group -> sqls.?,
        c.created_user -> sqls.?,
        c.modified_user -> sqls.?,
        c.created -> sqls.?,
        c.modified -> sqls.?
      )
    }.batch(formColSelectParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])

    // FormColValidation
    val formColValidationParams: Seq[Seq[Any]] = (1 to 5)
      .map(i => Seq(BigInt(i + 1000), BigInt(i + 1000), formId, 10, 1, 100, 1, 1, false, "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))

    withSQL {
      val c = FormColValidation.column
      insertInto(FormColValidation).namedValues(
        c.id -> sqls.?,
        c.form_col_id -> sqls.?,
        c.form_id -> sqls.?,
        c.max_value -> sqls.?,
        c.min_value -> sqls.?,
        c.max_length -> sqls.?,
        c.min_length -> sqls.?,
        c.input_type -> sqls.?,
        c.required -> sqls.?,
        c.user_group -> sqls.?,
        c.created_user -> sqls.?,
        c.modified_user -> sqls.?,
        c.created -> sqls.?,
        c.modified -> sqls.?
      )
    }.batch(formColValidationParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])

    // FormTransferTask
    val formTransferTaskParams: Seq[Seq[Any]] = (1 to 5)
      .map(i => Seq(BigInt(i + 1000), transferConfigId, formId, 1, "task", "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))
    val formTransferTaskId = 1001L
    withSQL {
      val c = FormTransferTask.column
      insertInto(FormTransferTask)
        .namedValues(
          c.id -> sqls.?,
          c.transfer_config_id -> sqls.?,
          c.form_id -> sqls.?,
          c.task_index -> sqls.?,
          c.name -> sqls.?,
          c.user_group -> sqls.?,
          c.created_user -> sqls.?,
          c.modified_user -> sqls.?,
          c.created -> sqls.?,
          c.modified -> sqls.?
        )
    }.batch(formTransferTaskParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])

    // FormTransferTaskCondition
    withSQL {
      val c = FormTransferTaskCondition.column
      insertInto(FormTransferTaskCondition)
        .namedValues(
          c.form_transfer_task_id -> formTransferTaskId,
          c.form_id -> formId,
          c.form_col_id -> formColId,
          c.operator -> "eq",
          c.cond_value -> "x",
          c.user_group -> "hoge",
          c.created_user -> userId.toString,
          c.modified_user -> userId.toString,
          c.created -> ZonedDateTime.now(),
          c.modified -> ZonedDateTime.now()
        )
    }.updateAndReturnGeneratedKey().apply()

    // FormTransferTaskMail
    withSQL {
      val c = FormTransferTaskMail.column
      insertInto(FormTransferTaskMail).namedValues(
        c.form_transfer_task_id -> formTransferTaskId,
        c.from_address_id -> 1,
        c.to_address -> "hoge@hoge.com",
        c.to_address_id -> 1,
        c.to_address_field -> "mail",
        c.cc_address -> "fuga@hoge.com",
        c.cc_address_id -> 2,
        c.cc_address_field -> "email",
        c.bcc_address_id -> 1,
        c.replyto_address_id -> 1,
        c.subject -> "Subject",
        c.body -> "Body",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()

    // FormTransferTaskSalesforce
    val formTransferTaskSalesforceId = withSQL {
      val c = FormTransferTaskSalesforce.column
      insertInto(FormTransferTaskSalesforce).namedValues(
        c.form_transfer_task_id -> formTransferTaskId,
        c.object_name -> "Lead",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()

    // FormTransferTaskSalesforceField
    val formTransferTaskSalesforceFieldParams: Seq[Seq[Any]] = (1 to 5)
      .map(i => Seq(BigInt(i + 1000), formTransferTaskSalesforceId, "foo", "hoge", "hoge", userId.toString, userId.toString, ZonedDateTime.now(), ZonedDateTime.now()))
    withSQL {
      val c = FormTransferTaskSalesforceField.column
      insertInto(FormTransferTaskSalesforceField).namedValues(
        c.id -> sqls.?,
        c.form_transfer_task_salesforce_id -> sqls.?,
        c.form_column_id -> sqls.?,
        c.field_name -> sqls.?,
        c.user_group -> sqls.?,
        c.created_user -> sqls.?,
        c.modified_user -> sqls.?,
        c.created -> sqls.?,
        c.modified -> sqls.?
      )
    }.batch(formTransferTaskSalesforceFieldParams: _*).apply()(session, implicitly[Factory[Int, Seq[Int]]])
  }
}
