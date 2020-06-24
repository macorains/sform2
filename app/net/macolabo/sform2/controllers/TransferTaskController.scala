package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import models._
import net.macolabo.sform2.models.RsResultSet
import net.macolabo.sform2.models.daos.TransferTaskDAO
import net.macolabo.sform2.models.json.{TransferTaskEntry, TransferTaskJson}
import net.macolabo.sform2.services.UserService
import org.webjars.play.WebJarsUtil
import play.api.{Environment, _}
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

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
    val transferTaskEntryList = transferTaskDAO.getTransferTaskListByFormId(hashed_form_id)
      .map(
        t => { TransferTaskEntry(t.id, t.transfer_type_id, t.name, t.status, Json.parse(t.config), t.created, t.modified, 0) }
      )
    val res = RsResultSet("OK", "OK", Json.toJson(transferTaskEntryList))
    Future.successful(Ok(Json.toJson(res)))
  }
}
