package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import models._
import net.macolabo.sform2.models.RsResultSet
import net.macolabo.sform2.models.daos.{FormsDAO, TransferTaskDAO}
import net.macolabo.sform2.services.Form.{FormGetFormResponseJson, FormService}
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Environment, _}
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.libs.json.Json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

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
  transferTaskDAO: TransferTaskDAO,
  formService: FormService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with FormGetFormResponseJson {

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formsDAO.getData(request.identity, hashed_form_id)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォームデータ取得(new)
   * @return フォームデータ
   */
  def _get(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.getForm(hashed_form_id, request.identity)
    Future.successful(Ok(toJson(res)))
  }


  //  def get2: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
//    val res = formsDAO.getList(request.identity)
//    Future.successful(Ok(Json.toJson(res)))
//                                                                                                                                                  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formsDAO.getList(request.identity)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォーム登録／更新
   * POST /form
   * @return
   */
  def create(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
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
      case r: RsResultSet => Future.successful(Ok(toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }

  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    Future.successful(Ok(toJson(formsDAO.delete(hashed_form_id))))
  }

  /**
   * フォームHTML取得
   * GET /form/html/<form_id>
   * @return
   */
  def getHtml: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      formsDAO.getHtml(data, request.host)
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(toJson(r)))
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
      case r: RsResultSet => Future.successful(Ok(toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }
}
