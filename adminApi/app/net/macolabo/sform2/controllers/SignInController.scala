package net.macolabo.sform2.controllers

import javax.inject.Inject
import net.macolabo.sform2.domain.services.User.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Configuration, Logger}
import play.api.i18n.I18nSupport
import play.api.libs.mailer._
import play.api.mvc._
import play.api.cache.SyncCacheApi
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.context.PlayContextFactory
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import net.macolabo.sform2.domain.models.SessionInfo

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

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
  ws: WSClient,
  playContextFactory: PlayContextFactory
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with Pac4jUtil
  with SessionAuthentication
{

  val logger: Logger = Logger(this.getClass)

  /**
   * Handles the submitted form.
   * @return The result to display.
   */

  /**
   * Google OIDC ログイン完了後の処理
   * GET /oidc
   * 登録済みユーザーならセッション Cookie を発行して管理画面へリダイレクトする。
   * セッションは user_id / user_group / user_role だけで作り直し、pac4j が保存した
   * OIDC プロファイルは破棄する（Cookie サイズを抑えるため。ログイン後は使わない）。
   */
  def oidcSignin: Action[AnyContent] = Secure("OidcClient").async { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val profile = profiles.get(0)
    checkUser(profile).map {
      case Some(u) =>
        Redirect(configuration.get[String]("sform.oauth.redirectUrl"))
          .withSession(
            "user_id" -> u.id.toString,
            "user_group" -> u.user_group.get,
            "user_role" -> u.role.getOrElse("")
          )
        // TODO 複数グループに属するユーザーの場合どうするか考える
      case None =>
        // 未登録ユーザー。別アカウントで再ログインできるようセッションも破棄する
        Forbidden(net.macolabo.sform2.views.html.redirect(configuration.get[String]("sform.oauth.loginFailedUrl")))
          .withNewSession
    }
  }

  /**
   * ログアウト
   * GET /logout
   * セッション Cookie を破棄してログイン画面へリダイレクトする。
   */
  def logout: Action[AnyContent] = Action {
    Redirect(configuration.get[String]("sform.oauth.loginUrl")).withNewSession
  }

  private def checkUser(profile: UserProfile) = {
    profile.getAttribute("email") match {
      case s: String =>
        userService.retrieve(s)
      case _ => Future.successful(None)
    }
  }

  /**
   * ログイン中ユーザー情報の取得（フロントのログイン状態の唯一の取得元）
   * GET /me
   * セッション Cookie が有効な場合のみ200、それ以外は401を返す。
   */
  def me: Action[AnyContent] = Authenticated { request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        Ok(Json.obj(
          "user_id" -> sessionInfo.user_id,
          "user_group" -> sessionInfo.user_group,
          "user_role" -> sessionInfo.user_role,
          "is_admin" -> (sessionInfo.user_role == "admin")
        ))
      case Failure(e) =>
        Unauthorized(s"Session invalid. ${e.getMessage}")
    }
  }
}
