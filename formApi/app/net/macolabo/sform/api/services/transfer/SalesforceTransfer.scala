package net.macolabo.sform.api.services.transfer

import akka.actor.ActorRef
import com.google.inject.Inject
import net.macolabo.sform.api.services.transfer.SalesforceTransfer.TransferTaskRequest
import net.macolabo.sform.api.services.transfer.TransferReceiver.ConsumeTaskRequest
import play.api.libs.json.JsValue

import javax.inject.Named

class SalesforceTransfer extends BaseTransfer {
  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata) =>
      // Salesforce登録処理

      endTask(taskList, postdata, "")

  }
}

object SalesforceTransfer {
  case class TransferTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue)
}

