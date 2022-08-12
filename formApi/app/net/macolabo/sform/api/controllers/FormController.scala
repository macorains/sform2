package net.macolabo.sform.api.controllers

import java.net.InetAddress

import javax.inject.Inject
import net.macolabo.sform.api.services.form.{FormService, FormValidationResultJson}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import scala.concurrent.ExecutionContext

class FormController @Inject()(
                                val controllerComponents: SecurityComponents,
                                formService: FormService,
                              )(
                                implicit
                                webJarsUtil: WebJarsUtil,
                                ex: ExecutionContext
                              ) extends Security[UserProfile]
  with I18nSupport
  with FormValidationResultJson
{
  val hostname :String = InetAddress.getLocalHost.getHostName

  /**
    * フォーム呼び出し
    * @return
    */
  def load: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    request.body.asJson match {
      case Some(f) =>
        formService.load(f, request.host).map(response => {
          Ok(Json.toJson(response))
        }).getOrElse(NotFound)
      case None => BadRequest
      case _ => BadRequest
    }
  }

  /**
    * フォームバリデート
    * FormId
    * FormData
    * @return
    */
  def validateForm: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    request.body.asJson match {
      case Some(f) =>
        formService.validate(f, request.host).map(response => {
          Ok(Json.toJson(response))
        }).getOrElse(NotFound)
      case None => BadRequest
      case _ => BadRequest
    }
  }

  /**
    * フォーム確認画面HTML取得
    * @return
    */
  def confirmForm: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    request.body.asJson match {
      case Some(f) =>
        formService.confirm(f, request.host).map(response => {
          Ok(Json.toJson(response))
        }).getOrElse(NotFound)
      case None => BadRequest
      case _ => BadRequest
    }
  }

  /**
    * フォーム登録
    * @return
    */
  def saveForm: Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    request.body.asJson match {
      case Some(f) =>
        formService.complete(f, request.host).map(response => {
          Ok(Json.toJson(response))
        }).getOrElse(NotFound)
      case None => BadRequest
      case _ => BadRequest
    }
  }

  /**
    * js取得
    * @param path ajax呼び出し元
    * @return
    */
  def getjs(path: String): Action[AnyContent] = Secure("HeaderClient") { implicit request =>
    Ok(net.macolabo.sform.api.views.js.forminput.render(path)).as("text/javascript")
  }
}
