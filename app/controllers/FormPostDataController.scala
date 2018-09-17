package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.Environment
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{ AbstractController, ControllerComponents }
import utils.auth.DefaultEnv
import models.daos.PostdataDAO

import scala.concurrent.{ ExecutionContext, Future }

class FormPostDataController @Inject() (
  env: Environment,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  dbapi: DBApi)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  case class postDataResponse(total: String, page: String, records: String, rows: JsValue)
  object postDataResponse {
    implicit def jsonPostDataResponseWrites: Writes[postDataResponse] = Json.writes[postDataResponse]
    implicit def jsonPostDataResponseReads: Reads[postDataResponse] = Json.reads[postDataResponse]
  }

  def getPostData(hashed_form_id: String) = silhouette.SecuredAction.async { implicit request =>
    val postdataDao = new PostdataDAO()
    val d = hashed_form_id match {
      case s: String => { postdataDao.getPostdata(s).map(t => addPostDataId(t.postdata_id, t.postdata)) }
      case _ => List(Json.toJson(""))
    }
    val res = postDataResponse(d.length.toString, "1", "1", Json.toJson(d))
    Future.successful(Ok(Json.toJson(res)).as("application/json; charset=UTF-8"))
  }

  def addPostDataId(id: Int, postdata: JsValue) = {
    Json.toJson(postdata.as[JsObject] ++ Json.obj("id" -> id.toString))
  }

}
