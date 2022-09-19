package net.macolabo.sform2.domain.services.Transfer

import akka.actor.Actor
import net.macolabo.sform2.domain.services.Transfer.TransferReceiver.ConsumeTaskRequest
import play.api.libs.json.JsValue

trait BaseTransfer extends Actor {
  def endTask(taskList: List[TransferTaskBean], postdata: JsValue, logText: String) = {
    // TODO エラーになるので一旦コメントアウト
    // sender() ! ConsumeTaskRequest(taskList.tail, postdata)
  }
}
