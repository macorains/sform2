package net.macolabo.sform2.controllers

import javax.inject._
import net.macolabo.sform2.services.External.Salesforce.{SalesforceCheckConnectionRequest, SalesforceCheckConnectionRequestJson, SalesforceCheckConnectionResponse, SalesforceCheckConnectionResponseJson, SalesforceConnectionService, SalesforceGetFieldResponse, SalesforceGetFieldResponseJson, SalesforceGetObjectResponse, SalesforceGetObjectResponseJson}
import net.macolabo.sform2.services.Transfer.{TransferGetTransferConfigListJson, TransferGetTransferConfigResponseJson, TransferGetTransferConfigSelectListJson, TransferService, TransferUpdateTransferConfigRequest, TransferUpdateTransferConfigRequestJson, TransferUpdateTransferConfigResponse, TransferUpdateTransferConfigResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext

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
  with Pac4jUtil
{

  /**
   * フォーム作成画面のTransferConfig選択リスト生成用のデータ取得
   * GET /transfer/selectlist
   * @return TransferConfigのid,nameのリスト
   */
  def getSelectList: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = transferService.getTransferConfigSelectList(userGroup)
    Ok(toJson(res))
  }

  /**
   * TransferConfigの一覧を返す
   * GET /transfer/config/list
   * @return TransferConfigのリスト
   */
  def getTransferConfigList: Action[AnyContent] =  Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = transferService.getTransferConfigList(userGroup)
    Ok(toJson(res))
  }

  /**
   * TransferConfig1件取得
   * @param transferConfigId TransferConfig ID
   * @return TransferConfig
   */
  def getTransferConfig(transferConfigId: Int): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = transferService.getTransferConfig(userGroup, transferConfigId)
    Ok(toJson(res))
  }

  /**
   * TransferConfig更新
   * @return Result
   */
  def saveTransferConfig: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val userId = profiles.asScala.headOption.map(_.getId)

    val res = userId.flatMap(id => {request.body.asJson.flatMap(r =>
      r.validate[TransferUpdateTransferConfigRequest].map(f => {
        transferService.updateTransferConfig(id, userGroup, f)
      }).asOpt)
    })
    res match {
      case Some(s: TransferUpdateTransferConfigResponse) => Ok(toJson(s))
      case None => BadRequest
    }
  }

  /**
   * Salesforce疎通チェック
   * @return Result
   */
  def checkTransferSalesforce: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val res = request.body.asJson.flatMap(r =>
      r.validate[SalesforceCheckConnectionRequest].map(f => {
        salesforceConnectionService.checkConnection(f)
      }).asOpt)
    res match {
      case Some(s: SalesforceCheckConnectionResponse) => Ok(toJson(s))
      case None => BadRequest
    }
  }

  /**
   * Salesforce Object取得
   * @param transferConfigId TransferConfig ID
   * @return SalesforceのObject情報リスト
   */
  def getTransferSalesforceObject(transferConfigId: Int): Action[AnyContent] =  Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = transferService.getTransferConfig(userGroup, transferConfigId).flatMap(c => {
      c.detail.salesforce.flatMap(s => {
        salesforceConnectionService.getObject(s)
      })
    })
    res match {
      case Some(s: List[SalesforceGetObjectResponse]) => Ok(toJson(s))
      case None => BadRequest
    }
  }

  /**
   * Salesforce Field取得
   * @param transferConfigId TransferConfig ID
   * @param objectName オブジェクト名
   * @return SalesforceのField情報リスト
   */
  def getTransferSalesforceField(transferConfigId: Int, objectName: String): Action[AnyContent] =  Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = transferService.getTransferConfig(userGroup, transferConfigId).flatMap(c => {
      c.detail.salesforce.flatMap(s => {
        salesforceConnectionService.getField(s, objectName)
      })
    })
    res match {
      case Some(s: List[SalesforceGetFieldResponse]) => Ok(toJson(s))
      case None => BadRequest
    }
  }
}
