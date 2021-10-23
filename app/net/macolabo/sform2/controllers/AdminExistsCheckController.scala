package net.macolabo.sform2.controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import net.macolabo.sform2.services.User.{AdminExistsCheckResultJson, UserService}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._
import net.macolabo.sform2.utils.auth.DefaultEnv
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.{ExecutionContext, Future}

class AdminExistsCheckController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile] with I18nSupport with AdminExistsCheckResultJson {

  def check: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(AdminExistsCheckResult(userService.checkAdminExists)))
  }
}
