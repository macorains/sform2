package net.macolabo.sform2.controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import net.macolabo.sform2.services.User.{AdminExistsCheckResultJson, UserService}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._
import net.macolabo.sform2.utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class AdminExistsCheckController @Inject() (
  components: ControllerComponents,
  userService: UserService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with AdminExistsCheckResultJson {

  def check: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(AdminExistsCheckResult(userService.checkAdminExists)))
  }
}
