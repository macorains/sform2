package net.macolabo.sform2.controllers

import net.macolabo.sform2.domain.models.SessionInfo
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
 * 管理画面 API 用の認証。
 *
 * ログイン状態は Play のセッション Cookie（HttpOnly / SameSite=Lax）だけで管理する。
 * セッションに user_id / user_group / user_role が揃っている場合のみアクションを実行し、
 * それ以外は 401 を返す（フロントはこれを受けてログイン画面へ遷移する）。
 *
 * Cookie で認証するため CSRF 対策が前提になる。Play の CSRFFilter
 * （bypassCorsTrustedOrigins = false）により、状態を変更するリクエストは
 * カスタムヘッダー（X-Requested-With）付きでなければ拒否される。
 * カスタムヘッダー付きのクロスオリジンリクエストは CORS の allowedOrigins でしか許可されない。
 */
trait SessionAuthentication { self: BaseController =>

  object Authenticated extends ActionBuilder[Request, AnyContent] {
    override def parser: BodyParser[AnyContent] = self.controllerComponents.parsers.defaultBodyParser

    override protected def executionContext: ExecutionContext = self.controllerComponents.executionContext

    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
      Try(SessionInfo(request.session)) match {
        case Success(_) => block(request)
        case Failure(e) => Future.successful(Results.Unauthorized(s"Session invalid. ${e.getMessage}"))
      }
  }
}
