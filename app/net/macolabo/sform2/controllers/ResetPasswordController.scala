package net.macolabo.sform2.controllers

import java.util.UUID
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry

import javax.inject.Inject
import net.macolabo.sform2.services.AuthToken.AuthTokenService
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc._
import net.macolabo.sform2.utils.auth.DefaultEnv
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.ExecutionContext

/**
 * The `Reset Password` controller.
 *
 * @param components             The Play controller components.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param authTokenService       The auth token service implementation.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class ResetPasswordController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService,
  passwordHasherRegistry: PasswordHasherRegistry,
  authTokenService: AuthTokenService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile] with I18nSupport {

  /**
   * Views the `Reset Password` page.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def view(token: UUID): Nothing = ???
//  def view(token: UUID): Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    authTokenService.validate(token).map {
//      // case Some(_) => Ok(views.html.resetPassword(ResetPasswordForm.form, token))
//      case Some(_) => Ok("")
//      case None => Redirect(routes.SignInController.view()).flashing("error" -> Messages("invalid.reset.link"))
//    }
//  }

  /**
   * Resets the password.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def submit(token: UUID): Nothing = ???
//  def submit(token: UUID): Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    authTokenService.validate(token).flatMap {
//      case Some(authToken) =>
//        ResetPasswordForm.form.bindFromRequest.fold(
//          // form => Future.successful(BadRequest(views.html.resetPassword(form, token))),
//          form => Future.successful(BadRequest("")),
//          password => userService.retrieve(authToken.userID).flatMap {
//            case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
//              val passwordInfo = passwordHasherRegistry.current.hash(password)
//              authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
//                Redirect(routes.SignInController.view()).flashing("success" -> Messages("password.reset"))
//              }
//            case _ => Future.successful(Redirect(routes.SignInController.view()).flashing("error" -> Messages("invalid.reset.link")))
//          }
//        )
//      case None => Future.successful(Redirect(routes.SignInController.view()).flashing("error" -> Messages("invalid.reset.link")))
//    }
//  }
}
