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
    val result = request.body.asJson.flatMap(r =>
      r.validate[ApiTokenInsertRequest].map(f => {
        apiTokenService.insert(f, request.session)
      }).asOpt)
    result.map(res => Ok(Json.toJson(res))).getOrElse(BadRequest)
  }

  def getExpiry: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val res = apiTokenService.getExpiry(request.session)

    res match {
      case Some(s) => Ok(Json.parse(s"""{"expiry":"$s"}"""))
      case None => NotFound
    }
  }
}
