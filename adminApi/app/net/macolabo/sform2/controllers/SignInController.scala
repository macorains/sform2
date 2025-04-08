package net.macolabo.sform2.controllers

import net.macolabo.sform2.domain.models.entity.user.User

import javax.inject.Inject
import net.macolabo.sform2.domain.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Logger}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.UserProfile
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param controllerComponents   The Play controller components.
 * @param userService            The user service implementation.
 * @param configuration          The Play configuration.
 * @param cache                  The cache instance.
 * @param mailerClient The Mailer instance.
 * @param webJarsUtil            The webjar util.
 */
class SignInController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService,
  configuration: Configuration,
  cache: SyncCacheApi,
  mailerClient: MailerClient,
  ws: WSClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with Pac4jUtil
{

  val logger: Logger = Logger(this.getClass)

  /**
   * Handles the submitted form.
   * @return The result to display.
   */

  def oidcSignin: Action[AnyContent] = Secure("OidcClient").async { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    checkUser(profiles.get(0)).map {
      case Some(u) =>
        val generator = new JwtGenerator(new SecretSignatureConfiguration("12345678901234567890123456789012"))
        val token = generator.generate(profiles.get(0))
        Ok(net.macolabo.sform2.views.html.jwt(configuration.get[String]("sform.oauth.redirectUrl"), token))
      case None =>
        Forbidden(net.macolabo.sform2.views.html.redirect("http://localhost:5173/login_failed"))
    }
  }

  private def checkUser(profile: UserProfile) = {
    profile.getAttribute("email") match {
      case s: String =>
        userService.retrieve(s)
      case _ => Future.successful(None)
    }
  }
}
