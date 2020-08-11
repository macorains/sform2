package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import javax.inject.Inject
import net.macolabo.sform2.services.AuthToken.AuthTokenService
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.mailer.MailerClient
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import net.macolabo.sform2.utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Forgot Password` controller.
 *
 * @param components       The Play controller components.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param webJarsUtil      The webjar util.
 * @param ex               The execution context.
 */
class ForgotPasswordController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  /**
   * Views the `Forgot Password` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    // Future.successful(Ok(views.html.forgotPassword(ForgotPasswordForm.form)))
    Future.successful(Ok(""))
  }

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */

  def submit: Nothing = ???
//  def submit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    ForgotPasswordForm.form.bindFromRequest.fold(
//      // form => Future.successful(BadRequest(views.html.forgotPassword(form))),
//      form => Future.successful(BadRequest("")),
//      email => {
//        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
//        val result = Redirect(routes.SignInController.view()).flashing("info" -> Messages("reset.email.sent"))
//        userService.retrieve(loginInfo).flatMap {
//          case Some(user) if user.email.isDefined =>
//            authTokenService.create(user.userID).map { authToken =>
//              val url = routes.ResetPasswordController.view(authToken.id).absoluteURL()
//
//              mailerClient.send(Email(
//                subject = Messages("email.reset.password.subject"),
//                from = Messages("email.from"),
//                to = Seq(email),
//                bodyText = Some(views.txt.emails.resetPassword(user, url).body),
//                bodyHtml = Some(views.html.emails.resetPassword(user, url).body)
//              ))
//              result
//            }
//          case None => Future.successful(result)
//        }
//      }
//    )
//  }
}
