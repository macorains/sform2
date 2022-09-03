package net.macolabo.sform2.domain.services.Transfer

import akka.actor.{Actor, ActorRef}
import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{FormDAO, FormTransferTaskConditionDAO, FormTransferTaskDAO, FormTransferTaskMailDAO, FormTransferTaskSalesforceDAO, FormTransferTaskSalesforceFieldDAO}
import net.macolabo.sform2.domain.models.entity.formtransfertask.{FormTransferTask, FormTransferTaskCondition, FormTransferTaskMail}
import net.macolabo.sform2.domain.services.Transfer.MailTransfer.TransferTaskRequest
import net.macolabo.sform2.domain.services.Transfer.TransferReceiver.{ConsumeTaskRequest, NewTaskRequest}
import play.api.Logging
import play.api.libs.json.JsValue
import scalikejdbc.{DB, DBSession}

import javax.inject.Named

class TransferReceiver @Inject()(
  formDAO: FormDAO,
  formTransferTaskDAO: FormTransferTaskDAO,
  formTransferTaskConditionDAO: FormTransferTaskConditionDAO,
  formTransferTaskMailDAO: FormTransferTaskMailDAO,
  formTransferTaskSalesforceDAO: FormTransferTaskSalesforceDAO,
  formTransferTaskSalesforceFieldDAO: FormTransferTaskSalesforceFieldDAO,
  @Named("actor_mail_transfer") mailTransfer: ActorRef,
  @Named("actor_salesforce_transfer") salesforceTransfer: ActorRef
) extends Actor with Logging {

  override def receive: Receive = {

    case NewTaskRequest(postdata, postdataId, hashedFormId) =>
      DB.localTx(implicit session => {

        // フォームIDからTransferTaskを検索
        val transferTaskList: List[FormTransferTask] = getFormtransferTaskList(hashedFormId)
        // タスク処理用オブジェクトのリストを作る
        val taskList: List[TransferTaskBean] = createTransferTaskBeanList(transferTaskList)

        if(taskList.nonEmpty) self ! ConsumeTaskRequest(taskList, postdata)
        else logger.error(s"This form does not have any TransferTasks. (hashedFormID:$hashedFormId)")
      })

    case ConsumeTaskRequest(taskList, postdata) =>
      // リストの先頭を取り出して、その内容で各Transferに振り分ける
      taskList.headOption.map(tl => {
        tl.t_mail.foreach(_ => mailTransfer ! TransferTaskRequest(taskList, postdata))
        // SalesforceTransfer実装したらコメント外す
        // tl.t_salesforce.foreach(_ => SalesforceTransfer ! TransferTaskRequest(taskList, postdata))
      }).getOrElse(println("ほげー－－－－")) // TODO taskListが空 = 処理完了なので、何かしら完了処理を実装する
  }

  private def getFormtransferTaskList(hashedFormId: String)(implicit session:DBSession): List[FormTransferTask] = {
    formDAO.get(hashedFormId).map(form => {
      formTransferTaskDAO.getList(form.id)
    }).getOrElse(List.empty)
  }

  private def createTransferTaskBeanList(transferTaskList: List[FormTransferTask])(implicit session:DBSession): List[TransferTaskBean] = {
    transferTaskList.map(tt => {
      TransferTaskBean(
        tt.id,
        tt.transfer_config_id,
        tt.form_id,
        tt.task_index,
        tt.name,
        tt.user_group,
        getFormTransferTaskCondition(tt.id),
        getFormTransferTaskMail(tt.id),
        getFormTransferTaskSalesforce(tt.id)
      )
    })
  }

  private def getFormTransferTaskCondition(formTransferTaskId: BigInt)(implicit session:DBSession): List[TransferTaskBeanCondition] = {
    formTransferTaskConditionDAO.getList(formTransferTaskId).map(tc => {
      TransferTaskBeanCondition(
        tc.id,
        tc.form_transfer_task_id,
        tc.form_id,
        tc.form_col_id,
        tc.operator,
        tc.cond_value,
        tc.user_group
      )
    })
  }

  private def getFormTransferTaskMail(formTransferTaskId: BigInt)(implicit session:DBSession): Option[TransferTaskBeanMail] = {
    formTransferTaskMailDAO.get(formTransferTaskId).map(tm => {
      TransferTaskBeanMail(
        tm.id,
        tm.form_transfer_task_id,
        tm.from_address_id,
        tm.to_address,
        tm.to_address_id,
        tm.to_address_field,
        tm.cc_address,
        tm.cc_address_id,
        tm.cc_address_field,
        tm.bcc_address_id,
        tm.replyto_address_id,
        tm.subject,
        tm.body,
        tm.user_group
      )
    })
  }

  private def getFormTransferTaskSalesforce(formTransferTaskId: BigInt)(implicit session:DBSession): Option[TransferTaskBeanSalesforce] = {
    formTransferTaskSalesforceDAO.get(formTransferTaskId).map(ts => {
      TransferTaskBeanSalesforce(
        ts.id,
        ts.form_transfer_task_id,
        ts.object_name,
        ts.user_group,
        getFormTransferTaskSalesforceField(ts.id)
      )
    })
  }

  private def getFormTransferTaskSalesforceField(formTransferTaskSalesforceId: BigInt)(implicit session:DBSession): List[TransferTaskBeanSalesforceField] = {
    formTransferTaskSalesforceFieldDAO.getList(formTransferTaskSalesforceId).map(sf => {
      TransferTaskBeanSalesforceField(
        sf.id,
        sf.form_transfer_task_salesforce_id,
        sf.form_column_id,
        sf.field_name,
        sf.user_group
      )
    })
  }
}

object TransferReceiver {
  case class NewTaskRequest(postdata: JsValue, postdataId: Long, hashedFormId: String)
  case class ConsumeTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue)
}
