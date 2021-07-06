package net.macolabo.sform2.model.daos

import com.mohiva.play.silhouette.api.LoginInfo
import net.macolabo.sform2.helper.SformTestHelper
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.daos.FormDAO
import net.macolabo.sform2.models.entity.Transfer.TransferConfig
import net.macolabo.sform2.models.entity.form.{Form, FormCol, FormColSelect, FormColValidation, FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail, FormTransferTaskSalesforce, FormTransferTaskSalesforceField}
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.ZonedDateTime
import java.util.UUID

class FormDAOImplSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {

  val loginInfo = LoginInfo("", "")
  val userId = UUID.randomUUID()
  val user = User(userId, loginInfo, Some("hoge"), Some("hoge"), Some("foo"), Some("var"), Some("foovar"), Some("hoge@hoge.com"), None, activated = true, deletable = false)
  var formId = BigInt(1)
  var transferConfigId = BigInt(1)

  override def fixture(implicit session: DBSession): Unit = {
    insertTransferConfig()
    insertForm()
    val formColId = insertFormCol()
    insertFormColSelect(formColId)
    insertFormColValidation(formColId)
    val formTransferTaskId = insertFormTransferTask()
    insertFormTransferTaskCondition(formColId, formTransferTaskId)
    insertFormTransferTaskMail(formTransferTaskId)
    val formTransferTaskSalesforceId = insertFormTransferTaskSalesforce(formTransferTaskId)
    insertFormTransferTaskSalesforceField(formTransferTaskSalesforceId)
  }

  it should "select form detail" in { implicit session =>
    val mockFormDAO = mock[FormDAO]

    val form = mockFormDAO.get(user, formId)

    assert(form.nonEmpty)
    assert(form.get.id.isValidLong)
  }

  // TestData
  private def insertTransferConfig() = {
    withSQL {
      val c = TransferConfig.column
      insertInto(TransferConfig).namedValues(
        c.id -> transferConfigId,
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
    }.updateAndReturnGeneratedKey().apply()
  }

  private def insertForm() = {
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
    }.updateAndReturnGeneratedKey().apply()
  }

  private def insertFormCol() = {
    withSQL {
      val c = FormCol.column
      insertInto(FormCol).namedValues(
        c.form_id -> formId,
        c.name -> "foo",
        c.col_id -> "foo",
        c.col_index -> 1,
        c.col_type -> 1,
        c.default_value -> "x",
        c.user_group -> "hoge",
        c.created_user -> userId.toString,
        c.modified_user -> userId.toString,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  private def insertFormColSelect(formColId: Long) = {
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

  }

  private def insertFormColValidation(formColId: Long) = {
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
  }

  private def insertFormTransferTask() = {
    withSQL {
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
  }

  private def insertFormTransferTaskCondition(formColId: Long, formTransferTaskId: Long) = {
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

  }

  private def insertFormTransferTaskMail(formTransferTaskId: Long) = {
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
  }

  private def insertFormTransferTaskSalesforce(formTransferTaskId: Long) = {
    withSQL {
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
  }

  private def insertFormTransferTaskSalesforceField(formTransferTaskSalesforceId: Long) = {
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