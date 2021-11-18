package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._

import javax.inject._
import net.macolabo.sform2.services.External.Salesforce.{SalesforceCheckConnectionRequest, SalesforceCheckConnectionRequestJson, SalesforceCheckConnectionResponse, SalesforceCheckConnectionResponseJson, SalesforceConnectionService, SalesforceGetFieldResponse, SalesforceGetFieldResponseJson, SalesforceGetObjectResponse, SalesforceGetObjectResponseJson}
import net.macolabo.sform2.services.Transfer.{TransferGetTransferConfigListJson, TransferGetTransferConfigResponseJson, TransferGetTransferConfigSelectListJson, TransferService, TransferUpdateTransferConfigRequest, TransferUpdateTransferConfigRequestJson, TransferUpdateTransferConfigResponse, TransferUpdateTransferConfigResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.DefaultEnv
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.{ExecutionContext, Future}

class TransferController @Inject() (
  val controllerComponents: SecurityComponents,
  transferService: TransferService,
  salesforceConnectionService: SalesforceConnectionService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with TransferGetTransferConfigSelectListJson
  with TransferGetTransferConfigListJson
  with TransferGetTransferConfigResponseJson
  with TransferUpdateTransferConfigRequestJson
  with TransferUpdateTransferConfigResponseJson
  with SalesforceCheckConnectionRequestJson
  with SalesforceCheckConnectionResponseJson
  with SalesforceGetObjectResponseJson
  with SalesforceGetFieldResponseJson
{

  /**
   * フォーム作成画面のTransferConfig選択リスト生成用のデータ取得
   * GET /transfer/selectlist
   * @return TransferConfigのid,nameのリスト
   */
  def getSelectList: Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = transferService.getTransferConfigSelectList(request.identity)
    Future.successful(Ok(toJson(res)))
   */
  }

  /**
   * TransferConfigの一覧を返す
   * GET /transfer/config/list
   * @return TransferConfigのリスト
   */
  def getTransferConfigList: Action[AnyContent] = Action.async { implicit request =>
  ???
    /*
    val res = transferService.getTransferConfigList(request.identity)
    Future.successful(Ok(toJson(res)))
*/
  }

  /**
   * TransferConfig1件取得
   * @param transferConfigId TransferConfig ID
   * @return TransferConfig
   */
  def getTransferConfig(transferConfigId: Int): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = transferService.getTransferConfig(request.identity, transferConfigId)
    Future.successful(Ok(toJson(res)))
    /
     */
  }

  /**
   * TransferConfig更新
   * @return Result
   */
  def saveTransferConfig: Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    println(request.body.asJson.get.validate[TransferUpdateTransferConfigRequest])
    val res = request.body.asJson.flatMap(r =>
      r.validate[TransferUpdateTransferConfigRequest].map(f => {
        transferService.updateTransferConfig(request.identity, f)
      }).asOpt)
    res match {
      case Some(s: TransferUpdateTransferConfigResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }

     */
  }

  /**
   * Salesforce疎通チェック
   * @return Result
   */
  def checkTransferSalesforce: Action[AnyContent] = Action.async { implicit request =>
    val res = request.body.asJson.flatMap(r =>
      r.validate[SalesforceCheckConnectionRequest].map(f => {
        salesforceConnectionService.checkConnection(f)
      }).asOpt)
    res match {
      case Some(s: SalesforceCheckConnectionResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
  }

  /**
   * Salesforce Object取得
   * @param transferConfigId TransferConfig ID
   * @return SalesforceのObject情報リスト
   */
  def getTransferSalesforceObject(transferConfigId: Int): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = transferService.getTransferConfig(request.identity,transferConfigId).flatMap(c => {
      c.detail.salesforce.flatMap(s => {
        salesforceConnectionService.getObject(s)
      })
    })
    res match {
      case Some(s: List[SalesforceGetObjectResponse]) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }

     */
  }

  /**
   * Salesforce Field取得
   * @param transferConfigId TransferConfig ID
   * @param objectName オブジェクト名
   * @return SalesforceのField情報リスト
   */
  def getTransferSalesforceField(transferConfigId: Int, objectName: String): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = transferService.getTransferConfig(request.identity,transferConfigId).flatMap(c => {
      c.detail.salesforce.flatMap(s => {
        salesforceConnectionService.getField(s, objectName)
      })
    })
    res match {
      case Some(s: List[SalesforceGetFieldResponse]) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
    */
  }
}
