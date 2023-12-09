package net.macolabo.sform2.domain.services.TransferConfig

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos._
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce, TransferConfigSalesforceObject, TransferConfigSalesforceObjectField}
import net.macolabo.sform2.domain.services.TransferConfig.save._
import net.macolabo.sform2.domain.utils.Crypto
import scalikejdbc.DB

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

class TransferConfigServiceImpl @Inject()(
  transferConfigMailDAO: TransferConfigMailDAO,
  transferConfigMailAddressDAO: TransferConfigMailAddressDAO,
  transferConfigSalesforceDAO: TransferConfigSalesforceDAO,
  transferConfigSalesforceObjectDAO: TransferConfigSalesforceObjectDAO,
  transferConfigSalesforceObjectFieldDAO: TransferConfigSalesforceObjectFieldDAO,
  transferConfigDAO: TransferConfigDAO
) (implicit ex: ExecutionContext) extends TransferConfigService {

  // Insert or Update
  def saveTransferConfig(userId: String, userGroup: String, request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig): BigInt = {
    DB.localTx(implicit session => {
      val transferConfigId = request.id.map(id => {
        transferConfigDAO.save(TransferConfig(
          id,
          request.type_code,
          request.config_index.get, // 上書き保存の時は必ず入る想定
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
          0, // createの場合、DAO側で既存最大値+1を挿入する
          request.name,
          1, // active固定
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        ))
      })

      request.detail.mail.map(detail => {
        saveMailTransferConfig(userId, userGroup, detail, transferConfigId)
      })

      request.detail.salesforce.map(detail => {
        saveSalesforceTransferConfig(userId, userGroup, detail, cryptoConfig, transferConfigId)
      })

      transferConfigId
    })
  }

  def saveMailTransferConfig(userId: String, userGroup: String, request: MailTransferConfigSaveRequest, transferConfigId: BigInt): BigInt = {
    val transferConfigMailId = request.id.map(id => {
      transferConfigMailDAO.save(
        TransferConfigMail(
          id,
          request.transfer_config_id.get, // 上書き保存の時は必ず入る想定
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
          transferConfigId,
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
      .map(mailAddress => saveMailTransferConfigMailAddress(userId, userGroup, mailAddress, transferConfigMailId))

    // 更新or作成リクエストに含まれていないメールアドレスは削除対象なので削除する
    transferConfigMailAddressDAO.getList(userGroup, transferConfigMailId)
      .filterNot(address => mailAddressIdList.contains(address.id))
      .map(address => address.id)
      .foreach(address => transferConfigMailAddressDAO.delete(userGroup, address))

    transferConfigMailId
  }

  def saveMailTransferConfigMailAddress(userId: String, userGroup: String, request: MailTransferConfigMailAddressSaveRequest, transferConfigMailId: BigInt): BigInt = {
    request.id.map(id => {
      transferConfigMailAddressDAO.save(TransferConfigMailAddress(
        id,
        request.transfer_config_mail_id.get, // 上書き保存の時は必ず入る想定
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
        transferConfigMailId,
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

  def saveSalesforceTransferConfig(userId: String, userGroup: String, request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig, transferConfigId: BigInt): BigInt = {
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

    val transferConfigSalesforceId = request.id.map(id => {
      transferConfigSalesforceDAO.save(TransferConfigSalesforce(
        id,
        request.transfer_config_id.get, // 上書き保存の時は必ず入る想定
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
        transferConfigId,
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
      saveSalesforceTransferConfigObject(userId, userGroup, o, transferConfigSalesforceId)
    })

    // 不要になったSalesforceオブジェクトの削除
    transferConfigSalesforceObjectDAO
      .getList(userGroup, request.id)
      .filterNot(o => updatedObjects.contains(o.id))
      .map(o => o.id)
      .foreach(o => transferConfigSalesforceObjectDAO.delete(userGroup, o))

    transferConfigSalesforceId
  }

  def saveSalesforceTransferConfigObject(userId: String, userGroup: String, request: SalesforceTransferConfigObjectSaveRequest, transferConfigSalesforceId: BigInt): BigInt = {
    val transferConfigSalesforceObjectId = request.id.map(id => {
      transferConfigSalesforceObjectDAO.save(
        TransferConfigSalesforceObject(
          id,
          request.transfer_config_salesforce_id.get, // 上書き保存の時は必ず入る想定
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
          transferConfigSalesforceId,
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
    val updateFields = request.fields.map(f => insertSalesforceTransferConfigObjectField(userId, userGroup, f, transferConfigSalesforceObjectId))

    // 不要になったオブジェクトのフィールドを削除
    transferConfigSalesforceObjectFieldDAO
      .getList(userGroup, request.id)
      .filterNot(f =>updateFields.contains(f.id))
      .map(f => f.id)
      .foreach(f => transferConfigSalesforceObjectFieldDAO.delete(userGroup, f))

    transferConfigSalesforceObjectId
  }

  def insertSalesforceTransferConfigObjectField(userId: String, userGroup: String, request: SalesforceTransferConfigObjectFieldSaveRequest, transferConfigSalesforceObjectId: BigInt): BigInt = {
    request.id.map(id => {
      transferConfigSalesforceObjectFieldDAO.save(
        TransferConfigSalesforceObjectField(
          id,
          request.transfer_config_salesforce_object_id.get, // 上書き保存の時は必ず入る想定
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
          transferConfigSalesforceObjectId,
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
  def deleteTransferConfig(userId: String, userGroup: String, id: BigInt): Int = {
    DB.localTx(implicit session => {
      transferConfigMailDAO.get(userGroup, id).map(tc => deleteMailTransferConfig(userGroup, tc))
      transferConfigSalesforceDAO.get(id).map(tc => deleteSalesforceTransferConfig(userGroup, tc.id))
      transferConfigDAO.delete(userGroup, id)
    })
  }

  def deleteMailTransferConfig(userGroup: String, config: TransferConfigMail): Int = {
    transferConfigMailAddressDAO.getList(userGroup, config.id).map(address => deleteMailTransferConfigMailAddress(userGroup, address.id))
    transferConfigMailDAO.delete(userGroup, config.id)

  }

  def deleteMailTransferConfigMailAddress(userGroup: String, id: BigInt): Int = {
    transferConfigMailAddressDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfig(userGroup: String, id: BigInt): Int = {
    transferConfigSalesforceObjectDAO.getList(userGroup, Some(id)).map(tc => deleteSalesforceTransferConfigObject(userGroup, tc.id))
    transferConfigSalesforceDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfigObject(userGroup: String, id: BigInt): Int = {
    transferConfigSalesforceObjectFieldDAO.getList(userGroup, Some(id)).map(field => deleteSalesforceTransferConfigObjectField(userGroup, field.id))
    transferConfigSalesforceObjectDAO.delete(userGroup, id)
  }

  def deleteSalesforceTransferConfigObjectField(userGroup: String, id: BigInt): Int = {
    transferConfigSalesforceObjectFieldDAO.delete(userGroup, id)
  }

}
