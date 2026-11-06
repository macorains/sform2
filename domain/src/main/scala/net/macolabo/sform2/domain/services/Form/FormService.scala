package net.macolabo.sform2.domain.services.Form

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.{Body, Content, Destination, Message, SendEmailRequest}
import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.daos.{FormDAO, TransferConfigMailAddressDAOImpl}
import net.macolabo.sform2.domain.services.Form.delete.FormDeleteResponse
import net.macolabo.sform2.domain.services.Form.get.FormGetResponse
import net.macolabo.sform2.domain.services.Form.list.FormListResponse
import net.macolabo.sform2.domain.services.Form.insert.FormInsertResponse
import net.macolabo.sform2.domain.services.Form.sendtestmail.{FormSendTestMailRequest, FormSendTestMailResponse}
import net.macolabo.sform2.domain.services.Form.update.{FormUpdateRequest, FormUpdateResponse}
import play.api.mvc.Session
import scalikejdbc.DB

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class FormService @Inject()(
  formDAO: FormDAO,
  transferConfigMailAddressDAO: TransferConfigMailAddressDAOImpl
) (implicit ex: ExecutionContext) {

  /**
   * フォーム詳細データ取得
   *
   * @param hashed_form_id フォームhashed ID
   * @param sessionInfo セッションデータ
   * @return フォームデータ
   */
  def getForm(hashed_form_id: String, sessionInfo: SessionInfo): Option[FormGetResponse] = {
    DB.localTx(implicit session => {
      formDAO.getByHashedId(sessionInfo.user_group, hashed_form_id)
    })
  }

  /**
   * フォームリスト取得
   *
   * @param sessionInfo セッションデータ
   * @return フォームリスト
   */
  def getList(sessionInfo: SessionInfo): FormListResponse = {
    DB.localTx(implicit session => {
      formDAO.getList(sessionInfo.user_group)
    })
  }

  /**
   * フォーム作成
   *
   * @param formUpdateRequest フォーム作成リクエストデータ
   * @param sessionInfo セッションデータ
   * @return フォーム作成結果レスポンス
   */
  def insert(formUpdateRequest: FormUpdateRequest, sessionInfo: SessionInfo): FormInsertResponse = {
    DB.localTx(implicit session => {
      val (id, hashedId) = formDAO.update(sessionInfo.user_id, sessionInfo.user_group, formUpdateRequest)
      FormInsertResponse(id, hashedId)
    })
  }

  /**
   * フォーム更新
   *
   * @param formUpdateRequest フォーム更新リクエストデータ
   * @param sessionInfo セッションデータ
   * @return フォーム更新結果レスポンス
   */
  def update(formUpdateRequest: FormUpdateRequest, sessionInfo: SessionInfo): FormUpdateResponse = {
    DB.localTx(implicit session => {
      val (id, _) = formDAO.update(sessionInfo.user_id, sessionInfo.user_group, formUpdateRequest)
      FormUpdateResponse(id)
    })
  }

  /**
   * フォーム削除
   *
   * @param hashed_form_id ハッシュ化フォームID
   * @param sessionInfo セッションデータ
   * @return フォーム削除結果レスポンス
   */
  def deleteForm(hashed_form_id: String, sessionInfo: SessionInfo): FormDeleteResponse = {
    DB.localTx(implicit session => {
      formDAO.deleteByHashedId(sessionInfo.user_group, hashed_form_id)
    })
  }

  /**
   * メール送信テスト
   *
   * @param request メール送信テストリクエスト
   * @return メール送信テスト結果レスポンス
   */
  def sendTestMail(request: FormSendTestMailRequest): FormSendTestMailResponse = {
    DB.localTx(implicit session => {
      val fromAddressOpt = transferConfigMailAddressDAO.get(request.from_address_id).map(_.address)
      val toAddressOpt = request.to_address.filter(_.nonEmpty)
        .orElse(request.to_address_id.flatMap(id => transferConfigMailAddressDAO.get(id).map(_.address)))

      (fromAddressOpt, toAddressOpt) match {
        case (Some(fromAddr), Some(toAddr)) =>
          Try {
            val destination = new Destination().withToAddresses(toAddr)

            val ccAddressOpt = request.cc_address.filter(_.nonEmpty)
              .orElse(request.cc_address_id.flatMap(id => transferConfigMailAddressDAO.get(id).map(_.address)))
            ccAddressOpt.foreach(cc => destination.withCcAddresses(cc))

            request.bcc_address_id.flatMap(id => transferConfigMailAddressDAO.get(id).map(_.address))
              .foreach(bcc => destination.withBccAddresses(bcc))

            val sesRequest = new SendEmailRequest()
              .withDestination(destination)
              .withMessage(new Message()
                .withBody(new Body()
                  .withText(new Content().withCharset("UTF-8").withData(request.body)))
                .withSubject(new Content().withCharset("UTF-8").withData(request.subject)))
              .withSource(fromAddr)

            val replyToAddresses = request.replyto_address_id
              .flatMap(id => transferConfigMailAddressDAO.get(id).map(_.address))
              .map(addr => List(addr).asJava)
              .getOrElse(java.util.List.of[String]())
            if (!replyToAddresses.isEmpty) sesRequest.withReplyToAddresses(replyToAddresses)

            val sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
              .withRegion(Regions.AP_NORTHEAST_1).build()
            sesClient.sendEmail(sesRequest)
          } match {
            case Success(_) => FormSendTestMailResponse(result = true, message = "テストメールを送信しました")
            case Failure(e) => FormSendTestMailResponse(result = false, message = s"送信に失敗しました: ${e.getMessage}")
          }
        case (None, _) =>
          FormSendTestMailResponse(result = false, message = "送信元メールアドレスが見つかりません")
        case (_, None) =>
          FormSendTestMailResponse(result = false, message = "送信先メールアドレスが指定されていません")
      }
    })
  }
}
