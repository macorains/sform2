package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import net.macolabo.sform2.services.Transfer.{TransferGetTransferConfigListJson, TransferGetTransferConfigResponseJson, TransferGetTransferConfigSelectListJson, TransferService, TransferUpdateTransferConfigRequest, TransferUpdateTransferConfigRequestJson, TransferUpdateTransferConfigResponse, TransferUpdateTransferConfigResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

class TransferController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  transferService: TransferService,
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components)
  with I18nSupport
  with TransferGetTransferConfigSelectListJson
  with TransferGetTransferConfigListJson
  with TransferGetTransferConfigResponseJson
  with TransferUpdateTransferConfigRequestJson
  with TransferUpdateTransferConfigResponseJson
{

  case class TransferGetConfigRequest(transferName: String)
  case class TransferSaveConfigRequest(transferName: String, config: JsValue)
  object TransferSaveConfigRequest {
    implicit def jsonTransferSaveConfigRequestWrites: Writes[TransferSaveConfigRequest] = Json.writes[TransferSaveConfigRequest]
    implicit def jsonTransferSaveConfigRequestReads: Reads[TransferSaveConfigRequest] = Json.reads[TransferSaveConfigRequest]
  }

  /**
   * フォーム作成画面のTransferConfig選択リスト生成用のデータ取得
   * GET /transfer/selectlist
   * @return TransferConfigのid,nameのリスト
   */
  def getSelectList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = transferService.getTransferConfigSelectList(request.identity)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * TransferConfigの一覧を返す
   * GET /transfer/config/list
   * @return TransferConfigのリスト
   */
  def getTransferConfigList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = transferService.getTransferConfigList(request.identity)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * TransferConfig1件取得
   * @param transferConfigId TransferConfig ID
   * @return TransferConfig
   */
  def getTransferConfig(transferConfigId: Int): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = transferService.getTransferConfig(request.identity, transferConfigId)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * TransferConfig更新
   * @return Result
   */
  def saveTransferConfig: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = request.body.asJson.flatMap(r =>
      r.validate[TransferUpdateTransferConfigRequest].map(f => {
        transferService.updateTransferConfig(request.identity, f)
      }).asOpt)
    res match {
      case Some(s: TransferUpdateTransferConfigResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
  }
}
