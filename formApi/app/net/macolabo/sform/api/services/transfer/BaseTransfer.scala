package net.macolabo.sform.api.services.transfer

import akka.actor.{Actor, ActorRef}
import net.macolabo.sform.api.services.transfer.TransferReceiver.ConsumeTaskRequest
import play.api.libs.json.JsValue

trait BaseTransfer extends Actor {
  def endTask(taskList: List[TransferTaskBean], postdata: JsValue, logText: String) = {
    sender() ! ConsumeTaskRequest(taskList.tail, postdata)
  }
}
