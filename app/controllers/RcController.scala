package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.i18n.I18nSupport
import play.api.Environment
import models._
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil

import scala.concurrent.{ ExecutionContext, Future }

class RcController @Inject() (
  env: Environment,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  case class transferGetConfigRequest(transferName: String)
  object transferGetConfigRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetConfigRequest] = Json.writes[transferGetConfigRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetConfigRequest] = Json.reads[transferGetConfigRequest]
  }

  case class transferSaveConfigRequest(transferName: String, config: JsValue)
  object transferSaveConfigRequest {
    implicit def jsonTransferSaveConfigRequestWrites: Writes[transferSaveConfigRequest] = Json.writes[transferSaveConfigRequest]
    implicit def jsonTransferSaveConfigRequestReads: Reads[transferSaveConfigRequest] = Json.reads[transferSaveConfigRequest]
  }

  case class transferGetTaskRequest(formId: String)
  object transferGetTaskRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetTaskRequest] = Json.writes[transferGetTaskRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetTaskRequest] = Json.reads[transferGetTaskRequest]
  }

  implicit val rcparamReads: Reads[RCParam] = (
    (__ \ "objtype").read[String] and
    (__ \ "action").read[String] and
    (__ \ "rcdata").read[JsValue]
  )(RCParam.apply _)


  def checkUser(user: String) = {
    println(user)
  }

  def getjs(path: String) = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.js.sform.render(path)).as("text/javascript utf-8"))

  }

}