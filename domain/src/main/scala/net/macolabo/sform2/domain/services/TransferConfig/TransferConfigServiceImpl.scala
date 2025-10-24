package net.macolabo.sform2.domain.services.TransferConfig

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
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
  def saveTransferConfig(request: TransferConfigSaveRequest, cryptoConfig: CryptoConfig, sessionInfo: SessionInfo): BigInt = {
    DB.localTx(implicit session => {
      val transferConfigId = request.id.map(id => {
        transferConfigDAO.save(TransferConfig(
          id,
          request.type_code,
          request.config_index.get, // 上書き保存の時は必ず入る想定
          request.name,
          request.status,
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        ))
      }).getOrElse({
        transferConfigDAO.create(TransferConfig(
          null, // 作成時は自動発番されるので使わない
          request.type_code,
          0, // createの場合、DAO側で既存最大値+1を挿入する
          request.name,
          request.status,
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        ))
      })

      request.detail.mail.map(detail => {
        saveMailTransferConfig(detail, transferConfigId, sessionInfo)
      })

      request.detail.salesforce.map(detail => {
        saveSalesforceTransferConfig(detail, cryptoConfig, transferConfigId, sessionInfo)
      })

      transferConfigId
    })
  }

  def saveMailTransferConfig(request: MailTransferConfigSaveRequest, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt = {
    val transferConfigMailId = request.id.map(id => {
      transferConfigMailDAO.save(
        TransferConfigMail(
          id,
          request.transfer_config_id.get, // 上書き保存の時は必ず入る想定
          request.use_cc,
          request.use_bcc,
          request.use_replyto,
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
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
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })

    val mailAddressIdList = request.mail_address_list
      .map(mailAddress => saveMailTransferConfigMailAddress(mailAddress, transferConfigMailId, sessionInfo))

    // 更新or作成リクエストに含まれていないメールアドレスは削除対象なので削除する
    transferConfigMailAddressDAO.getList(sessionInfo.user_group, transferConfigMailId)
      .filterNot(address => mailAddressIdList.contains(address.id))
      .map(address => address.id)
      .foreach(address => transferConfigMailAddressDAO.delete(sessionInfo.user_group, address))

    transferConfigMailId
  }

  def saveMailTransferConfigMailAddress(request: MailTransferConfigMailAddressSaveRequest, transferConfigMailId: BigInt, sessionInfo: SessionInfo): BigInt = {
    request.id.map(id => {
      transferConfigMailAddressDAO.save(TransferConfigMailAddress(
        id,
        request.transfer_config_mail_id.get, // 上書き保存の時は必ず入る想定
        request.address_index,
        request.name,
        request.address,
        sessionInfo.user_group,
        sessionInfo.user_id,
        sessionInfo.user_id,
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
        sessionInfo.user_group,
        sessionInfo.user_id,
        sessionInfo.user_id,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    })
  }

  def saveSalesforceTransferConfig(request: SalesforceTransferConfigSaveRequest, cryptoConfig: CryptoConfig, transferConfigId: BigInt, sessionInfo: SessionInfo): BigInt = {
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
        sessionInfo.user_group,
        sessionInfo.user_id,
        sessionInfo.user_id,
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
        sessionInfo.user_group,
        sessionInfo.user_id,
        sessionInfo.user_id,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      ))
    })

    // Transferで使用するSalesforceオブジェクト一覧の保存
    val updatedObjects = request.objects.map(o => {
      saveSalesforceTransferConfigObject(o, transferConfigSalesforceId, sessionInfo)
    })

    // 不要になったSalesforceオブジェクトの削除
    transferConfigSalesforceObjectDAO
      .getList(sessionInfo.user_group, request.id)
      .filterNot(o => updatedObjects.contains(o.id))
      .map(o => o.id)
      .foreach(o => transferConfigSalesforceObjectDAO.delete(sessionInfo.user_group, o))

    transferConfigSalesforceId
  }

  def saveSalesforceTransferConfigObject(request: SalesforceTransferConfigObjectSaveRequest, transferConfigSalesforceId: BigInt, sessionInfo: SessionInfo): BigInt = {
    val transferConfigSalesforceObjectId = request.id.map(id => {
      transferConfigSalesforceObjectDAO.save(
        TransferConfigSalesforceObject(
          id,
          request.transfer_config_salesforce_id.get, // 上書き保存の時は必ず入る想定
          request.name,
          request.label,
          request.active,
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
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
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })

    // オブジェクトのフィールド情報保存
    val updateFields = request.fields.map(f => insertSalesforceTransferConfigObjectField(f, transferConfigSalesforceObjectId, sessionInfo))

    // 不要になったオブジェクトのフィールドを削除
    transferConfigSalesforceObjectFieldDAO
      .getList(sessionInfo.user_group, request.id)
      .filterNot(f =>updateFields.contains(f.id))
      .map(f => f.id)
      .foreach(f => transferConfigSalesforceObjectFieldDAO.delete(sessionInfo.user_group, f))

    transferConfigSalesforceObjectId
  }

  def insertSalesforceTransferConfigObjectField(request: SalesforceTransferConfigObjectFieldSaveRequest, transferConfigSalesforceObjectId: BigInt, sessionInfo: SessionInfo): BigInt = {
    request.id.map(id => {
      transferConfigSalesforceObjectFieldDAO.save(
        TransferConfigSalesforceObjectField(
          id,
          request.transfer_config_salesforce_object_id.get, // 上書き保存の時は必ず入る想定
          request.name,
          request.label,
          request.field_type,
          request.active,
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
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
          sessionInfo.user_group,
          sessionInfo.user_id,
          sessionInfo.user_id,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })
  }

  // Delete
  def deleteTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int = {
    DB.localTx(implicit session => {
      transferConfigMailDAO.get(sessionInfo.user_group, id).map(tc => deleteMailTransferConfig(tc, sessionInfo))
      transferConfigSalesforceDAO.get(id).map(tc => deleteSalesforceTransferConfig(tc.id, sessionInfo))
      transferConfigDAO.delete(sessionInfo.user_group, id)
    })
  }

  def deleteMailTransferConfig(config: TransferConfigMail, sessionInfo: SessionInfo): Int = {
    transferConfigMailAddressDAO.getList(sessionInfo.user_group, config.id).map(address => deleteMailTransferConfigMailAddress(address.id, sessionInfo))
    transferConfigMailDAO.delete(sessionInfo.user_group, config.id)

  }

  def deleteMailTransferConfigMailAddress(id: BigInt, sessionInfo: SessionInfo): Int = {
    transferConfigMailAddressDAO.delete(sessionInfo.user_group, id)
  }

  def deleteSalesforceTransferConfig(id: BigInt, sessionInfo: SessionInfo): Int = {
    transferConfigSalesforceObjectDAO.getList(sessionInfo.user_group, Some(id)).map(tc => deleteSalesforceTransferConfigObject(tc.id, sessionInfo))
    transferConfigSalesforceDAO.delete(sessionInfo.user_group, id)
  }

  def deleteSalesforceTransferConfigObject(id: BigInt, sessionInfo: SessionInfo): Int = {
    transferConfigSalesforceObjectFieldDAO.getList(sessionInfo.user_group, Some(id)).map(field => deleteSalesforceTransferConfigObjectField(field.id, sessionInfo))
    transferConfigSalesforceObjectDAO.delete(sessionInfo.user_group, id)
  }

  def deleteSalesforceTransferConfigObjectField(id: BigInt, sessionInfo: SessionInfo): Int = {
    transferConfigSalesforceObjectFieldDAO.delete(sessionInfo.user_group, id)
  }

}
