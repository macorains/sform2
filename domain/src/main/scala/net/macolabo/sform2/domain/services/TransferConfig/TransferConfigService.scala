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
    })

    val mailAddressIdList = request.mail_address_list
      .map(mailAddress => saveMailTransferConfigMailAddress(userId, userGroup, mailAddress))

    // 更新or作成リクエストに含まれていないメールアドレスは削除対象なので削除する
    transferConfigMailAddressDAO.getList(userGroup, mailTransferConfigId)
      .filterNot(address => mailAddressIdList.contains(address.id))
      .map(address => address.id)
      .foreach(address => transferConfigMailAddressDAO.delete(userGroup, address))

    mailTransferConfigId
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
      saveSalesforceTransferConfigObject(userId, userGroup, o)
    })

    // 不要になったSalesforceオブジェクトの削除
    transferConfigSalesforceObjectDAO
      .getList(userGroup, request.id)
      .filterNot(o => updatedObjects.contains(o.id))
      .map(o => o.id)
      .foreach(o => transferConfigSalesforceObjectDAO.delete(userGroup, o))


  }

  def saveSalesforceTransferConfigObject(userId: String, userGroup: String, request: SalesforceTransferConfigObjectSaveRequest) = {
    request.id.map(id => {
      transferConfigSalesforceObjectDAO.save(
        TransferConfigSalesforceObject(
          request.id.getOrElse(BigInt(0)),
          request.transfer_config_salesforce_id,
          request.name,
          request.label,
          request.active,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    }).getOrElse({
      transferConfigSalesforceObjectDAO.create(
        TransferConfigSalesforceObject(
          null,
          request.transfer_config_salesforce_id,
          request.name,
          request.label,
          request.active,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })

    // オブジェクトのフィールド情報保存
    val updateFields = request.fields.map(f => insertSalesforceTransferConfigObjectField(userId, userGroup, f))

    // 不要になったオブジェクトのフィールドを削除
    transferConfigSalesforceObjectFieldDAO
      .getList(userGroup, request.id)
      .filterNot(f =>updateFields.contains(f.id))
      .map(f => f.id)
      .foreach(f => transferConfigSalesforceObjectFieldDAO.delete(userGroup, f))
  }

  def insertSalesforceTransferConfigObjectField(userId: String, userGroup: String, request: SalesforceTransferConfigObjectFieldSaveRequest) = {
    request.id.map(id => {
      transferConfigSalesforceObjectFieldDAO.save(
        TransferConfigSalesforceObjectField(
          request.id.getOrElse(0),
          request.transfer_config_salesforce_object_id,
          request.name,
          request.label,
          request.field_type,
          request.active,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    }).getOrElse({
      transferConfigSalesforceObjectFieldDAO.create(
        TransferConfigSalesforceObjectField(
          null,
          request.transfer_config_salesforce_object_id,
          request.name,
          request.label,
          request.field_type,
          request.active,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })
  }

  // Delete
  def deleteTransferConfig(userId: String, userGroup: String, id: BigInt) = {
    DB.localTx(implicit session => {
      transferConfigMailDAO.get(userGroup, id).map(tc => deleteMailTransferConfig(userGroup, tc))
      transferConfigSalesforceDAO.get(id).map(tc => deleteSalesforceTransferConfig(userGroup, tc.id))
      transferConfigDAO.delete(userGroup, id)
    })
  }

  def deleteMailTransferConfig(userGroup: String, config: TransferConfigMail) = {
    transferConfigMailAddressDAO.getList(userGroup, config.id).map(address => deleteMailTransferConfigMailAddress(userGroup, address.id))
    transferConfigMailDAO.delete(userGroup, config.id)

  }

  def deleteMailTransferConfigMailAddress(userGroup: String, id: BigInt) = {
    transferConfigMailAddressDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfig(userGroup: String, id: BigInt) = {
    transferConfigSalesforceObjectDAO.getList(userGroup, Some(id)).map(tc => deleteSalesforceTransferConfigObject(userGroup, tc.id))
    transferConfigSalesforceDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfigObject(userGroup: String, id: BigInt) = {
    transferConfigSalesforceObjectFieldDAO.getList(userGroup, Some(id)).map(field => deleteSalesforceTransferConfigObjectField(userGroup, field.id))
    transferConfigSalesforceObjectDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfigObjectField(userGroup: String, id: BigInt) = {
    transferConfigSalesforceObjectFieldDAO.delete(userGroup, id)
  }

}
