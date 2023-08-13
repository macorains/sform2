package net.macolabo.sform2.domain.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{TransferConfigDAO, TransferConfigMailAddressDAO, TransferConfigMailDAO, TransferConfigSalesforceDAO, TransferConfigSalesforceObjectDAO, TransferConfigSalesforceObjectFieldDAO}
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce, TransferConfigSalesforceObject, TransferConfigSalesforceObjectField}
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforceObjectField
import net.macolabo.sform2.domain.utils.Crypto
import scalikejdbc.{DB, DBSession}

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

class TransferService @Inject()(
  transferConfigMailDAO: TransferConfigMailDAO,
  transferConfigMailAddressDAO: TransferConfigMailAddressDAO,
  transferConfigSalesforceDAO: TransferConfigSalesforceDAO,
  transferConfigSalesforceObjectDAO: TransferConfigSalesforceObjectDAO,
  transferConfigSalesforceObjectFieldDAO: TransferConfigSalesforceObjectFieldDAO,
  transferConfigDAO: TransferConfigDAO
) (implicit ex: ExecutionContext)
  extends TransferGetTransferConfigSelectListJson
    with TransferGetTransferConfigListJson
    with TransferGetTransferConfigResponseJson
{

  /**
   * フォーム編集画面のTransferConfig選択リスト用データ取得
   * @param userGroup ユーザーグループ
   * @return TransferConfigのID,Nameのリスト
   */
  def getTransferConfigSelectList(userGroup: String): List[TransferGetTransferConfigSelectList] = {
    DB.localTx(implicit session => {
      transferConfigDAO.getList(userGroup).map(f => {
        TransferGetTransferConfigSelectList(
          f.id,
          f.name,
          f.type_code
        )
      })
    })
  }

  /**
   * TransferConfigのリスト取得
   * @param userGroup ユーザーグループ
   * @return TransferConfigのリスト
   */
  def getTransferConfigList(userGroup: String): List[TransferGetTransferConfigListResponse] = {
    DB.localTx(implicit session => {
      transferConfigDAO.getList(userGroup).map(f => {
        TransferGetTransferConfigListResponse(
          f.id,
          f.type_code,
          f.config_index,
          f.name,
          f.status
        )
      })
    })
  }

  /**
   * TransferConfigの詳細付きデータ取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return 詳細付きTransferConfig
   */
  def getTransferConfig(userGroup: String, transferConfigId: Int, cryptoConfig: CryptoConfig): Option[TransferGetTransferConfigResponse] = {
    DB.localTx(implicit session => {
      transferConfigDAO.get(userGroup, transferConfigId).map(f => {
        TransferGetTransferConfigResponse(
          f.id,
          f.type_code,
          f.config_index,
          f.name,
          f.status,
          TransferGetTransferResponseConfigDetail(
            getTransferConfigMail(userGroup, transferConfigId),
            getTransferConfigSalesforce(userGroup, transferConfigId, cryptoConfig)
          )
        )
      })
    })
  }

  /**
   * TransferConfigの更新
   * @param userId ユーザーID
   * @param userGroup ユーザーグループ
   * @param transferUpdateTransferConfigRequest TransferConfig更新リクエスト
   * @return Result
   */
  def updateTransferConfig(userId: String, userGroup: String, transferUpdateTransferConfigRequest: TransferUpdateTransferConfigRequest, cryptoConfig: CryptoConfig): TransferUpdateTransferConfigResponse = {
    DB.localTx(implicit session => {
      transferConfigDAO.save(
        TransferConfig(
          transferUpdateTransferConfigRequest.id,
          transferUpdateTransferConfigRequest.type_code,
          transferUpdateTransferConfigRequest.config_index,
          transferUpdateTransferConfigRequest.name,
          transferUpdateTransferConfigRequest.status,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )

      transferUpdateTransferConfigRequest.detail.mail.map(d => {
        updateTransferConfigMail(userGroup, userId, d)
      })

      transferUpdateTransferConfigRequest.detail.salesforce.map(d => {
        updateTransferConfigSalesforce(userGroup, userId, d, cryptoConfig)
      })

      TransferUpdateTransferConfigResponse(transferUpdateTransferConfigRequest.id)
    })
  }

  /**
   * TransferConfigMail更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfig TransferConfigMail更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMail(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfig: TransferUpdateTransferRequestMailTransferConfig): BigInt = {
    DB.localTx(implicit session => {
      transferConfigMailDAO.save(
        TransferConfigMail(
          transferUpdateTransferRequestMailTransferConfig.id,
          transferUpdateTransferRequestMailTransferConfig.transfer_config_id,
          transferUpdateTransferRequestMailTransferConfig.use_cc,
          transferUpdateTransferRequestMailTransferConfig.use_bcc,
          transferUpdateTransferRequestMailTransferConfig.use_replyto,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
      val updateMailAddressList = transferUpdateTransferRequestMailTransferConfig.mail_address_list.map(m => {
        m.id match {
          case Some(s: BigInt) if s > 0 => updateTransferConfigMailAddress(userGroup, userId, m)
          case _ => insertTransferConfigMailAddress(userGroup, userId, m)
        }
      })
      transferConfigMailAddressDAO.getList(userGroup, transferUpdateTransferRequestMailTransferConfig.id)
        .filterNot(c => updateMailAddressList.contains(c.id))
        .map(c => c.id)
        .foreach(c => transferConfigMailAddressDAO.delete(userGroup, c))
      transferUpdateTransferRequestMailTransferConfig.id
    })
  }

  /**
   * TransferConfigMailAddress更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress): BigInt = {
    DB.localTx(implicit session => {
      val id = transferUpdateTransferRequestMailTransferConfigMailAddress.id.getOrElse(BigInt(0))

      TransferConfigMailAddress(
        id,
        transferUpdateTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
        transferUpdateTransferRequestMailTransferConfigMailAddress.address_index,
        transferUpdateTransferRequestMailTransferConfigMailAddress.name,
        transferUpdateTransferRequestMailTransferConfigMailAddress.address,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )
      id
    })
  }

  /**
   * TransferConfigMailAddress作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return
   */
  private def insertTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress): BigInt = {
    DB.localTx(implicit session => {
      transferConfigMailAddressDAO.create(
        TransferConfigMailAddress(
          transferUpdateTransferRequestMailTransferConfigMailAddress.id.getOrElse(0),
          transferUpdateTransferRequestMailTransferConfigMailAddress.transfer_config_mail_id,
          transferUpdateTransferRequestMailTransferConfigMailAddress.address_index,
          transferUpdateTransferRequestMailTransferConfigMailAddress.name,
          transferUpdateTransferRequestMailTransferConfigMailAddress.address,
          userGroup,
          userId,
          userId,
          ZonedDateTime.now(),
          ZonedDateTime.now()
        )
      )
    })
  }

  /**
   * TransferConfigSalesforce更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfig TransferConfigSalesforce更新リクエスト
   * @return
   */
  private def updateTransferConfigSalesforce(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfig: TransferUpdateTransferRequestSalesforceTransferConfig, cryptoConfig: CryptoConfig): BigInt = {
    // 暗号化処理
    val crypto = Crypto(cryptoConfig.secret_key_string, cryptoConfig.cipher_algorithm, cryptoConfig.secret_key_algorithm, cryptoConfig.charset)
    val ivUserName = crypto.generateIV
    val sfUserName = crypto.encrypt(transferUpdateTransferRequestSalesforceTransferConfig.sf_user_name, ivUserName)
    val ivPassword = crypto.generateIV
    val sfPassword = crypto.encrypt(transferUpdateTransferRequestSalesforceTransferConfig.sf_password, ivPassword)
    val ivClientId = crypto.generateIV
    val sfClientId = crypto.encrypt(transferUpdateTransferRequestSalesforceTransferConfig.sf_client_id, ivClientId)
    val ivClientSecret = crypto.generateIV
    val sfClientSecret = crypto.encrypt(transferUpdateTransferRequestSalesforceTransferConfig.sf_client_secret, ivClientSecret)

    DB.localTx(implicit session => {
      transferConfigSalesforceDAO.save(TransferConfigSalesforce(
        transferUpdateTransferRequestSalesforceTransferConfig.id,
        transferUpdateTransferRequestSalesforceTransferConfig.transfer_config_id,
        transferUpdateTransferRequestSalesforceTransferConfig.sf_domain,
        transferUpdateTransferRequestSalesforceTransferConfig.api_version,
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

      val updatedObjects = transferUpdateTransferRequestSalesforceTransferConfig.objects.map(o => {
        o.id match {
          case Some(i: BigInt) if i > 0 => updateTransferConfigSalesforceObject(userGroup, userId, o)
          case _ => insertTransferConfigSalesforceObject(userGroup, userId, transferUpdateTransferRequestSalesforceTransferConfig.id, TransferUpdateTransferRequestSalesforceTransferConfigObjectToTransferInsertTransferRequestSalesforceTransferConfigObject(o))
        }
      })

      transferConfigSalesforceObjectDAO
        .getList(userGroup, transferUpdateTransferRequestSalesforceTransferConfig.id)
        .filterNot(o => updatedObjects.contains(o.id))
        .map(o => o.id)
        .foreach(o => transferConfigSalesforceObjectDAO.delete(userGroup, o))

      transferUpdateTransferRequestSalesforceTransferConfig.id
    })
  }

  /**
   * TransferConfigSalesforceObject更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfigObject　TransferUpdateTransferRequestSalesforceTransferConfigObject
   * @return 更新したID
   */
  private def updateTransferConfigSalesforceObject(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfigObject: TransferUpdateTransferRequestSalesforceTransferConfigObject): BigInt = {
    transferConfigSalesforceObjectDAO.save(
      TransferConfigSalesforceObject(
        transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)),
        transferUpdateTransferRequestSalesforceTransferConfigObject.transfer_config_salesforce_id,
        transferUpdateTransferRequestSalesforceTransferConfigObject.name,
        transferUpdateTransferRequestSalesforceTransferConfigObject.label,
        transferUpdateTransferRequestSalesforceTransferConfigObject.active,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )
    )

    val updatedFields = transferUpdateTransferRequestSalesforceTransferConfigObject.fields.map(f => {
      f.id match {
        case Some(i: BigInt) if i>0 => updateTransferConfigSalesforceObjectField(userGroup, userId, f)
        case _ => insertTransferConfigSalesforceObjectField(userGroup, userId, transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)), TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(f))
      }
    })

    transferConfigSalesforceObjectFieldDAO
      .getList(userGroup, transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0)))
        .filterNot(f => updatedFields.contains(f.id))
        .map(f => f.id)
        .foreach(f => transferConfigSalesforceObjectFieldDAO.delete(userGroup, f))

    transferUpdateTransferRequestSalesforceTransferConfigObject.id.getOrElse(BigInt(0))
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @param transferInsertTransferRequestSalesforceTransferConfigObject TransferInsertTransferRequestSalesforceTransferConfigObject
   * @return 作成したレコードのID
   */
  private def insertTransferConfigSalesforceObject(userGroup: String, userId: String, transferConfigSalesforceId: BigInt, transferInsertTransferRequestSalesforceTransferConfigObject: TransferInsertTransferRequestSalesforceTransferConfigObject): BigInt = {
    transferConfigSalesforceObjectDAO.create(
      TransferConfigSalesforceObject(
        0,
        transferConfigSalesforceId,
        transferInsertTransferRequestSalesforceTransferConfigObject.name,
        transferInsertTransferRequestSalesforceTransferConfigObject.label,
        transferInsertTransferRequestSalesforceTransferConfigObject.active,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )
    )
  }

  /**
   * TransferConfigSalesforceObject更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfigObjectField TransferUpdateTransferRequestSalesforceTransferConfigObjectField
   * @return 更新したID
   */
  private def updateTransferConfigSalesforceObjectField(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfigObjectField: TransferUpdateTransferRequestSalesforceTransferConfigObjectField): BigInt = {
    transferConfigSalesforceObjectFieldDAO.save(
      TransferConfigSalesforceObjectField(
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.id.getOrElse(0),
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.transfer_config_salesforce_object_id,
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.name,
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.label,
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.field_type,
        transferUpdateTransferRequestSalesforceTransferConfigObjectField.active,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )
    )
    transferUpdateTransferRequestSalesforceTransferConfigObjectField.id.getOrElse(0)
  }

  /**
   * TransferConfigSalesforceObject作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @param transferInsertTransferRequestSalesforceTransferConfigObjectField TransferInsertTransferRequestSalesforceTransferConfigObjectField
   * @return 作成したレコードのID
   */
  private def insertTransferConfigSalesforceObjectField(userGroup: String, userId: String, transferConfigSalesforceObjectId: BigInt, transferInsertTransferRequestSalesforceTransferConfigObjectField: TransferInsertTransferRequestSalesforceTransferConfigObjectField): BigInt = {
    transferConfigSalesforceObjectFieldDAO.create(
      TransferConfigSalesforceObjectField(
        0,
        transferConfigSalesforceObjectId,
        transferInsertTransferRequestSalesforceTransferConfigObjectField.name,
        transferInsertTransferRequestSalesforceTransferConfigObjectField.label,
        transferInsertTransferRequestSalesforceTransferConfigObjectField.field_type,
        transferInsertTransferRequestSalesforceTransferConfigObjectField.active,
        userGroup,
        userId,
        userId,
        ZonedDateTime.now(),
        ZonedDateTime.now()
      )
    )
  }


  /**
   * MailTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return MailTransfer用のconfig
   */
  private def getTransferConfigMail(userGroup: String, transferConfigId: BigInt): Option[TransferGetTransferResponseMailTransferConfig] = {
    transferConfigMailDAO.get(userGroup, transferConfigId).map(f => {
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

  /**
   * MailTransferConfigのメールアドレスリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @return MailTransferConfigに付随するメールアドレスリスト
   */
  private def getTransferConfigMailAddress(userGroup: String, transferConfigMailId: BigInt): List[TransferGetTransferResponseMailTransferConfigMailAddress] = {
    transferConfigMailAddressDAO.getList(userGroup, transferConfigMailId).map(f => {
      TransferGetTransferResponseMailTransferConfigMailAddress(
        f.id,
        f.transfer_config_mail_id,
        f.address_index,
        f.name,
        f.address
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return SalesforceTransfer用のconfig
   */
  private def getTransferConfigSalesforce(userGroup: String, transferConfigId: BigInt, cryptoConfig: CryptoConfig)(implicit session: DBSession): Option[TransferGetTransferResponseSalesforceTransferConfig] = {
    val crypto = Crypto(cryptoConfig.secret_key_string, cryptoConfig.cipher_algorithm, cryptoConfig.secret_key_algorithm, cryptoConfig.charset)

    transferConfigSalesforceDAO.get(transferConfigId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfig(
        f.id,
        f.transfer_config_id,
        f.sf_domain,
        f.api_version,
        crypto.decrypt(f.sf_user_name, f.iv_user_name),
        crypto.decrypt(f.sf_password, f.iv_password),
        crypto.decrypt(f.sf_client_id, f.iv_client_id),
        crypto.decrypt(f.sf_client_secret, f.iv_client_secret),
        getTransferConfigSalesforceObject(userGroup, f.id)
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig Object 取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceId TransferConfigSalesforce ID
   * @return SalesforceTransfer用のconfig Object リスト
   */
  private def getTransferConfigSalesforceObject(userGroup: String, transferConfigSalesforceId: BigInt): List[TransferGetTransferResponseSalesforceTransferConfigObject] = {
    transferConfigSalesforceObjectDAO.getList(userGroup, transferConfigSalesforceId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfigObject(
        f.id,
        f.transfer_config_salesforce_id,
        f.name,
        f.label,
        f.active,
        getTransferConfigSalesforceObjectField(userGroup, f.id)
      )
    })
  }

  /**
   * SalesforceTransfer用のconfig Object Field 取得
   * @param userGroup ユーザーグループ
   * @param transferConfigSalesforceObjectId TransferConfigSalesforceObject ID
   * @return SalesforceTransfer用のconfig Object Field リスト
   */
  private def getTransferConfigSalesforceObjectField(userGroup: String, transferConfigSalesforceObjectId: BigInt): List[TransferGetTransferResponseSalesforceTransferConfigObjectField] = {
    transferConfigSalesforceObjectFieldDAO.getList(userGroup, transferConfigSalesforceObjectId).map(f => {
      TransferGetTransferResponseSalesforceTransferConfigObjectField(
        f.id,
        f.transfer_config_salesforce_object_id,
        f.name,
        f.label,
        f.field_type,
        f.active
      )
    })
  }

  /*
  Update -> Insert変換
   */
  def TransferUpdateTransferRequestSalesforceTransferConfigObjectToTransferInsertTransferRequestSalesforceTransferConfigObject(src: TransferUpdateTransferRequestSalesforceTransferConfigObject): TransferInsertTransferRequestSalesforceTransferConfigObject = {
    TransferInsertTransferRequestSalesforceTransferConfigObject(
      src.transfer_config_salesforce_id,
      src.name,
      src.label,
      src.active,
      src.fields.map(f => TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(f))
    )
  }

  def TransferUpdateTransferRequestSalesforceTransferConfigObjectFieldToTransferInsertTransferRequestSalesforceTransferConfigObjectField(src: TransferUpdateTransferRequestSalesforceTransferConfigObjectField): TransferInsertTransferRequestSalesforceTransferConfigObjectField = {
    TransferInsertTransferRequestSalesforceTransferConfigObjectField(
      src.transfer_config_salesforce_object_id,
      src.name,
      src.label,
      src.field_type,
      src.active
    )
  }
}
