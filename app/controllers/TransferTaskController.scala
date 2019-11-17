package controllers
import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.Environment
import models._
import models.daos.TransferTaskDAO
import models.json.{ TransferTaskEntry, TransferTaskJson }
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.{ DefaultEnv, WithProvider }
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._

import scala.concurrent.{ ExecutionContext, Future }

class TransferTaskController @Inject() (
  env: Environment,
  dbapi: DBApi,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  transferTaskDAO: TransferTaskDAO
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport with TransferTaskJson {

  case class transferGetTaskRequest(formId: String)
  object transferGetTaskRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetTaskRequest] = Json.writes[transferGetTaskRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetTaskRequest] = Json.reads[transferGetTaskRequest]
  }

  // GET /transfertask
  def getList: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }
  // GET /transfertask/id
  def getTransferTask(id: Int): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin"))).async { implicit request =>
    val res = RsResultSet("OK", "OK", transferTaskDAO.getTransferTask(id))
    Future.successful(Ok(Json.toJson(res)))
  }
  // GET /transfertask/list/:form_id
  def getTransferTaskListByFormId(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val transferTaskList = transferTaskDAO.getTransferTaskListByFormId(hashed_form_id)
    val transferTaskEntryList = transferTaskList.map(t => {
      val config = if(t.config.isInstanceOf[JsValue]) t.config.as[JsObject] else Json.toJson("{}")
      TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, config, t.created, t.modified, 0)
    })
    val res = RsResultSet("OK", "OK", Json.toJson(transferTaskEntryList))
    Future.successful(Ok(Json.toJson(res)))
  }
}
