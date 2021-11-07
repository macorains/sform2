package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import net.macolabo.sform2.services.Form.delete.FormDeleteResponseJson
import net.macolabo.sform2.services.Form.get.FormGetResponseJson
import net.macolabo.sform2.services.Form.list.FormListResponseJson
import net.macolabo.sform2.services.Form.update.{FormUpdateRequest, FormUpdateRequestJson, FormUpdateResponse, FormUpdateResponseJson}

import javax.inject._
import net.macolabo.sform2.services.Form.{FormDeleteFormResponseJson, FormGetFormResponseJson, FormGetListResponseJson, FormInsertFormRequest, FormInsertFormRequestJson, FormInsertFormResponse, FormInsertFormResponseJson, FormService, FormUpdateFormRequest, FormUpdateFormRequestJson, FormUpdateFormResponse, FormUpdateFormResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}

import scala.concurrent.{ExecutionContext, Future}

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
{

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = formService.getForm(request.identity, hashed_form_id)
    Future.successful(Ok(toJson(res)))

     */
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = formService.getList(request.identity)
    Future.successful(Ok(toJson(res)))

     */
  }

  /**
   * フォーム更新
   * @return
   */
  def save(): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    println(request.body.asJson.get.validate[FormUpdateRequest].toString)
    val res = request.body.asJson.flatMap(r =>
      r.validate[FormUpdateRequest].map(f => {
        formService.update(request.identity, f)
      }).asOpt)
    res match {
      case Some(s :FormUpdateResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }

     */
  }

  /**
   * フォーム作成
   * @return
   */
  def create(): Action[AnyContent] = Action.async { implicit request =>
    ???
    /*
    val res = request.body.asJson.flatMap(r =>
      r.validate[FormUpdateRequest].map(f => {
        formService.insert(request.identity, f)
      }).asOpt)
    res match {
      case Some(s :FormUpdateResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
     */
  }


  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = Action.async { implicit request =>
    ???
    //val res = formService.deleteForm(request.identity, hashed_form_id)
//    res.result match {
//      case Some(s: Int) => Future.successful(Ok(toJson(res)))
//      case None => Future.successful(BadRequest)
//    }
  //  Future.successful(Ok(toJson(res)))
  }


}
