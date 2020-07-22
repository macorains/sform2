package net.macolabo.sform2.services.Transfer

import java.time.ZonedDateTime

import com.google.inject.Inject
import net.macolabo.sform2.models.User
import net.macolabo.sform2.models.transfer.{TransferConfig, TransferConfigMail, TransferConfigMailAddress, TransferConfigSalesforce}

import scala.concurrent.ExecutionContext

class TransferService @Inject() (implicit ex: ExecutionContext) extends TransferGetTransferConfigSelectListJson with TransferGetTransferConfigListJson with TransferGetTransferConfigResponseJson {

  /**
   * フォーム編集画面のTransferConfig選択リスト用データ取得
   * @param identity 認証情報
   * @return TransferConfigのID,Nameのリスト
   */
  def getTransferConfigSelectList(identity: User): List[TransferGetTransferConfigSelectList] = {
    val userGroup = identity.group.getOrElse("")
    TransferConfig.getList(userGroup).map(f => {
      TransferGetTransferConfigSelectList(
        f.id,
        f.name
      )
    })
  }

  /**
   * TransferConfigのリスト取得
   * @param identity 認証情報
   * @return TransferConfigのリスト
   */
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

  /**
   * TransferConfigの詳細付きデータ取得
   * @param identity 認証情報
   * @param transferConfigId TransferConfig ID
   * @return 詳細付きTransferConfig
   */
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

  /**
   * TransferConfigの更新
   * @param identity 認証情報
   * @param transferUpdateTransferConfigRequest TransferConfig更新リクエスト
   * @return Result
   */
  def updateTransferConfig(identity: User, transferUpdateTransferConfigRequest: TransferUpdateTransferConfigRequest): TransferUpdateTransferConfigResponse = {
    val userId = identity.userID.toString
    val userGroup = identity.group.getOrElse("")
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
    ).update

    transferUpdateTransferConfigRequest.detail.mail.map(d => {
      updateTransferConfigMail(userGroup, userId, d)
    })
    transferUpdateTransferConfigRequest.detail.salesforce.map(d => {
      updateTransferConfigSalesforce(userGroup, userId, d)
    })
    TransferUpdateTransferConfigResponse(transferUpdateTransferConfigRequest.id)
  }

  /**
   * TransferConfigMail更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfig TransferConfigMail更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMail(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfig: TransferUpdateTransferRequestMailTransferConfig): Int = {
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
    ).update
    val updateMailAddressList = transferUpdateTransferRequestMailTransferConfig.mail_address_list.map(m => {
      m.id match {
        case Some(s: Int) if s > 0 => updateTransferConfigMailAddress(userGroup, userId, m)
        case _ => insertTransferConfigMailAddress(userGroup, userId, m)
      }
    })
    TransferConfigMailAddress.getList(userGroup, transferUpdateTransferRequestMailTransferConfig.id)
        .filterNot(c => updateMailAddressList.contains(c.id))
        .map(c => c.id)
        .foreach(c => TransferConfigMailAddress.erase(userGroup, c))
    transferUpdateTransferRequestMailTransferConfig.id
  }

  /**
   * TransferConfigMailAddress更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return Result
   */
  private def updateTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress) = {
    val id = transferUpdateTransferRequestMailTransferConfigMailAddress.id.getOrElse(0)
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
    ).update
    id
  }

  /**
   * TransferConfigMailAddress作成
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestMailTransferConfigMailAddress TransferConfigMailAddress更新リクエスト
   * @return
   */
  private def insertTransferConfigMailAddress(userGroup: String, userId: String, transferUpdateTransferRequestMailTransferConfigMailAddress: TransferUpdateTransferRequestMailTransferConfigMailAddress): Int = {
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
    ).insert
  }

  /**
   * TransferConfigSalesforce更新
   * @param userGroup ユーザーグループ
   * @param userId ユーザーID
   * @param transferUpdateTransferRequestSalesforceTransferConfig TransferConfigSalesforce更新リクエスト
   * @return
   */
  private def updateTransferConfigSalesforce(userGroup: String, userId: String, transferUpdateTransferRequestSalesforceTransferConfig: TransferUpdateTransferRequestSalesforceTransferConfig): Int = {
    TransferConfigSalesforce(
      transferUpdateTransferRequestSalesforceTransferConfig.id,
      transferUpdateTransferRequestSalesforceTransferConfig.transfer_config_id,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_user_name,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_password,
      transferUpdateTransferRequestSalesforceTransferConfig.sf_security_token,
      userGroup,
      userId,
      userId,
      ZonedDateTime.now(),
      ZonedDateTime.now()
    ).update
  }

  /**
   * MailTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return MailTransfer用のconfig
   */
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

  /**
   * MailTransferConfigのメールアドレスリスト取得
   * @param userGroup ユーザーグループ
   * @param transferConfigMailId TransferConfigMail ID
   * @return MailTransferConfigに付随するメールアドレスリスト
   */
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

  /**
   * SalesforceTransfer用のconfig取得
   * @param userGroup ユーザーグループ
   * @param transferConfigId TransferConfig ID
   * @return SalesforceTransfer用のconfig
   */
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
