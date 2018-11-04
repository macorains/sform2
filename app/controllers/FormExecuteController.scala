package controllers

import javax.inject._
import scala.concurrent.{ ExecutionContext, Future }
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.i18n.I18nSupport
import play.api.Environment
import play.api.libs.mailer._
import play.filters.csrf._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import org.webjars.play.WebJarsUtil
import models.daos.FormsDAO
import models.services.UserService
import utils.auth.DefaultEnv

class FormExecuteController @Inject() (
  env: Environment,
  dbapi: DBApi,
  mailerClient: MailerClient,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock,
  formsDAO: FormsDAO
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  def test: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    println(CSRF.getToken(request))
    request.body.asJson match {
      case Some(f) =>
        println(f \ "rcdata")
        Future.successful(Ok("OK"))
      case None => Future.successful(Ok(Json.toJson("error")))
      case _ => Future.successful(Ok(Json.toJson("a")))
    }
  }

  def getCsrfToken: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    CSRF.getToken(request) match {
      case Some(t: play.filters.csrf.CSRF.Token) =>
        Future.successful(Ok(Json.toJson(t.value)))
      case _ => Future.successful(Ok(Json.toJson("Could not get csrfToken.")))
    }
  }

  def auth: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val body: AnyContent = request.body
    println(body)
    val jsonBody: Option[JsValue] = body.asJson
    println(CSRF.getToken(request))
    println(body.toString)
    jsonBody.map { json =>
      val e = (json \ "email").as[String] + ":" + (json \ "group").as[String]
      val p = (json \ "password").as[String]
      val credentials = Credentials(e, p)
      credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if !user.activated => Future.successful(Ok(Json.toJson("User not activated.")))
          case Some(user) =>
            silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.init(authenticator).flatMap { token =>
                silhouette.env.authenticatorService.embed(
                  token,
                  Ok(Json.toJson("OK"))
                )
              }
            }
          case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }.recover {
        case _: ProviderException =>
          Ok(Json.toJson("Authentication Failed."))
      }
    }.getOrElse(Future.successful(Ok(Json.toJson("Request Parameter Incollect."))))
  }

  /**
   * フォーム定義取得
   * FormId
   * @return
   */
  def getForm: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) =>
        request.body.asJson match {
          case Some(f) =>
            val html = formsDAO.getHtml(f, request.host)
            Future.successful(Ok(html.getDataset))
          case None => Future.successful(Ok(Json.toJson("error")))
          case _ => Future.successful(Ok(Json.toJson("a")))
        }
      case None => Future.successful(Ok(Json.toJson("Unauthorized.")))
    }
  }

  /**
   * フォームバリデート
   * FormId
   * FormData
   * @return
   */
  def validateForm: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) =>
        request.body.asJson match {
          case Some(f) =>
            val html = formsDAO.validate(f, request.host)
            Future.successful(Ok(html.getDataset))
          case None => Future.successful(Ok(Json.toJson("error__")))
          case _ => Future.successful(Ok(Json.toJson("a__")))
        }
      case None => Future.successful(Ok(Json.toJson("Unauthorized.")))
    }
  }

  /**
   * フォーム登録
   * FormId
   * FormData
   * @return
   */
  def saveForm: Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) =>
        request.body.asJson match {
          case f: Option[JsValue] =>
            f.get match {
              case f1: JsValue =>
                val html = formsDAO.savePost(f1, request.host)
                Logger.info("FormExecuteController.saveForm sucess.")
                Future.successful(Ok(Json.toJson(html.getDataset)))
              case _ =>
                Logger.error("FormExecuteController.saveForm failed. (1)")
                Future.successful(Ok(Json.toJson("NG")))
            }
          case _ =>
            Logger.error("FormExecuteController.saveForm failed. (2)")
            Future.successful(Ok(Json.toJson("{msg:a}")))
        }
      case None =>
        Logger.error("FormExecuteController.saveForm failed. (Unauthorized)")
        Future.successful(Ok(Json.toJson("Unauthorized.")))
    }
  }

  def getjs(path: String): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.js.forminput.render(path)).as("text/javascript"))

  }

}

