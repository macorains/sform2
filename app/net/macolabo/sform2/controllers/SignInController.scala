package net.macolabo.sform2.controllers

import com.digitaltangible.playguard._

import javax.inject.Inject
import net.macolabo.sform2.models.user.VerificationRequestJson
import net.macolabo.sform2.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Logger}
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsString, JsSuccess, Json}
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.UserProfile
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.jdk.CollectionConverters._
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
  with SignInVerificationRequestConverter
{

  val profilePrefix = "PR_"

  val verificationCodePrefix = "VC_"
  val authenticatorPrefix = "AU_"
  val loginEventPrefix = "LE_"
  val failureCheckPrefix = "FC_"
  val cacheExpireTime = 60

  val logger: Logger = Logger(this.getClass())

  /**
   * Handles the submitted form.
   * @return The result to display.
   */
  def submit: Action[AnyContent] = Secure("DirectFormClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val authKey = if(profiles.asScala.nonEmpty) profiles.get(0).getAttribute("AuthKey").asInstanceOf[String] else ""
    val verificationCode = if(profiles.asScala.nonEmpty) profiles.get(0).getAttribute("VerificationCode").asInstanceOf[String] else ""
    cache.set(profilePrefix + authKey, profiles)
    //cache.set(verificationCodePrefix + authKey, verificationCode)

    mailerClient.send(Email(
//      subject = Messages("email.verification.subject"),
//      from = Messages("email.from"),
      subject = "hogehoge",
      from = "mac.rainshrine@gmail.com",
      to = Seq("mac.rainshrine@gmail.com"),
      bodyText = Some(verificationCode),
      bodyHtml = Some("<html><body>hogehoge</body></html>>")
    ))
    Ok(s"""{"authkey" : "$authKey"}""")
  }

  /**
   * 認証コードのチェック
   * @return
   */
  def verification: Action[AnyContent] = Action { implicit request =>
    request.body.asJson.flatMap(bodyJson => {
      bodyJson.validate[SignInVerificationRequest].map(verificationRequest => {
        val authKey = verificationRequest.authkey
        val verificationCode = verificationRequest.verification_code

        getCachedProfiles(authKey).map(profiles => {
          val profile = profiles.get(0)
          val vc = profile.getAttribute("VerificationCode").asInstanceOf[String]

          if(vc.nonEmpty && vc.equals(verificationCode)) {
            val generator = new JwtGenerator(new SecretSignatureConfiguration("12345678901234567890123456789012"))
            val token = generator.generate(profile)
            Ok(token).withHeaders("X-Auth-Token" -> token)
          } else NotFound("")
        }).getOrElse(NotFound(""))
      }).asOpt
    }).getOrElse(BadRequest(""))
  }

  private val httpErrorRateLimitFunction =
    HttpErrorRateLimitFunction[Request](new RateLimiter(1, 1/7f, "test failure rate limit"), _ => Future.successful(BadRequest(Json.parse(s"""{"message":"LoginFailureLimitExceeded"}"""))))

  private def getCachedProfiles(authKey: String)  = {
    cache.get[java.util.List[UserProfile]](profilePrefix + authKey)

    // この辺りにauthkeyとverificationcodeの照合ロジック入れればOK
    // あとは生成したJWTトークンが使えるか・・・
//    val profile = cache.get("PR_" + key.get()).get().asInstanceOf[UserProfile]
//    List(profile).asJava

    //val profiles = profileManager.getProfiles()
    //asScala(profiles).toList
  }

}
