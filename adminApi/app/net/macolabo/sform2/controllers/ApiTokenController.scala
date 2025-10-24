package net.macolabo.sform2.controllers

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.services.ApiToken.ApiTokenService
import net.macolabo.sform2.domain.services.ApiToken.insert.ApiTokenInsertRequest
import net.macolabo.sform2.domain.utils.TokenUtil
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}

class ApiTokenController @Inject()(
  apiTokenService: ApiTokenService,
  val controllerComponents: SecurityComponents
)(
  implicit
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with TokenUtil
  with Pac4jUtil {

  def save: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[ApiTokenInsertRequest].fold(
              errors => BadRequest(JsError.toJson(errors)),
              value => {
                val result = apiTokenService.insert(value, sessionInfo)
                Ok(Json.toJson(result))
              }
            )
          case None => BadRequest("Missing JSON")
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  def getExpiry: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        apiTokenService.getExpiry(sessionInfo) match {
          case Some(s) => Ok(Json.parse(s"""{"expiry":"$s"}"""))
          case None => NotFound
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }
}
