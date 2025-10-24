package net.macolabo.sform2.controllers

import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.models.entity.CryptoConfig

import javax.inject._
import net.macolabo.sform2.domain.services.External.Salesforce.{SalesforceCheckConnectionRequest, SalesforceCheckConnectionRequestJson, SalesforceCheckConnectionResponseJson, SalesforceConnectionService, SalesforceGetFieldResponseJson, SalesforceGetObjectResponse, SalesforceGetObjectResponseJson}
import net.macolabo.sform2.domain.services.Transfer.{TransferGetTransferConfigListJson, TransferGetTransferConfigResponseJson, TransferGetTransferConfigSelectListJson, TransferService, TransferUpdateTransferConfigRequestJson, TransferUpdateTransferConfigResponseJson}
import net.macolabo.sform2.domain.services.TransferConfig.TransferConfigService
import net.macolabo.sform2.domain.services.TransferConfig.save.{TransferConfigSaveRequest, TransferConfigSaveRequestJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.{Configuration, Logger}
import play.api.libs.json.JsError

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

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
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = transferService.getTransferConfigSelectList(sessionInfo)
        Ok(toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * TransferConfigの一覧を返す
   * GET /transfer/config/list
   * @return TransferConfigのリスト
   */
  def getTransferConfigList: Action[AnyContent] =  Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = transferService.getTransferConfigList(sessionInfo)
        Ok(toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * TransferConfig1件取得
   * @param transferConfigId TransferConfig ID
   * @return TransferConfig
   */
  def getTransferConfig(transferConfigId: Int): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = transferService.getTransferConfig(transferConfigId, cryptoConfig, sessionInfo)
        Ok(toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * TransferConfig更新
   * @return Result
   */
  def saveTransferConfig: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[TransferConfigSaveRequest].fold(
              errors => BadRequest(JsError.toJson(errors)),
              value => {
                transferConfigService.saveTransferConfig(value, cryptoConfig, sessionInfo)
                Ok("")
              }
            )
          case None => BadRequest("Missing JSON")
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  def deleteTransferConfig(transferConfigId: Int): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        transferConfigService.deleteTransferConfig(transferConfigId, sessionInfo)
        Ok("")
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * Salesforce疎通チェック
   * @return Result
   */
  def checkTransferSalesforce: Action[AnyContent] = Secure("HeaderClient").async { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(_) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[SalesforceCheckConnectionRequest].fold(
              errors => Future.successful(BadRequest(JsError.toJson(errors))),
              value =>
                salesforceConnectionService.checkConnection(value).map {
                  case Right(res) =>
                    Ok(res)
                  case Left(error) =>
                    logger.error(error)
                    Unauthorized(error)
                }
            )
          case None => Future.successful(BadRequest("Missing JSON"))
        }
      case Failure(e) =>
        Future.successful(BadRequest(s"Session invalid. ${e.getMessage}"))

    }
  }

  /**
   * Salesforce Object取得
   * @param transferConfigId TransferConfig ID
   * @return SalesforceのObject情報リスト
   */
  def getTransferSalesforceObject(transferConfigId: Int): Action[AnyContent] =  Secure("HeaderClient").async { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        transferService.getTransferConfig(transferConfigId, cryptoConfig, sessionInfo) match {
          case Some(transferConfig) =>
            transferConfig.detail.salesforce match {
              case Some(salesforceTransferConfig) =>
                salesforceConnectionService.getObject(salesforceTransferConfig).map {
                  case Left(e: String) => BadRequest(e)
                  case Right(objectList: List[SalesforceGetObjectResponse]) => Ok(toJson(objectList))
                }
              case None =>
                Future.successful(BadRequest)
            }
          case None =>
            Future.successful(NotFound)
        }
      case Failure(e) =>
        Future.successful(BadRequest("Session invalid." + e.getMessage))
    }
  }

  /**
   * Salesforce Field取得
   * @param transferConfigId TransferConfig ID
   * @param objectName オブジェクト名
   * @return SalesforceのField情報リスト
   */
  def getTransferSalesforceField(transferConfigId: Int, objectName: String): Action[AnyContent] =  Secure("HeaderClient").async { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        transferService.getTransferConfig(transferConfigId, cryptoConfig, sessionInfo) match {
          case Some(transferConfig) =>
            transferConfig.detail.salesforce match {
              case Some(salesforceTransferConfig) =>
                salesforceConnectionService.getField(salesforceTransferConfig, objectName).map {
                  case Left(e: String) =>
                    logger.error(e)
                    Unauthorized(e)
                  case Right(fields) =>
                    Ok(toJson(fields))
                }
              case None =>
                Future.successful(BadRequest)
            }
          case None =>
            Future.successful(BadRequest)
        }
      case Failure(e) =>
        Future.successful(BadRequest(s"Session invalid. ${e.getMessage}"))
    }
  }
}
