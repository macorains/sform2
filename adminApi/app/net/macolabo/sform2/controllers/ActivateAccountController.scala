package net.macolabo.sform2.controllers

import java.util.UUID

import javax.inject.Inject
import net.macolabo.sform2.domain.services.AuthToken.AuthTokenService
import net.macolabo.sform2.domain.services.User.UserService
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.mailer.MailerClient
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Pac4jScalaTemplateHelper, Security, SecurityComponents}

import scala.concurrent.{ExecutionContext, Future}

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
  def send(email: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    // emailからユーザー検索

    // 対象が未アクティベートならアクティベートコードを生成してキャッシュに入れる

    // アクティベートメールを送る


    // Silhouetteを抜くために一旦蓋をする (2021/11/14)
    /*
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(routes.SignInController.view).flashing("info" -> Messages("activation.email.sent", decodedEmail))

    userService.retrieve(loginInfo).flatMap {
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
    */
    Future.successful(Ok)
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.user_id).flatMap {
        case Some(user) =>
          userService.save(user.copy(activated = true)).map { _ =>
            Ok
          }
        case _ =>
          Future.successful(NotFound)
      }
      case None =>
        Future.successful(BadRequest)
    }
  }
}
