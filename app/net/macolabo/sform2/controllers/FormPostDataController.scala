package net.macolabo.sform2.controllers

import javax.inject.Inject
import net.macolabo.sform2.services.FormPostData.FormPostDataService
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.ExecutionContext

class FormPostDataController @Inject() (
  val controllerComponents: SecurityComponents
                                       )(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext,
  formPostDataService: FormPostDataService
) extends Security[UserProfile] with I18nSupport {

  case class postDataResponse(total: String, page: String, records: String, cols: JsValue, rows: JsValue)
  object postDataResponse {
    implicit def jsonPostDataResponseWrites: Writes[postDataResponse] = Json.writes[postDataResponse]
    implicit def jsonPostDataResponseReads: Reads[postDataResponse] = Json.reads[postDataResponse]
  }

  def getPostData(hashed_form_id: String): Action[AnyContent] = Action.async { implicit request =>
    ???

    //Future.successful(Ok(formPostDataService.getData(hashed_form_id, request.identity)).as("application/json; charset=UTF-8"))
  }

  def addPostDataId(id: Int, postdata: JsValue): JsValue = {
    Json.toJson(postdata.as[JsObject] ++ Json.obj("id" -> id.toString))
  }
}
