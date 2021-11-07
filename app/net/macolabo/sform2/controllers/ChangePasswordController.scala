package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import net.macolabo.sform2.utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext

/**
 * The `Change Password` controller.
 *
 * @param components             The Play controller components.
 * @param credentialsProvider    The credentials provider.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class ChangePasswordController @Inject() (
  components: ControllerComponents,
  authInfoRepository: AuthInfoRepository,
  passwordHasherRegistry: PasswordHasherRegistry
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  /**
   * Views the `Change Password` page.
   *
   * @return The result to display.
   */
//  def view = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))) {
//    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
//      // Ok(views.html.changePassword(ChangePasswordForm.form, request.identity))
//      Ok("")
//  }
  def view = ???

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit = ???
//  def submit = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async {
//    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
//      ChangePasswordForm.form.bindFromRequest.fold(
//        // form => Future.successful(BadRequest(views.html.changePassword(form, request.identity))),
//        form => Future.successful(BadRequest("")),
//        password => {
//          val (currentPassword, newPassword) = password
//          val credentials = Credentials(request.identity.email.getOrElse(""), currentPassword)
//          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
//            val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
//            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
//              Redirect(routes.ChangePasswordController.view()).flashing("success" -> Messages("password.changed"))
//            }
//          }.recover {
//            case _: ProviderException =>
//              Redirect(routes.ChangePasswordController.view()).flashing("error" -> Messages("current.password.invalid"))
//          }
//        }
//      )
//  }
}
