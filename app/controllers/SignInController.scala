package controllers

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import models.json.{VerificationRequestEntry, VerificationRequestJson}
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsNull, JsObject, JsString, JsValue, Json}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request, RequestHeader}
import play.cache.SyncCacheApi
import utils.auth.DefaultEnv

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param credentialsProvider    The credentials provider.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 * @param webJarsUtil            The webjar util.
 */
class SignInController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  configuration: Configuration,
  clock: Clock,
  cache: SyncCacheApi,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with VerificationRequestJson {

  val verificationCodePrefix = "VC_"
  val authenticatorPrefix = "AU_"
  val loginEventPrefix = "LE_"
  val cacheExpireTime = 60

  /**
   * Views the `Sign In` page.
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(""))
  }

  /**
   * Handles the submitted form.
   * @return The result to display.
   */
  def submit = silhouette.UnsecuredAction.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(Json.parse(s"""{"message":"${Messages("error.invalid.request")}"}"""))),
      data => {
        val credentials = Credentials(data.email + ":" + data.group, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.ApplicationController.index())
          userService.retrieve(loginInfo).flatMap {
            case Some(user) if !user.activated =>
              Future.successful(BadRequest(Json.parse(s"""{"message":"${Messages("error.not.activated")}"}""")))
            case Some(user) =>
              val c = configuration.underlying
              silhouette.env.authenticatorService.create(loginInfo).map {
                case authenticator if data.rememberMe =>
                  authenticator.copy(
                    expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                    idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                    customClaims = Some(JsObject(Seq("role" -> JsString("operator")))) //ロールを設定する
                  )
                case authenticator => authenticator
              }.flatMap { authenticator =>
                val verificationCode = (Math.floor(Math.random * 899999).toInt + 100000).toString
                val formToken = UUID.randomUUID().toString
                cache.set(verificationCodePrefix + formToken, verificationCode, cacheExpireTime)
                cache.set(authenticatorPrefix + formToken, authenticator, cacheExpireTime)
                cache.set(loginEventPrefix + formToken, LoginEvent(user, request), cacheExpireTime)

                mailerClient.send(Email(
                  subject = Messages("email.verification.subject"),
                  from = Messages("email.from"),
                  to = Seq(data.email),
                  bodyText = Some(views.txt.emails.verification(verificationCode).body),
                  bodyHtml = Some(views.html.emails.verification(verificationCode).body)
                ))
                Future.successful(Ok(Json.parse(s"""{"message":"OK","formToken":"$formToken"}""")))
              }
            case None => Future.failed(new IdentityNotFoundException(s"${Messages("error.user.not.found")}"))
          }
        }.recover {
          case e: ProviderException =>
            BadRequest(Json.parse(s"""{"message":"${e.toString}"}"""))
        }
      }
    )
  }

  /**
   * 認証コードのチェック
   * @return
   */
  def verification: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    request.body.asJson.getOrElse(JsNull).validate[VerificationRequestEntry].asOpt match {
      case Some(verificationRequest) => {
        val formToken = verificationRequest.formToken
        val verificationCode = cache.getOptional[String](verificationCodePrefix + formToken)
        val loginEvent = cache.getOptional[LoginEvent[Identity]](loginEventPrefix + formToken)
        val authentication = cache.getOptional[JWTAuthenticator](authenticatorPrefix + formToken)

        if(verificationCode.isPresent && verificationCode.get.equals(verificationRequest.verificationCode) && loginEvent.isPresent && authentication.isPresent){
          silhouette.env.eventBus.publish(loginEvent.get())
          silhouette.env.authenticatorService.init(authentication.get()).flatMap { v =>
            silhouette.env.authenticatorService.embed(v, Ok)
          }
        } else {
          Future.successful(BadRequest(Json.parse(s"""{"message":"${Messages("error.invalid.request")}"}""")))
        }
      }
      case None => Future.successful(BadRequest(Json.parse(s"""{"message":"NG!"}"}""")))
    }
  }
}
