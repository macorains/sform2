package net.macolabo.sform2.controllers

import com.digitaltangible.playguard._

import javax.inject.Inject
import net.macolabo.sform2.models.user.{VerificationRequestEntry, VerificationRequestJson}
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.{WebJarsUtil, routes}
import play.api.{Configuration, Logger}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.{JsNull, JsObject, JsString, Json}
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.core.util.CommonHelper
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param components             The Play controller components.
 * @param userService            The user service implementation.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 * @param webJarsUtil            The webjar util.
 */
class SignInController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService,
  configuration: Configuration,
  cache: SyncCacheApi,
  mailerClient: MailerClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with VerificationRequestJson
  with Pac4jUtil
{

  val profilePrefix = "PR_"

  val verificationCodePrefix = "VC_"
  val authenticatorPrefix = "AU_"
  val loginEventPrefix = "LE_"
  val failureCheckPrefix = "FC_"
  val cacheExpireTime = 60

  val logger: Logger = Logger(this.getClass())

  /**
   * Views the `Sign In` page.
   * @return The result to display.
   */
  def view: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(""))
  }

  /**
   * Handles the submitted form.
   * @return The result to display.
   */
  def submit: Action[AnyContent] = Secure("DirectFormClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val authKey = if(profiles.asScala.nonEmpty) profiles.get(0).getAttribute("AuthKey") else ""
    cache.set(profilePrefix + authKey, profiles)

    mailerClient.send(Email(
//      subject = Messages("email.verification.subject"),
//      from = Messages("email.from"),
      subject = "hogehoge",
      from = "mac.rainshrine@gmail.com",
      to = Seq("mac.rainshrine@gmail.com"),
      bodyText = Some("hogehoge"),
      bodyHtml = Some("<html><body>hogehoge</body></html>>")
    ))
    Ok(s"""{"auth_key" : "$authKey"}""")


    /*
    val generator = new JwtGenerator(new SecretSignatureConfiguration("12345678901234567890123456789012"))
    var token: String = ""
    if (CommonHelper.isNotEmpty(profiles)) {
      token = generator.generate(profiles.get(0))
    }

    println("%%% hhhhh %%%")

    // println(profiles.toString)
    val json = Json.parse(s"""{"token":"${token}"}""")
    Ok(json).as("application/json")

     */


    /*
    SignInForm.form.bindFromRequest().fold(
      form => Future.successful(BadRequest(Json.parse(s"""{"message":"${Messages("error.invalid.request")}"}"""))),
      data => {
        val credentials = Credentials(data.email + ":" + data.group, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          // val result = Redirect(routes.ApplicationController.index())
          userService.retrieve(loginInfo).flatMap {
            case Some(user) if !user.activated =>
              Future.successful(NotAcceptable(Json.parse(s"""{"message":"${Messages("error.not.activated")}"}""")))
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
                  bodyText = Some(net.macolabo.sform2.views.txt.emails.verification(verificationCode).body),
                  bodyHtml = Some(net.macolabo.sform2.views.html.emails.verification(verificationCode).body)
                ))
                Future.successful(Ok(Json.parse(s"""{"message":"OK","formToken":"$formToken"}""")))
              }
            case None => Future.failed(new IdentityNotFoundException(s"${Messages("error.user.not.found")}"))
          }
        }.recover {
          case e: ProviderException =>
            logger.error(e.toString)
            NotFound(Json.parse(s"""{"message":"${e.toString}"}"""))
        }
      }
    )
    */

  }

  /**
   * 認証コードのチェック
   * @return
   */
  def verification: Action[AnyContent] = Action { implicit request =>
    getCachedProfiles(request).map(profiles => {
      val generator = new JwtGenerator(new SecretSignatureConfiguration("12345678901234567890123456789012"))
      val token = generator.generate(profiles.get(0))
      Ok(token)
    }).getOrElse(NotFound(""))
  }

  private val httpErrorRateLimitFunction =
    HttpErrorRateLimitFunction[Request](new RateLimiter(1, 1/7f, "test failure rate limit"), _ => Future.successful(BadRequest(Json.parse(s"""{"message":"LoginFailureLimitExceeded"}"""))))

//  private def getProfiles(implicit request: RequestHeader)  = {
//    val webContext = new PlayWebContext(request)
//    val profileManager = new ProfileManager(webContext, controllerComponents.sessionStore)
//    profileManager.getProfiles()
//
//    //val profiles = profileManager.getProfiles()
//    //asScala(profiles).toList
//  }

  private def getCachedProfiles(implicit request: RequestHeader)  = {
    val webContext = new PlayWebContext(request)
    val authKey = webContext.getRequestParameter("authkey").orElse("")
    val verificationCode = webContext.getRequestParameter("verificationcode").orElse("")

    cache.get[java.util.List[UserProfile]](profilePrefix + authKey)

    // この辺りにauthkeyとverificationcodeの照合ロジック入れればOK
    // あとは生成したJWTトークンが使えるか・・・
//    val profile = cache.get("PR_" + key.get()).get().asInstanceOf[UserProfile]
//    List(profile).asJava

    //val profiles = profileManager.getProfiles()
    //asScala(profiles).toList
  }

}
