package net.macolabo.sform2.controllers

import java.util.UUID
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers._

import javax.inject.Inject
import net.macolabo.sform2.forms.SignUpForm
import net.macolabo.sform2.models
import net.macolabo.sform2.models.entity.user.User
import net.macolabo.sform2.models.user.{UserSignUpResult, UserSignUpResultJson}
import net.macolabo.sform2.services.AuthToken.AuthTokenService
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.{WebJarsUtil, routes}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc._
import net.macolabo.sform2.utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign Up` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class SignUpController @Inject() (
  config: Configuration,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  authTokenService: AuthTokenService,
  avatarService: AvatarService,
  passwordHasherRegistry: PasswordHasherRegistry,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with UserSignUpResultJson {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    // Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    Future.successful(Ok(""))
  }

  /**
   * 初期管理ユーザーの登録
   * （初期管理ユーザー登録後は使わない）
   * @return The result to display.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    if(!userService.checkAdminExists) {
      val virtualHostName = config.get[String]("silhouette.virtualHostName")
      SignUpForm.form.bindFromRequest().fold(
        form => Future.successful(BadRequest(s"${Messages("error.invalid.request")}")),
        data => {
          val message = s"""${Messages("activate.account.text1")} ${data.email} ${Messages("activate.account.text2")}"""
          val result = Ok(Json.toJson(UserSignUpResult(Ok.header.status, Option(message))))
          val loginInfo = LoginInfo(CredentialsProvider.ID, s"""${data.email}:${data.group}""")
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>

              val url = net.macolabo.sform2.controllers.routes.SignInController.view().absoluteURL().replaceFirst("https*://[^/]+/", virtualHostName)
              mailerClient.send(Email(
                subject = Messages("email.already.signed.up.subject"),
                from = Messages("email.from"),
                to = Seq(data.email),
                bodyText = Some(net.macolabo.sform2.views.txt.emails.alreadySignedUp(user, url).body),
                bodyHtml = Some(net.macolabo.sform2.views.html.emails.alreadySignedUp(user, url).body)
              ))
              Future.successful(result)
            case None =>
              val authInfo = passwordHasherRegistry.current.hash(data.password)
              val user = User(
                userID = UUID.randomUUID(),
                loginInfo = loginInfo,
                group = Some(data.group),
                role = Some("operator"),
                firstName = Some(data.firstName),
                lastName = Some(data.lastName),
                fullName = Some(data.firstName + " " + data.lastName),
                email = Some(data.email),
                avatarURL = None,
                activated = false,
                deletable = false
              )
              for {
                avatar <- avatarService.retrieveURL(data.email)
                user <- userService.save(user.copy(avatarURL = avatar))
                _ <- authInfoRepository.add(loginInfo, authInfo)
                authToken <- authTokenService.create(user.userID)
              } yield {
                val url = net.macolabo.sform2.controllers.routes.ActivateAccountController.activate(authToken.id).absoluteURL().replaceFirst("https*://[^/]+/", virtualHostName)
                mailerClient.send(Email(
                  subject = Messages("email.sign.up.subject"),
                  from = Messages("email.from"),
                  to = Seq(data.email),
                  bodyText = Some(net.macolabo.sform2.views.txt.emails.signUp(user, url).body),
                  bodyHtml = Some(net.macolabo.sform2.views.html.emails.signUp(user, url).body)
                ))
                silhouette.env.eventBus.publish(SignUpEvent(user, request))
                result
              }
          }
        }
      )
    } else {
      Future.successful(BadRequest(s"${Messages("error.invalid.request")}"))
    }
  }
}
