package net.macolabo.sform2.controllers

import javax.inject._
import net.macolabo.sform2.domain.models.RsResultSet
import net.macolabo.sform2.domain.models.daos.UserDAO
import net.macolabo.sform2.domain.services.User.{UserSaveRequest, UserSaveRequestJson, UserService}
import org.webjars.play.WebJarsUtil
import play.api.{Environment, _}
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject() (
  val controllerComponents: SecurityComponents,
  userService: UserService,
  userDAO: UserDAO
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
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = RsResultSet("OK", "OK", userDAO.getList(userGroup))
    Ok(Json.toJson(res))
  }

  // adminロールの有無チェック用
  // GET /user/isadmin
  // TODO ロールチェックを考慮する
  def isAdmin: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val res = RsResultSet("OK", "OK", Json.toJson(""))
    Ok(Json.toJson(res))
  }

  // ユーザーの保存
  // POST /user
  def save: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    request.body.asJson.flatMap(bodyJson => {
      bodyJson.validate[UserSaveRequest].asOpt.map(userSaveRequest => {
        userService.save(userSaveRequest, userGroup)
        Ok
      })
    }).getOrElse(BadRequest)
  }

  // ユーザーの削除
  // DELETE /user
  def delete(userId: String): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    userService.delete(userId, userGroup)
    val res = RsResultSet("OK", "OK", Json.toJson(""))
    Ok(Json.toJson(res))
  }
}
