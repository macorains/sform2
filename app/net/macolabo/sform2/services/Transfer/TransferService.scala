package net.macolabo.sform2.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce}

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
        f.use_cc,
        f.use_bcc,
        f.use_replyto,
        getTransferConfigMailAddress(userGroup, f.id)
      )
    })
  }
  
  private def getTransferConfigMailAddress(userGroup: String, transferConfigMailId: Int): List[TransferGetTransferResponseMailTransferConfigMailAddress] = {
    TransferConfigMailAddress.getList(userGroup, transferConfigMailId).map(f => {
      TransferGetTransferResponseMailTransferConfigMailAddress(
        f.id,
        f.transfer_config_mail_id,
        f.address_index,
        f.name,
        f.address
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
