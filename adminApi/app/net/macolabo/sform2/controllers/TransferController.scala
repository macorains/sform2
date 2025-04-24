package net.macolabo.sform2.controllers

import net.macolabo.sform2.domain.models.entity.CryptoConfig

import javax.inject._
import net.macolabo.sform2.domain.services.External.Salesforce.{SalesforceCheckConnectionRequest, SalesforceCheckConnectionRequestJson, SalesforceCheckConnectionResponse, SalesforceCheckConnectionResponseJson, SalesforceConnectionService, SalesforceGetFieldResponse, SalesforceGetFieldResponseJson, SalesforceGetObjectResponse, SalesforceGetObjectResponseJson}
import net.macolabo.sform2.domain.services.Transfer.{TransferGetTransferConfigListJson, TransferGetTransferConfigResponse, TransferGetTransferConfigResponseJson, TransferGetTransferConfigSelectListJson, TransferGetTransferResponseSalesforceTransferConfig, TransferService, TransferUpdateTransferConfigRequest, TransferUpdateTransferConfigRequestJson, TransferUpdateTransferConfigResponse, TransferUpdateTransferConfigResponseJson}
import net.macolabo.sform2.domain.services.TransferConfig.TransferConfigService
import net.macolabo.sform2.domain.services.TransferConfig.save.{TransferConfigSaveRequest, TransferConfigSaveRequestJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.{Configuration, Logger}
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}

import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.concurrent.{Await, ExecutionContext, Future}

class TransferController @Inject() (
  val controllerComponents: SecurityComponents,
  transferService: TransferService,
  transferConfigService: TransferConfigService,
  salesforceConnectionService: SalesforceConnectionService,
  configuration: Configuration
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with TransferConfigSaveRequestJson
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

  private val logger = Logger(this.getClass).logger

  private val cryptoConfig: CryptoConfig = CryptoConfig(
    configuration.get[String]("sform.crypto.algorithm.key"),
    configuration.get[String]("sform.crypto.algorithm.cipher"),
    configuration.get[String]("sform.crypto.key"),
    configuration.get[String]("sform.crypto.charset"),
  )

  /**
   * フォーム作成画面のTransferConfig選択リスト生成用のデータ取得
   * GET /transfer/selectlist
   * @return TransferConfigのid,nameのリスト
   */
  def getSelectList: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = request.session.get("user_group").getOrElse("")
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
    val userGroup = request.session.get("user_group").getOrElse("")
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
    val userGroup = request.session.get("user_group").getOrElse("")
    val res = transferService.getTransferConfig(userGroup, transferConfigId, cryptoConfig)
    Ok(toJson(res))
  }

  /**
   * TransferConfig更新
   * @return Result
   */
  def saveTransferConfig: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = request.session.get("user_group").getOrElse("")
    val userId = profiles.asScala.headOption.map(_.getId)

    userId.map(uid =>
      request.body.asJson.map(body =>
        body.validate[TransferConfigSaveRequest] match {
          case transferConfigSaveRequest: JsSuccess[TransferConfigSaveRequest] =>
            transferConfigService.saveTransferConfig(uid, userGroup, transferConfigSaveRequest.get, cryptoConfig)
            Ok("")
          case e: JsError =>
            logger.error(e.toString)
            BadRequest(s"Json Validate error.($e)")
        }
      ).getOrElse(BadRequest("Json parse error."))
    ).getOrElse(InternalServerError("Can't get userID."))
  }

  /**
   * Salesforce疎通チェック
   * @return Result
   */
  def checkTransferSalesforce: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Await.result(request.body.asJson match {
      case Some(j: JsValue) =>
        j.validate[SalesforceCheckConnectionRequest].map(cr => {
          salesforceConnectionService.checkConnection(cr).map {
            case Right(res) => Ok(res)
            case Left(error) =>
              println(error) // TODO ログに出力する
              Unauthorized(error)
          }
        }).getOrElse(Future.successful(BadRequest))
      case _ =>
        println("fuga")
        Future.successful(BadRequest)
    }, Duration.Inf)
  }

  /**
   * Salesforce Object取得
   * @param transferConfigId TransferConfig ID
   * @return SalesforceのObject情報リスト
   */
  def getTransferSalesforceObject(transferConfigId: Int): Action[AnyContent] =  Secure("HeaderClient").async { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = request.session.get("user_group").getOrElse("")

    transferService.getTransferConfig(userGroup, transferConfigId, cryptoConfig) match {
      case Some(t: TransferGetTransferConfigResponse) =>
        t.detail.salesforce match {
          case Some(sf: TransferGetTransferResponseSalesforceTransferConfig) =>
            salesforceConnectionService.getObject(sf).map {
              case Left(e: String) => BadRequest(e)
              case Right(sr: List[SalesforceGetObjectResponse]) => Ok(toJson(sr))
            }
          case _ => Future.successful(BadRequest)
        }
      case _ => Future.successful(NotFound)
    }
  }

  /**
   * Salesforce Field取得
   * @param transferConfigId TransferConfig ID
   * @param objectName オブジェクト名
   * @return SalesforceのField情報リスト
   */
  def getTransferSalesforceField(transferConfigId: Int, objectName: String): Action[AnyContent] =  Secure("HeaderClient").async { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = request.session.get("user_group").getOrElse("")

    transferService.getTransferConfig(userGroup, transferConfigId, cryptoConfig) match {
      case Some(transferConfig: TransferGetTransferConfigResponse) =>
        transferConfig.detail.salesforce match {
          case Some(s: TransferGetTransferResponseSalesforceTransferConfig) =>
            salesforceConnectionService.getField(s, objectName).map {
              case Left(e: String) =>
                println(e)
                Unauthorized(e)
              case Right(field: List[SalesforceGetFieldResponse]) =>
                Ok(toJson(field))
            }
          case _ =>
            println("1")
            Future.successful(BadRequest)
        }
      case _ =>
        println("2")
        Future.successful(BadRequest)
    }
  }
}
