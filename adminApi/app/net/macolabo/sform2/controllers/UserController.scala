package net.macolabo.sform2.controllers

import javax.inject._
import net.macolabo.sform2.domain.models.{RsResultSet, SessionInfo}
import net.macolabo.sform2.domain.models.daos.UserDAO
import net.macolabo.sform2.domain.services.User.{UserSaveRequest, UserSaveRequestJson, UserService}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class UserController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with UserSaveRequestJson
  with Pac4jUtil
{

  // GET /user
  def getList: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = userService.getList(sessionInfo)
        Ok(Json.toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  // adminロールの有無チェック用
  // GET /user/isadmin
  def isAdmin: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        Ok(s"""{"is_admin": ${sessionInfo.user_role == "admin"}}""")
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  // ユーザーの保存
  // POST /user
  def save: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[UserSaveRequest].fold(
              errors => BadRequest(JsError.toJson(errors)),
              value => {
                userService.save(value, sessionInfo)
                Ok
              }
            )
          case None => BadRequest("Missing JSON")
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  // ユーザーの削除
  // DELETE /user
  def delete(userId: String): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        userService.delete(userId, sessionInfo)
        Ok
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }
}
