package net.macolabo.sform2.domain.services.Transfer

import net.macolabo.sform2.domain.services.Transfer.SalesforceTransfer.TransferTaskRequest
import play.api.libs.json.JsValue

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

