package net.macolabo.sform2.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.transfer.TransferConfig

import scala.concurrent.ExecutionContext

class TransferService @Inject() (implicit ex: ExecutionContext) extends TransferGetTransferConfigSelectListJson {

  def getTransferConfigSelectList(identity: User): List[TransferGetTransferConfigSelectList] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigSelectList(
        f.id,
        f.name
      )
    })
  }

}
