package net.macolabo.sform2.controllers

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.{Body, Content, Destination, Message, SendEmailRequest}
import com.digitaltangible.playguard._

import javax.inject.Inject
import net.macolabo.sform2.domain.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Logger}
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.UserProfile
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.ws.WSClient

import scala.jdk.CollectionConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param controllerComponents   The Play controller components.
 * @param userService            The user service implementation.
 * @param configuration          The Play configuration.
 * @param cache                  The cache instance.
 * @param mailerClient The Mailer instance.
 * @param webJarsUtil            The webjar util.
 */
class SignInController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService,
  configuration: Configuration,
  cache: SyncCacheApi,
  mailerClient: MailerClient,
  ws: WSClient
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with Pac4jUtil
  with SignInVerificationRequestConverter
{

  val profilePrefix = "PR_"

  val verificationCodePrefix = "VC_"
  val authenticatorPrefix = "AU_"
  val loginEventPrefix = "LE_"
  val failureCheckPrefix = "FC_"
  val cacheExpireTime = 60

  val logger: Logger = Logger(this.getClass)

  /**
   * Handles the submitted form.
   * @return The result to display.
   */
  def submit: Action[AnyContent] = Secure("DirectFormClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    if(profiles.asScala.nonEmpty) {
      val profile = profiles.get(0)
      val authKey = profile.getAttribute("AuthKey").asInstanceOf[String]
      cache.set(profilePrefix + authKey, profiles)

      // Test SESの設定終わるまでこちらで
      val vc = profile.getAttribute("VerificationCode").asInstanceOf[String]
      println(vc)

      // sendAuthkeyMail(profile)
      Ok(s"""{"authkey" : "$authKey"}""")
    } else {
      Ok(s"""{"authkey" : ""}""")
    }
  }

  def oidcSignin: Action[AnyContent] = Secure("OidcClient") { implicit request =>
//    val profiles = getProfiles(controllerComponents)(request)
//    Ok(profiles.toString)
    Ok("fuga")
  }

  def testAction: Action[AnyContent] = Secure("OidcClient") { implicit request=>
    Ok("hoge")
  }

  private def sendAuthkeyMail(profile: UserProfile)(implicit request:  AuthenticatedRequest[AnyContent]) = {
    val verificationCode = profile.getAttribute("VerificationCode").asInstanceOf[String]
    val mailFrom = configuration.get[String]("sform.mail.systemMailAddress")
    val mailTo = profile.getAttribute("email").asInstanceOf[String]
    val mailSubject = Messages("sform.mail.subject.verification")
    val mailTextBody = Messages("sform.mail.body.text.verification", verificationCode)
    val mailHtmlBody = Messages("sform.mail.body.html.verification", verificationCode)

    val sesClient = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build()

    val sesRequest = new SendEmailRequest()
      .withDestination(new Destination().withToAddresses(mailTo))
      .withMessage(new Message()
        .withBody(new Body()
          .withHtml(new Content().withCharset("UTF-8").withData(mailHtmlBody))
          .withText(new Content().withCharset("UTF-8").withData(mailTextBody)))
        .withSubject(new Content().withCharset("UTF-8").withData(mailSubject)))
      .withSource(mailFrom)
    sesClient.sendEmail(sesRequest)
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

  def getJwt(): Action[AnyContent] = Action { implicit  request =>
    // TODO 必要なくなったら削除
    println("*** headers ***")
    request.headers.headers.foreach(h => {
      println(s"${h._1} -> ${h._2}\n")
    })

    // TODO GCP側にアクセスできないとダメっぽいので、開発時だけコマンド叩いて動くようにするなどの対策をする
    // val jwt = request.headers.headers.filter(_._1.equals("x-goog-iap-jwt-assertion")).map(_._2).head
    val jwt = request.headers.headers.filter(_._1.equals("x-goog-iap-jwt-assertion")).map(_._2).headOption.getOrElse("pogihapoigh890324thg8iq3pigjaoiwgenvaokreno")
    Ok(net.macolabo.sform2.views.html.jwt(configuration.get[String]("sform.oauth.redirectUrl"), jwt))
  }

  def callback(): Action[AnyContent] = Action { implicit request => {
    Ok("hogehoge")
  }}

//  private val httpErrorRateLimitFunction =
//    HttpErrorRateLimitFunction[Request](new RateLimiter(1, 1/7f, "test failure rate limit"), _ => Future.successful(BadRequest(Json.parse(s"""{"message":"LoginFailureLimitExceeded"}"""))))

  private def getCachedProfiles(authKey: String)  = {
    cache.get[java.util.List[UserProfile]](profilePrefix + authKey)
  }

}
