package net.macolabo.sform2.controllers

import net.macolabo.sform2.forms.SignUpForm
import net.macolabo.sform2.models.entity.user.User

import java.util.UUID
import javax.inject.Inject
import net.macolabo.sform2.models.user.{UserSignUpResult, UserSignUpResultJson}
import net.macolabo.sform2.services.AuthToken.AuthTokenService
import net.macolabo.sform2.services.User.UserService
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.webjars.play.{WebJarsUtil, routes}
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesProvider}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign Up` controller.
 *
 * @param controllerComponents   The Play controller components.
 * @param userService            The user service implementation.
 * @param authTokenService       The auth token service implementation.
 * @param mailerClient           The mailer client.
 * @param webJarsUtil            The webjar util.
 * @param ex                     The execution context.
 */
class SignUpController @Inject() (
  config: Configuration,
  val controllerComponents: SecurityComponents,
  userService: UserService,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile] with I18nSupport with UserSignUpResultJson {

   /**
   * 初期管理ユーザーの登録
   * （初期管理ユーザー登録後は使わない）
   * @return The result to display.
   */
  def submit: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    if(!userService.checkAdminExists) {
      SignUpForm.form.bindFromRequest().fold(
        form => Future.successful(BadRequest(s"${Messages("error.invalid.request")}")),
        data => {
          val message = s"""${Messages("activate.account.text1")} ${data.email} ${Messages("activate.account.text2")}"""
          val result = Ok(Json.toJson(UserSignUpResult(Ok.header.status, Option(message))))

          userService.retrieve(data.email).map {
            case Some(user) => // 該当するユーザーが存在する場合
              sendAlreadySignedUpMail(user, data)
              Conflict("")
            case None => // 該当するユーザーが存在しない場合
              createAdminUser(data)
              Ok("")
          }
        }
      )
    } else {
      Future.successful(BadRequest(s"${Messages("error.invalid.request")}"))
    }
    /*
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

              val url = net.macolabo.sform2.controllers.routes.SignInController.view.absoluteURL().replaceFirst("https*://[^/]+/", virtualHostName)
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
                //silhouette.env.eventBus.publish(SignUpEvent(user, request))
                result
              }
          }
        }
      )
    } else {
      Future.successful(BadRequest(s"${Messages("error.invalid.request")}"))
    }
     */
  }

  private def sendAlreadySignedUpMail(user: User, data: SignUpForm.Data)(implicit provider: MessagesProvider) = {
    //val url = net.macolabo.sform2.controllers.routes.SignInController.view.absoluteURL().replaceFirst("https*://[^/]+/", virtualHostName)
    val url = ""
    mailerClient.send(Email(
      subject = Messages("email.already.signed.up.subject"),
      from = Messages("email.from"),
      to = Seq(data.email),
      bodyText = Some(net.macolabo.sform2.views.txt.emails.alreadySignedUp(user, url).body),
      bodyHtml = Some(net.macolabo.sform2.views.html.emails.alreadySignedUp(user, url).body)
    ))
  }

  private def createAdminUser(data: SignUpForm.Data)(implicit provider: MessagesProvider) = {
    //val authInfo = passwordHasherRegistry.current.hash(data.password)
    val service = new DefaultPasswordService
    val password = service.encryptPassword(data.password)
    val user = User(
      user_id = UUID.randomUUID(),
      username = data.email,
      password = password,
      user_group = Some(data.group),
      role = Some("operator"),
      first_name = Some(data.firstName),
      last_name = Some(data.lastName),
      full_name = Some(data.firstName + " " + data.lastName),
      email = Some(data.email),
      avatar_url = None,
      activated = false,
      deletable = false
    )
    for {
      // avatar <- avatarService.retrieveURL(data.email)
      user <- userService.save(user.copy())
      // _ <- authInfoRepository.add(loginInfo, authInfo)
      //authToken <- authTokenService.create(user.userID)
    } yield {
      // val url = net.macolabo.sform2.controllers.routes.ActivateAccountController.activate(authToken.id).absoluteURL().replaceFirst("https*://[^/]+/", virtualHostName)
      val url = "hogehoge"
      mailerClient.send(Email(
        subject = Messages("email.sign.up.subject"),
        from = Messages("email.from"),
        to = Seq(data.email),
        bodyText = Some(net.macolabo.sform2.views.txt.emails.signUp(user, url).body),
        bodyHtml = Some(net.macolabo.sform2.views.html.emails.signUp(user, url).body)
      ))
    }
  }
}
