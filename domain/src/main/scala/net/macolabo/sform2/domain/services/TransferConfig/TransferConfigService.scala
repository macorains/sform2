package net.macolabo.sform2.domain.services.TransferConfig

import com.google.inject.Inject
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
  def saveTransferConfig(userId: String, userGroup: String, request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig): BigInt
  def saveMailTransferConfig(userId: String, userGroup: String, request: MailTransferConfigSaveRequest, transferConfigId: BigInt): BigInt
  def saveMailTransferConfigMailAddress(userId: String, userGroup: String, request: MailTransferConfigMailAddressSaveRequest, transferConfigMailId: BigInt): BigInt
  def saveSalesforceTransferConfig(userId: String, userGroup: String, request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig, transferConfigId: BigInt): BigInt
  def saveSalesforceTransferConfigObject(userId: String, userGroup: String, request: SalesforceTransferConfigObjectSaveRequest, transferConfigSalesforceId: BigInt): BigInt
  def insertSalesforceTransferConfigObjectField(userId: String, userGroup: String, request: SalesforceTransferConfigObjectFieldSaveRequest, transferConfigSalesforceObjectId: BigInt): BigInt

  // Delete
  def deleteTransferConfig(userId: String, userGroup: String, id: BigInt): Any
  def deleteMailTransferConfig(userGroup: String, config: TransferConfigMail): Any
  def deleteMailTransferConfigMailAddress(userGroup: String, id: BigInt): Any
  def deleteSalesforceTransferConfig(userGroup: String, id: BigInt): Any
  def deleteSalesforceTransferConfigObject(userGroup: String, id: BigInt): Any
  def deleteSalesforceTransferConfigObjectField(userGroup: String, id: BigInt): Any

}
