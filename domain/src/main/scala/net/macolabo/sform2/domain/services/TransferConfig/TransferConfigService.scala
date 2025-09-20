package net.macolabo.sform2.domain.services.TransferConfig

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.daos.{TransferConfigDAO, TransferConfigMailAddressDAO, TransferConfigMailDAO, TransferConfigSalesforceDAO, TransferConfigSalesforceObjectDAO, TransferConfigSalesforceObjectFieldDAO}
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce, TransferConfigSalesforceObject, TransferConfigSalesforceObjectField}
import net.macolabo.sform2.domain.services.TransferConfig.save.{MailTransferConfigMailAddressSaveRequest, MailTransferConfigSaveRequest, SalesforceTransferConfigObjectFieldSaveRequest, SalesforceTransferConfigObjectSaveRequest, SalesforceTransferConfigSaveRequest, TransferConfigDetailSaveRequest, TransferConfigSaveRequest}
import net.macolabo.sform2.domain.utils.Crypto
import scalikejdbc.{DB, deleteFrom}

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

trait TransferConfigService {

  // Insert or Update
  def saveTransferConfig(request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig, sessionInfo: SessionInfo): BigInt
  def saveMailTransferConfig(request: MailTransferConfigSaveRequest, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveMailTransferConfigMailAddress(request: MailTransferConfigMailAddressSaveRequest, transferConfigMailId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveSalesforceTransferConfig(request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt
  def saveSalesforceTransferConfigObject(request: SalesforceTransferConfigObjectSaveRequest, transferConfigSalesforceId: BigInt, sessionInfo: SessionInfo): BigInt
  def insertSalesforceTransferConfigObjectField(request: SalesforceTransferConfigObjectFieldSaveRequest, transferConfigSalesforceObjectId: BigInt, sessionInfo: SessionInfo): BigInt

  // Delete
  def deleteTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteMailTransferConfig(config: TransferConfigMail, sessionInfo: SessionInfo): Int
  def deleteMailTransferConfigMailAddress(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfigObject(id: BigInt, sessionInfo: SessionInfo): Int
  def deleteSalesforceTransferConfigObjectField(id: BigInt, sessionInfo: SessionInfo): Int

}
