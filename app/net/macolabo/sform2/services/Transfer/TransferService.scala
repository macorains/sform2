package net.macolabo.sform2.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.transfer.{TransferConfig, TransferConfigMail, TransferConfigSalesforce}

import scala.concurrent.ExecutionContext

class TransferService @Inject() (implicit ex: ExecutionContext) extends TransferGetTransferConfigSelectListJson with TransferGetTransferConfigListJson with TransferGetTransferConfigResponseJson {

  def getTransferConfigSelectList(identity: User): List[TransferGetTransferConfigSelectList] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigSelectList(
        f.id,
        f.name
      )
    })
  }

  def getTransferConfigList(identity: User): List[TransferGetTransferConfigListResponse] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigListResponse(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status
      )
    })
  }

  def getTransferConfig(identity:User, transferConfigId: Int): Option[TransferGetTransferConfigResponse] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferConfigResponse(
        f.id,
        f.type_code,
        f.config_index,
        f.name,
        f.status,
        TransferGetTransferResponseConfigDetail(
          getTransferConfigMail(userGroup, transferConfigId),
          getTransferConfigSalesforce(userGroup, transferConfigId)
        )
      )
    })
  }

  private def getTransferConfigMail(userGroup: String, transferConfigId: Int): Option[TransferGetTransferResponseMailTransferConfig] = {
    TransferConfigMail.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferResponseMailTransferConfig(
        f.id,
        f.transfer_config_id,
        f.subject,
        f.reply_to,
        f.from_address,
        f.to_address,
        f.cc_address,
        f.bcc_address,
        f.body
      )
    })
  }

  private def getTransferConfigSalesforce(userGroup: String, transferConfigId: Int): Option[TransferGetTransferResponseSalesforceTransferConfig] = {
    TransferConfigSalesforce.get(userGroup, transferConfigId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfig(
        f.id,
        f.transfer_config_id,
        f.sf_user_name,
        f.sf_password,
        f.sf_security_token
      )
    })
  }

}
