package net.macolabo.sform2.controllers

import com.google.inject.Inject
import net.macolabo.sform2.services.ApiToken.ApiTokenService
import net.macolabo.sform2.services.ApiToken.insert.{ApiTokenInsertRequest, ApiTokenInsertRequestJson}
import net.macolabo.sform2.utils.TokenUtil
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Pac4jScalaTemplateHelper, Security, SecurityComponents}
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.CollectionHasAsScala

class ApiTokenController @Inject()(
  config: Configuration,
  components: ControllerComponents,
  apiTokenService: ApiTokenService,
  val controllerComponents: SecurityComponents
)(
  implicit
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with TokenUtil
  with Pac4jUtil
  with ApiTokenInsertRequestJson
{

  def generateApiTokenString: Action[AnyContent]  = Secure("HeaderClient")  { implicit request =>
    val tokenResponse = Json.parse(
      s"""
        {
          "token": ${generateToken}
        }
      """)
    Ok(tokenResponse)
  }

  def save: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = userId.flatMap(id => {
      request.body.asJson.flatMap(r =>
        r.validate[ApiTokenInsertRequest].map(f => {
          apiTokenService.insert(f)
        }).asOpt)
    })

    res match {
      case Some(s: Unit) => Ok
      case None => BadRequest
    }
  }
}
