package net.macolabo.sform2.controllers

import javax.inject.Inject
import net.macolabo.sform2.domain.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Logger}
import play.api.i18n.I18nSupport
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.UserProfile
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.context.PlayContextFactory
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
  ws: WSClient,
  playContextFactory: PlayContextFactory
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
    val profile = profiles.get(0)
    checkUser(profile).map {
      case Some(u) =>
        configuration.getOptional[String]("sform.jwt.secret").map(jwtSecret => {
          val generator = new JwtGenerator(new SecretSignatureConfiguration(jwtSecret))
          val token = generator.generate(profile)
          Ok(net.macolabo.sform2.views.html.jwt(configuration.get[String]("sform.oauth.redirectUrl"), token))
            .addingToSession("user_id" -> u.id.toString)
            .addingToSession("user_group" -> u.user_group.get)
            .addingToSession("user_role" -> u.role.getOrElse(""))
          // TODO 複数グループに属するユーザーの場合どうするか考える
        }).getOrElse(InternalServerError("sfrom.jwt.secret not found."))
      case None =>
        Forbidden(net.macolabo.sform2.views.html.redirect(configuration.get[String]("sform.oauth.loginFailedUrl")))
    }
  }

  private def checkUser(profile: UserProfile) = {
    profile.getAttribute("email") match {
      case s: String =>
        userService.retrieve(s)
      case _ => Future.successful(None)
    }
  }

  def checkSession: Action[AnyContent] = Secure("HeaderClient")  { request =>
    try {
      val profiles = getProfiles(controllerComponents)(request)
      val userGroup = request.session.get("user_group")
      userGroup.map(ug => Ok("Token is valid")).getOrElse(Unauthorized("Session Expired."))
    } catch {
      case e: NullPointerException => Unauthorized("Token is invalid or missing")
    }
  }
}
