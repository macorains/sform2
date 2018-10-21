package controllers

import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.Environment
import models.daos.UserDAO
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._

import scala.concurrent.{ ExecutionContext, Future }


class UserController @Inject()(
                                env: Environment,
                                dbapi: DBApi,
                                components: ControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                credentialsProvider: CredentialsProvider,
                                socialProviderRegistry: SocialProviderRegistry,
                                configuration: Configuration,
                                userDAO: UserDAO
                              )
                              (
                                implicit
                                webJarsUtil: WebJarsUtil,
                                ex: ExecutionContext
                              ) extends AbstractController(components) with I18nSupport {

  // ToDo グループによる制御必要
  def getList() = silhouette.SecuredAction.async { implicit request =>
    val res = userDAO.getList();
    Future.successful(Ok(Json.toJson(res)))
  }
}
