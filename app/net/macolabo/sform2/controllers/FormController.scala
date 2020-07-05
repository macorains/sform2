package net.macolabo.sform2.controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import javax.inject._
import net.macolabo.sform2.models.RsResultSet
import net.macolabo.sform2.models.daos.{FormsDAO, TransferTaskDAO}
import net.macolabo.sform2.services.Form.{FormGetFormResponseJson, FormGetListResponseJson, FormService, FormUpdateFormRequest, FormUpdateFormRequestJson, FormUpdateFormResponse, FormUpdateFormResponseJson}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsResult, JsValue}
import play.api.libs.json.Json._
import play.api.mvc._
import net.macolabo.sform2.utils.auth.{DefaultEnv, WithProvider}

import scala.concurrent.{ExecutionContext, Future}

class FormController @Inject() (
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  formsDAO: FormsDAO,
  transferTaskDAO: TransferTaskDAO,
  formService: FormService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components)
  with I18nSupport
  with FormGetFormResponseJson
  with FormGetListResponseJson
  with FormUpdateFormRequestJson
  with FormUpdateFormResponseJson
{

  /**
   * フォームデータ取得
   * @return フォームデータ
   */
  def get(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.getForm(request.identity, hashed_form_id)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = formService.getList(request.identity)
    Future.successful(Ok(toJson(res)))
  }

  /**
   * フォーム登録／更新
   * @return
   */
  def save(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val res = request.body.asJson.flatMap(r =>
      r.validate[FormUpdateFormRequest].map(f => {
        formService.update(request.identity, f)
      }).asOpt)

    res match {
      case Some(s :FormUpdateFormResponse) => Future.successful(Ok(toJson(s)))
      case None => Future.successful(BadRequest)
    }
  }


  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    Future.successful(Ok(toJson(formsDAO.delete(hashed_form_id))))
  }

  /**
   * フォームHTML取得
   * GET /form/html/<form_id>
   * @return
   */
  def getHtml: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      formsDAO.getHtml(data, request.host)
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }

  /**
   * フォームバリデート
   * POST /form/validate
   * @return
   */
  def validate(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      formsDAO.validate(data, request.host)
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }
}
