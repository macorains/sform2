package net.macolabo.sform2.domain.services.TransferConfig

import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.{TransferConfigSesMail}
import net.macolabo.sform2.domain.services.TransferConfig.save.{SesMailTransferConfigMailAddressSaveRequest, SesMailTransferConfigSaveRequest, SalesforceTransferConfigObjectFieldSaveRequest, SalesforceTransferConfigObjectSaveRequest, SalesforceTransferConfigSaveRequest, TransferConfigSaveRequest}

import scala.concurrent.ExecutionContext

trait TransferConfigService {

  // Insert or Update
  def saveTransferConfig(request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig, sessionInfo: SessionInfo): BigInt
  def saveSesMailTransferConfig(request: SesMailTransferConfigSaveRequest, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveSesMailTransferConfigMailAddress(request: SesMailTransferConfigMailAddressSaveRequest, transferConfigSesMailId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveSalesforceTransferConfig(request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveSalesforceTransferConfigObject(request: SalesforceTransferConfigObjectSaveRequest, transferConfigSalesforceId: BigInt, sessionInfo: SessionInfo): BigInt
  def insertSalesforceTransferConfigObjectField(request: SalesforceTransferConfigObjectFieldSaveRequest, transferConfigSalesforceObjectId: BigInt, sessionInfo: SessionInfo): BigInt

  // Delete
  def deleteTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSesMailTransferConfig(config: TransferConfigSesMail, transferConfigId: BigInt, sessionInfo: SessionInfo): Int
  def deleteSesMailTransferConfigMailAddress(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfigObject(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfigObjectField(id: BigInt, sessionInfo: SessionInfo): Int

}
