package controllers

import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.Environment
import models._
import models.daos.{ FormsDAO, TransferTaskDAO }
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.{ DefaultEnv, WithProvider }
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._

import scala.concurrent.{ ExecutionContext, Future }

class FormController @Inject() (
  env: Environment,
  dbapi: DBApi,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  formsDAO: FormsDAO,
  transferTaskDAO: TransferTaskDAO
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async { implicit request =>
    val res = formsDAO.getData(request.identity, hashed_form_id)
    Future.successful(Ok(Json.toJson(res)))
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async { implicit request =>
    val res = formsDAO.getList(request.identity)
    Future.successful(Ok(Json.toJson(res)))
  }

  /**
   * フォーム登録／更新
   * POST /form
   * @return
   */
  def create(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async { implicit request =>
    val identity = request.identity
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      val formInsertResult = formsDAO.insert(data, identity)
      (formInsertResult.getDataset \ "id").as[String] match {
        case s: String if s != "failed" =>
          transferTaskDAO.bulkSave(data, identity)
          formInsertResult
        case _ => formInsertResult
      }
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(Json.toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }

  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID)).async { implicit request =>
    Future.successful(Ok(Json.toJson(formsDAO.delete(hashed_form_id))))
  }

  /**
   * フォームHTML取得
   * GET /form/html/<form_id>
   * @return
   */
  def getHtml(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      formsDAO.getHtml(data, request.host)
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(Json.toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }

  /**
   * フォームバリデート
   * POST /form/validate
   * @return
   */
  def validate(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      formsDAO.validate(data, request.host)
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(Json.toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }
}
