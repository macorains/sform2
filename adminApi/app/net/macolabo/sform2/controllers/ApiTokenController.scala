package net.macolabo.sform2.controllers

import com.google.inject.Inject
import net.macolabo.sform2.domain.services.ApiToken.ApiTokenService
import net.macolabo.sform2.domain.services.ApiToken.insert.ApiTokenInsertRequest
import net.macolabo.sform2.domain.utils.TokenUtil
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.CollectionHasAsScala

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
    val profiles = getProfiles(controllerComponents)(request)
    val userId = request.session.get("user_id").getOrElse(profiles.asScala.headOption.map(_.getId).getOrElse("Unknown"))
    val userGroup = request.session.get("user_group").getOrElse("Unknown")

    val result = request.body.asJson.flatMap(r =>
      r.validate[ApiTokenInsertRequest].map(f => {
        // apiTokenService.insert(f, userId, userGroup)
        apiTokenService.insert(f, request.session)
      }).asOpt)
    result.map(res => Ok(Json.toJson(res))).getOrElse(BadRequest)
  }

  def getExpiry: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = request.session.get("user_group").getOrElse("")


    //val res = userId.flatMap(_ => apiTokenService.getExpiry(userGroup))
    val res = userId.flatMap(_ => apiTokenService.getExpiry(request.session))

    res match {
      case Some(s) => Ok(Json.parse(s"""{"expiry":"$s"}"""))
      case None => NotFound
    }
  }
}
