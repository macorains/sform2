package net.macolabo.sform2.domain.services.TransferConfig

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{TransferConfigDAO, TransferConfigMailAddressDAO, TransferConfigMailDAO, TransferConfigSalesforceDAO, TransferConfigSalesforceObjectDAO, TransferConfigSalesforceObjectFieldDAO}
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce}
import net.macolabo.sform2.domain.services.TransferConfig.save.{MailTransferConfigMailAddressSaveRequest, MailTransferConfigSaveRequest, SalesforceTransferConfigObjectFieldSaveRequest, SalesforceTransferConfigObjectSaveRequest, SalesforceTransferConfigSaveRequest, TransferConfigDetailSaveRequest, TransferConfigSaveRequest}
import net.macolabo.sform2.domain.utils.Crypto
import scalikejdbc.DB

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

class TransferConfigService @Inject()(
  transferConfigMailDAO: TransferConfigMailDAO,
  transferConfigMailAddressDAO: TransferConfigMailAddressDAO,
  transferConfigSalesforceDAO: TransferConfigSalesforceDAO,
  transferConfigSalesforceObjectDAO: TransferConfigSalesforceObjectDAO,
  transferConfigSalesforceObjectFieldDAO: TransferConfigSalesforceObjectFieldDAO,
  transferConfigDAO: TransferConfigDAO
) (implicit ex: ExecutionContext){

  // Insert or Update
  def saveTransferConfig(userId: String, userGroup: String, request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig) = {
    DB.localTx(implicit session => {
      val transferConfigId = request.id.map(id => {
        transferConfigDAO.save(TransferConfig(
          id,
          request.type_code,
          request.config_index,
          request.name,
          request.status,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        ))
      }).getOrElse({
        transferConfigDAO.create(TransferConfig(
          null, // 作成時は自動発番されるので使わない
          request.type_code,
          request.config_index,
          request.name,
          request.status,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        ))
      })

      request.detail.mail.map(detail => {
        saveMailTransferConfig(userId, userGroup, detail)
      })

      request.detail.salesforce.map(detail => {
        saveSalesforceTransferConfig(userId, userGroup, detail, cryptoConfig)
      })

      // TODO 何か返す？
    })
  }

  def saveMailTransferConfig(userId: String, userGroup: String, request: MailTransferConfigSaveRequest) = {
    val mailTransferConfigId = request.id.map(id => {
      transferConfigMailDAO.save(
        TransferConfigMail(
          id,
          request.transfer_config_id,
          request.use_cc,
          request.use_bcc,
          request.use_replyto,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    }).getOrElse({
      transferConfigMailDAO.create(
        TransferConfigMail(
          null, // 新規作成時は使用しない
          request.transfer_config_id,
          request.use_cc,
          request.use_bcc,
          request.use_replyto,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )

      val mailAddressIdList = request.mail_address_list
        .map(mailAddress => saveMailTransferConfigMailAddress(userId, userGroup, mailAddress))

      // 更新or作成リクエストに含まれていないメールアドレスは削除対象なので削除する
      transferConfigMailAddressDAO.getList(userGroup, mailTransferConfigId)
        .filterNot(address => mailAddressIdList.contains(address.id))
        .map(address => address.id)
        .foreach(address => transferConfigMailAddressDAO.erase(userGroup, address))

      request.id
    })
  }

  def saveMailTransferConfigMailAddress(userId: String, userGroup: String, request: MailTransferConfigMailAddressSaveRequest) = {
    request.id.map(id => {
      transferConfigMailAddressDAO.save(TransferConfigMailAddress(
        id,
        request.transfer_config_mail_id,
        request.address_index,
        request.name,
        request.address,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    }).getOrElse({
      transferConfigMailAddressDAO.create(TransferConfigMailAddress(
        null, // 新規作成時は使わない
        request.transfer_config_mail_id,
        request.address_index,
        request.name,
        request.address,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    })
  }

  def saveSalesforceTransferConfig(userId: String, userGroup: String, request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig) = {
    // 暗号化処理
    val crypto = Crypto(cryptoConfig.secret_key_string, cryptoConfig.cipher_algorithm, cryptoConfig.secret_key_algorithm, cryptoConfig.charset)
    val ivUserName = crypto.generateIV
    val sfUserName = crypto.encrypt(request.sf_user_name, ivUserName)
    val ivPassword = crypto.generateIV
    val sfPassword = crypto.encrypt(request.sf_password, ivPassword)
    val ivClientId = crypto.generateIV
    val sfClientId = crypto.encrypt(request.sf_client_id, ivClientId)
    val ivClientSecret = crypto.generateIV
    val sfClientSecret = crypto.encrypt(request.sf_client_secret, ivClientSecret)

    request.id.map(id => {
      transferConfigSalesforceDAO.save(TransferConfigSalesforce(
        id,
        request.transfer_config_id,
        request.sf_domain,
        request.api_version,
        sfUserName,
        sfPassword,
        sfClientId,
        sfClientSecret,
        ivUserName,
        ivPassword,
        ivClientId,
        ivClientSecret,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    }).getOrElse({
      transferConfigSalesforceDAO.create(TransferConfigSalesforce(
        null, // 新規作成時は使用しない
        request.transfer_config_id,
        request.sf_domain,
        request.api_version,
        sfUserName,
        sfPassword,
        sfClientId,
        sfClientSecret,
        ivUserName,
        ivPassword,
        ivClientId,
        ivClientSecret,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    })

    // Transferで使用するSalesforceオブジェクト一覧の保存
    val updatedObjects = request.objects.map(o => {
      saveSalesforceTransferConfigObject(o)
    })

    // 不要になったSalesforceオブジェクトの削除
    transferConfigSalesforceObjectDAO
      .getList(userGroup, request.id)
      .filterNot(o => updatedObjects.contains(o.id))
      .map(o => o.id)
      .foreach(o => transferConfigSalesforceObjectDAO.erase(userGroup, o))


  }

  def saveSalesforceTransferConfigObject(request: SalesforceTransferConfigObjectSaveRequest) = {
    ???
  }

  def insertSalesforceTransferConfigObjectField(request: SalesforceTransferConfigObjectFieldSaveRequest) = {
    ???
  }

  // Delete
  def deleteTransferConfig(id: BigInt) = {
    ???
  }

  def deleteTransferConfigDetail(id: BigInt) = {
    ???
  }

  def deleteMailTransferConfig(id: BigInt) = {
    ???
  }

  def deleteMailTransferConfigMailAddress(id: BigInt) = {
    ???
  }

  def deleteSalesforceTransferConfig(id: BigInt) = {
    ???
  }

  def deleteSalesforceTransferConfigObject(id: BigInt) = {
    ???
  }

  def deleteSalesforceTransferConfigObjectField(id: BigInt) = {
    ???
  }

}
