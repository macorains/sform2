package net.macolabo.sform2.controllers

import net.macolabo.sform2.domain.models.SessionInfo
import net.macolabo.sform2.domain.services.Form.delete.FormDeleteResponseJson
import net.macolabo.sform2.domain.services.Form.get.FormGetResponseJson
import net.macolabo.sform2.domain.services.Form.list.FormListResponseJson
import net.macolabo.sform2.domain.services.Form.update.{FormUpdateRequest, FormUpdateRequestJson, FormUpdateResponse, FormUpdateResponseJson}

import javax.inject._
import net.macolabo.sform2.domain.services.Form.FormService
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.JsError

import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class FormController @Inject() (
  val controllerComponents: SecurityComponents,
  formService: FormService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends Security[UserProfile]
  with I18nSupport
  with FormGetResponseJson
  with FormListResponseJson
  with FormUpdateRequestJson
  with FormUpdateResponseJson
  with FormDeleteResponseJson
  with Pac4jUtil
{

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = formService.getForm(hashed_form_id, sessionInfo)
        Ok(toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = Secure(clients = "HeaderClient") { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val res = formService.getList(sessionInfo)
        Ok(toJson(res))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }

  }

  /**
   * フォーム更新
   * @return
   */
  def save(): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[FormUpdateRequest].fold(
              errors => BadRequest(JsError.toJson(errors)),
              value => {
                val result = formService.update(value, sessionInfo)
                Ok(toJson(result))
              }
            )
          case None => BadRequest("Missing JSON")
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * フォーム作成
   * @return
   */
  def create(): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        request.body.asJson match {
          case Some(jsBody) =>
            jsBody.validate[FormUpdateRequest].fold(
              errors => BadRequest(JsError.toJson(errors)),
              value => {
                val result = formService.insert(value, sessionInfo)
                Ok(toJson(result))
              }
            )
          case None => BadRequest("Missing JSON")
        }
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }

  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    Try(SessionInfo(request.session)) match {
      case Success(sessionInfo) =>
        val result = formService.deleteForm(hashed_form_id, sessionInfo)
        Ok(toJson(result))
      case Failure(e) =>
        BadRequest(s"Session invalid. ${e.getMessage}")
    }
  }
}
