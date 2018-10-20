package controllers
import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Environment
import play.api.libs.mailer._
import models._
import models.daos.{FormsDAO, TransferTaskDAO, TransfersDAO, UserDAO}
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._
import models.connector.SalesforceConnector
import models.daos.TransferConfig.BaseTransferConfigDAO

import scala.concurrent.{ ExecutionContext, Future }


class TransferTaskController @Inject()(
                                env: Environment,
                                dbapi: DBApi,
                                components: ControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                credentialsProvider: CredentialsProvider,
                                socialProviderRegistry: SocialProviderRegistry,
                                configuration: Configuration
                              )
                              (
                                implicit
                                webJarsUtil: WebJarsUtil,
                                ex: ExecutionContext
                              ) extends AbstractController(components) with I18nSupport {

  def getList() = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }

  def getTransferTaskListByFormId() = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }


}
