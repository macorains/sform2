package net.macolabo.sform2.controllers

import com.google.inject.Inject
import net.macolabo.sform2.domain.services.ApiToken.ApiTokenService
import net.macolabo.sform2.domain.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertRequestJson}
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
  with Pac4jUtil
  with ApiTokenInsertRequestJson {

  def generateApiTokenString: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val tokenResponse = Json.parse(
      s"""
        {
          "token": "$generateToken"
        }
      """)
    Ok(tokenResponse)
  }

  def save: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = userId.flatMap(uid => {
      request.body.asJson.flatMap(r =>
        r.validate[ApiTokenInsertRequest].map(f => {
          apiTokenService.insert(f, uid, userGroup)
        }).asOpt)
    })

    res match {
      case Some(_) => Ok
      case None => BadRequest
    }
  }

  def getExpiry: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = userId.flatMap(_ => apiTokenService.getExpiry(userGroup))

    res match {
      case Some(s) => Ok(Json.parse(s"""{"expiry":"$s"}"""))
      case None => BadRequest
    }
  }
}
