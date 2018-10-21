package controllers
import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.Environment
import models._
import models.daos.TransferTaskDAO
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._

import scala.concurrent.{ ExecutionContext, Future }


class TransferTaskController @Inject()(
                                env: Environment,
                                dbapi: DBApi,
                                components: ControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                credentialsProvider: CredentialsProvider,
                                socialProviderRegistry: SocialProviderRegistry,
                                configuration: Configuration,
                                transferTaskDAO: TransferTaskDAO
                              )
                              (
                                implicit
                                webJarsUtil: WebJarsUtil,
                                ex: ExecutionContext
                              ) extends AbstractController(components) with I18nSupport {

  case class transferGetTaskRequest(formId: String)
  object transferGetTaskRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetTaskRequest] = Json.writes[transferGetTaskRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetTaskRequest] = Json.reads[transferGetTaskRequest]
  }

  def getList() = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }

  def getTransferTaskListByFormId() = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      data.validate[transferGetTaskRequest] match {
        case s: JsSuccess[transferGetTaskRequest] => {
          Logger.info("RcController.receive TransferTask.getTransferTaskListByFormId success.")
          RsResultSet("OK", "OK", transferTaskDAO.getTransferTaskListByFormId(s.get.formId))
        }
        case e: JsError => {
          Logger.error("RcController.receive TransferTask.getTransferTaskListByFormId failed.(1)")
          RsResultSet("NG", "NG", Json.parse("""{}"""))
        }
      }
    }.getOrElse {
      None
    }
    res match{
        case r:RsResultSet => Future.successful(Ok(Json.toJson(r)))
        case _ => Future.successful(BadRequest("Bad!"))
    }
  }
}
