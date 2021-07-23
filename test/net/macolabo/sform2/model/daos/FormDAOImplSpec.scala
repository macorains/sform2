package net.macolabo.sform2.model.daos

import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.test.FakeEnvironment
import net.macolabo.sform2.helper.SformTestHelper
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.FormDAOImpl
import net.macolabo.sform2.models.entity.Transfer.TransferConfig
import net.macolabo.sform2.models.entity.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}
import net.macolabo.sform2.services.Form.get.{FormColGetReponse, FormColSelectGetReponse, FormColValidationGetReponse, FormGetResponse}
import net.macolabo.sform2.services.Form.update.{FormColSelectUpdateRequest, FormColUpdateRequest, FormColValidationUpdateRequest, FormTransferTaskConditionUpdateRequest, FormTransferTaskMailUpdateRequest, FormTransferTaskSalesforceFieldUpdateRequest, FormTransferTaskSalesforceUpdateRequest, FormTransferTaskUpdateRequest, FormUpdateRequest}
import net.macolabo.sform2.utils.auth.DefaultEnv
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc.interpolation.SQLSyntax.count

import java.time.ZonedDateTime
import java.util.UUID
import scala.collection.compat.Factory

class FormDAOImplSpec extends FixtureAnyFlatSpec with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {

  val loginInfo = LoginInfo("hoge", "hoge")
  val userId = UUID.randomUUID()
  val user = User(userId, loginInfo, Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge"), Some("hoge@hoge.com"), None, activated = true, deletable = false)
  var formId = BigInt(100)
  var transferConfigId = BigInt(100)



  behavior of "Form"

  it should "select form detail" in { implicit session =>

    val formDAO = new FormDAOImpl()
    val form = formDAO.get(user, formId)

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
      assert(formTransferTaskMail.cc_address.nonEmpty)
      assert(formTransferTaskMail.bcc_address_id.isValidLong)
      assert(formTransferTaskMail.replyto_address_id.isValidLong)
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
    val formList = formDAO.getList(user)
    assert(formList.forms.nonEmpty)
    val form = formList.forms.head
    assert(form.id.isValidLong)
    assert(form.hashed_id.nonEmpty)
    assert(form.form_index.isValidInt)
    assert(form.name.nonEmpty)
    assert(form.status.isValidInt)
    assert(form.title.nonEmpty)
  }

  it should "create form & update form" in { implicit session =>

    val formColSelectUpdateRequest = createFormColSelectUpdateRequest()
    val formColValidationUpdateRequest = createFormColValidationUpdateRequest()
    val formColUpdateRequest = createFormColUpdateRequest(List(formColSelectUpdateRequest), formColValidationUpdateRequest)
    val formUpdateRequest = createFormUpdateRequest(List(formColUpdateRequest), List.empty)

    val formDAO = new FormDAOImpl()

    // フォームを作成する（作成時にはFormTransferTaskは存在しない）
    val response = formDAO.update(user, formUpdateRequest)
    val newFormId = response.id
    assert(newFormId.isValidLong)

    // 作成したフォームを取得
    val newForm = formDAO.get(user, newFormId).get


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
    }.map(_.long(1)).single().apply().get

    val transferTask = FormTransferTask.syntax("transferTask")
    val formTransferTaskId = withSQL {
      select(
        transferTask.id
      )
        .from(FormTransferTask as transferTask)
        .where
        .eq(transferTask.form_id, formId)
    }.map(_.long(1)).single().apply().get

    val transferTaskCondition = FormTransferTaskCondition.syntax("transferTaskCondition")
    val formTransferTaskConditionId = withSQL {
      select(
        transferTaskCondition.id
      )
        .from(FormTransferTaskCondition as transferTaskCondition)
        .where
        .eq(transferTaskCondition.form_transfer_task_id, formTransferTaskId)
    }.map(_.long(1)).single().apply().get

    val transferTaskMail = FormTransferTaskMail.syntax("transferTaskMail")
    val formTransferTaskMailId = withSQL {
      select(
        transferTaskMail.id
      )
        .from(FormTransferTaskMail as transferTaskMail)
        .where
        .eq(transferTaskMail.form_transfer_task_id, formTransferTaskId)
    }.map(_.long(1)).single().apply().get

    val transferTaskSalesforce = FormTransferTaskSalesforce.syntax("transferTaskSalesforce")
    val formTransferTaskSalesforceId = withSQL {
      select(
        transferTaskSalesforce.id
      )
        .from(FormTransferTaskSalesforce as transferTaskSalesforce)
        .where
        .eq(transferTaskSalesforce.form_transfer_task_id, formTransferTaskId)
    }.map(_.long(1)).single().apply().get

    val transferTaskSalesforceField = FormTransferTaskSalesforceField.syntax("transferTaskSalesforceField")
    val formTransferTaskSalesforceFieldId = withSQL {
      select(
        transferTaskSalesforceField.id
      )
        .from(FormTransferTaskSalesforceField as transferTaskSalesforceField)
        .where
        .eq(transferTaskSalesforceField.form_transfer_task_salesforce_id, formTransferTaskSalesforceId)
    }.map(_.long(1)).single().apply().get

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
  def createFormColSelectUpdateRequest() = {
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

  def createFormColSelectUpdateRequest(formColSelectGetResponse: FormColSelectGetReponse) = {
    FormColSelectUpdateRequest(
      id = Some(formColSelectGetResponse.id),
      form_col_id = Some(formColSelectGetResponse.form_col_id),
      form_id = Some(formColSelectGetResponse.form_id),
      select_index = formColSelectGetResponse.select_index,
      select_name = formColSelectGetResponse.select_name,
      select_value = formColSelectGetResponse.select_value,
      is_default = formColSelectGetResponse.is_default,
      edit_style = formColSelectGetResponse.edit_style,
      view_style = formColSelectGetResponse.view_style
    )
  }

  def createFormColValidationUpdateRequest() = {
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

  def createFormColValidationUpdateRequest(formColValidationGetResponse: FormColValidationGetReponse) = {
    FormColValidationUpdateRequest(
      id = Some(formColValidationGetResponse.id),
      form_col_id = Some(formColValidationGetResponse.form_col_id),
      form_id = Some(formColValidationGetResponse.form_id),
      max_value = formColValidationGetResponse.max_value,
      min_value = formColValidationGetResponse.min_value,
      max_length = formColValidationGetResponse.max_length,
      min_length = formColValidationGetResponse.min_length,
      input_type = formColValidationGetResponse.input_type,
      required = formColValidationGetResponse.required
    )
  }

  def createFormColUpdateRequest(formColSelectUpdateRequestList: List[FormColSelectUpdateRequest], formColValidationUpdateRequest: FormColValidationUpdateRequest) = {
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

  def createFormColUpdateRequest(formColGetResponse: FormColGetReponse, formColSelectUpdateRequestList: List[FormColSelectUpdateRequest], formColValidationUpdateRequest: FormColValidationUpdateRequest) = {
    FormColUpdateRequest(
      id = Some(formColGetResponse.id),
      form_id = Some(formColGetResponse.form_id),
      name = formColGetResponse.name,
      col_id = formColGetResponse.col_id,
      col_index = formColGetResponse.col_index,
      col_type = formColGetResponse.col_type,
      default_value = formColGetResponse.default_value,
      select_list = formColSelectUpdateRequestList,
      validations = formColValidationUpdateRequest
    )
  }

  def createFormUpdateRequest(formColUpdateRequestList: List[FormColUpdateRequest], formTransferTaskList:  List[FormTransferTaskUpdateRequest]) = {
    FormUpdateRequest(
      id = None,
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

  def createFormUpdateRequest(formGetResponse: FormGetResponse, formColUpdateRequestList: List[FormColUpdateRequest], formTransferTaskList:  List[FormTransferTaskUpdateRequest]) = {
    FormUpdateRequest(
      id = Some(formGetResponse.id),
      name = formGetResponse.name,
      form_index = formGetResponse.form_index,
      title = formGetResponse.title,
      status = formGetResponse.status,
      cancel_url = formGetResponse.cancel_url,
      close_text = formGetResponse.close_text,
      hashed_id = formGetResponse.hashed_id,
      complete_url = formGetResponse.complete_url,
      input_header = formGetResponse.input_header,
      complete_text = formGetResponse.complete_text,
      confirm_header = formGetResponse.confirm_header,
      form_cols = formColUpdateRequestList,
      form_transfer_tasks = formTransferTaskList
    )
  }

  def createFormTransferTaskConditionUpdateRequest(formId: BigInt) = {
    FormTransferTaskConditionUpdateRequest(
      id = None,
      form_transfer_task_id = Some(0),
      form_id = Some(formId),
      form_col_id = 0,
      operator = "eq",
      cond_value = "x"
    )
  }

  def createFormTransferTaskMailUpdateRequest() = {
    FormTransferTaskMailUpdateRequest(
      id = None,
      form_transfer_task_id = None,
      from_address_id = 1,
      to_address = "hoge@hoge.com",
      cc_address = Some("hoge@hoge.com"),
      bcc_address_id = Some(1),
      replyto_address_id = Some(1),
      subject = "hogehoge",
      body = "fugafuga"
    )
  }

  def createFormTransferTaskSalesforceFieldUpdateRequest() = {
    FormTransferTaskSalesforceFieldUpdateRequest(
      id = None,
      form_transfer_task_salesforce_id = Some(0),
      form_column_id = "hoge",
      field_name = "fuga"
    )
  }

  def createFormTransferTaskSalesforceUpdateRequest(salesforceFieldUpdateRequestList: List[FormTransferTaskSalesforceFieldUpdateRequest]) = {
    FormTransferTaskSalesforceUpdateRequest(
      id = None,
      form_transfer_task_id = None,
      object_name = "fuga",
      fields = salesforceFieldUpdateRequestList
    )
  }

  def createFormTransferTaskUpdateRequest(formId: BigInt, conditionUpdateRequestList: List[FormTransferTaskConditionUpdateRequest], mailUpdateRequest: Option[FormTransferTaskMailUpdateRequest], salesforceUpdateRequest: Option[FormTransferTaskSalesforceUpdateRequest]) = {
    FormTransferTaskUpdateRequest(
      id = None,
      transfer_config_id = 1,
      form_id = formId,
      task_index = 1,
      name = "hoge",
      form_transfer_task_conditions = conditionUpdateRequestList,
      mail = mailUpdateRequest,
      salesforce = salesforceUpdateRequest
    )
  }

  /*
   Fixture
   - フォーム1 select、フォーム削除検証用
   - フォーム2 update検証用
     - formTransferTaskなしのフォームを更新してformTransferTaskを追加
     - 複数のformCol, formColSelect, formTransferTask, formTransferTaskSalesforceFieldから一部だけ削除更新
   */
  def fixTransferConfig(implicit session: DBSession) = {
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
  }
  override def fixture(implicit session: DBSession) = {

      // TransferConfig
    // fixTransferConfig(session)
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


    // フォーム1
    withSQL {
      val c = Form.column
      insertInto(Form).namedValues(
        c.id -> formId,
        c.hashed_id -> "hoge",
        c.form_index -> 1,
        c.name -> "hoge",
        c.title -> "hoge",
        c.status -> 1,
        c.cancel_url -> "hoge",
        c.complete_url -> "hoge",
        c.input_header -> "Header",
        c.confirm_header -> "Header",
        c.complete_text -> "Complete",
        c.close_text -> "Close",
        c.form_data -> "{}",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.update().apply()

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

    withSQL {
      val c = FormColSelect.column
      insertInto(FormColSelect).namedValues(
        c.form_col_id -> formColId,
        c.form_id -> formId,
        c.select_index -> 1,
        c.select_name -> "foo",
        c.select_value -> "foo",
        c.is_default -> true,
        c.edit_style -> "",
        c.view_style -> "",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()

    withSQL {
      val c = FormColValidation.column
      insertInto(FormColValidation).namedValues(
        c.form_col_id -> formColId,
        c.form_id -> formId,
        c.max_value -> 10,
        c.min_value -> 1,
        c.max_length -> 100,
        c.min_length -> 1,
        c.input_type -> 1,
        c.required -> false,
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()

    val formTransferTaskId = withSQL {
      val c = FormTransferTask.column
      insertInto(FormTransferTask)
        .namedValues(
          c.transfer_config_id -> transferConfigId,
          c.form_id -> formId,
          c.task_index -> 1,
          c.name -> "Task",
          c.user_group -> "hoge",
          c.created_user -> userId.toString,
          c.modified_user -> userId.toString,
          c.created -> ZonedDateTime.now(),
          c.modified -> ZonedDateTime.now()
        )
    }.updateAndReturnGeneratedKey().apply()

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

    withSQL {
      val c = FormTransferTaskMail.column
      insertInto(FormTransferTaskMail).namedValues(
        c.form_transfer_task_id -> formTransferTaskId,
        c.from_address_id -> 1,
        c.to_address -> "hoge@hoge.com",
        c.cc_address -> "hoge@hoge.com",
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

    withSQL {
      val c = FormTransferTaskSalesforceField.column
      insertInto(FormTransferTaskSalesforceField).namedValues(
        c.form_transfer_task_salesforce_id -> formTransferTaskSalesforceId,
        c.form_column_id -> "foo",
        c.field_name -> "foo",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }
}