package net.macolabo.sform.api.controllers

import net.macolabo.sform2.domain.services.AuthToken.AuthTokenService
import org.pac4j.core.profile.UserProfile
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request}
import play.api.{Configuration, Environment}

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.CollectionHasAsScala

/**
  * The `Sign In` controller.
  * @param env Environment
  * @param controllerComponents             The Play controller components.
  * @param authTokenService            The authToken service implementation.
  * @param configuration          The Play configuration.
  */
class SignInController @Inject() (
                                   env: Environment,
                                   val controllerComponents: SecurityComponents,
                                   authTokenService: AuthTokenService,
                                   configuration: Configuration
                                 )(
                                   implicit
                                   ex: ExecutionContext
                                 ) extends Security[UserProfile]
  with I18nSupport
  with Pac4jUtil
{

  // フォームで認証→JWT発行の流れでの認証処理
  // ApiTokenDAOを通じてd_apitokenの内容を参照している
  def auth: Action[AnyContent] = Secure("DirectFormClient") { implicit request: Request[AnyContent] =>
    val profiles = getProfiles(controllerComponents)(request)
    val token = if(profiles.asScala.nonEmpty) {
      val profile = profiles.get(0)
      // TODO Secretを環境変数からとるようにする 2025/08/03
      val generator = new JwtGenerator(new SecretSignatureConfiguration("12345678901234567890123456789012"))
      Some(generator.generate(profile))
    } else None
    Ok(Json.toJson(token)).withHeaders("X-Auth-Token" -> token.getOrElse(""))
  }
}
