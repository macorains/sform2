package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.services.UserService
import models.json.AdminExistsCheckResultJson
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class AdminExistsCheckController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with AdminExistsCheckResultJson {

  def check: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(Json.toJson(AdminExistsCheckResult(userService.checkAdminExists))))
  }
}
