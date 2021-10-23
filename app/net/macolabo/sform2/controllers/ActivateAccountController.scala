package net.macolabo.sform2.controllers

import java.net.URLDecoder
import java.util.UUID
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import net.macolabo.sform2.services.AuthToken.AuthTokenService
import net.macolabo.sform2.services.User.UserService
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import net.macolabo.sform2.utils.auth.DefaultEnv
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Pac4jScalaTemplateHelper, Security, SecurityComponents}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * The `Activate Account` controller.
 *
 * @param config           Configuration
 * @param components       The Play controller components.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param ex               The execution context.
 */
class ActivateAccountController @Inject() (
  config: Configuration,
  components: ControllerComponents,
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient,
  val controllerComponents: SecurityComponents,
  implicit val pac4jTemplateHelper: Pac4jScalaTemplateHelper[UserProfile]
)(
  implicit
  ex: ExecutionContext
) extends Security[UserProfile] with I18nSupport {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  // TODO HTTPレスポンスのみ返すように変更すること (2019/03/20)
  def send(email: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(routes.SignInController.view).flashing("info" -> Messages("activation.email.sent", decodedEmail))

    val lastResult = userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.userID).map { authToken =>
          val virtualHostName = config.get[String]("silhouette.virtualHostName")
          val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL()
            .replaceFirst("https*://[^/]+/", virtualHostName)
          mailerClient.send(Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(net.macolabo.sform2.views.txt.emails.activateAccount(user, url).body),
            bodyHtml = Some(net.macolabo.sform2.views.html.emails.activateAccount(user, url).body)
          ))
          result
        }
      case None => Future.successful(result)
    }
    Await.result(lastResult, Duration.Inf)
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            Ok
          }
        case _ =>
          Future.successful(BadRequest)
      }
      case None =>
        Future.successful(BadRequest)
    }
    Ok("")
  }
}
