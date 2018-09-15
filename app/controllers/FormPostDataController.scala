package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import play.api.Environment
import play.api.db.DBApi
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{ AbstractController, ControllerComponents }
import utils.auth.DefaultEnv

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

  def getPostData() = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(Json.obj("status" -> "OK", "message" -> ("Hello"))).as("application/json; charset=UTF-8"))
  }

}
