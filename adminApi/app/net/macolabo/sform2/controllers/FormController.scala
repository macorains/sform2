package net.macolabo.sform2.controllers

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

import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext

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
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = formService.getForm(userGroup, hashed_form_id)
    Ok(toJson(res))
  }

  /**
   * フォーム一覧取得
   * GET /form/list
   * @return フォームデータのリスト
   */
  def getList: Action[AnyContent] = Secure(clients = "HeaderClient") { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userGroup = getAttributeValue(profiles, "user_group")
    val res = formService.getList(userGroup)
    Ok(toJson(res))
  }

  /**
   * フォーム更新
   * @return
   */
  def save(): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = getAttributeValue(profiles, "user_group")

    println(request.body.asJson.get.validate[FormUpdateRequest].toString)

    val res = userId.flatMap(id => {
      request.body.asJson.flatMap(r =>
        r.validate[FormUpdateRequest].map(f => {
          formService.update(id, userGroup, f)
        }).asOpt)
    })

    res match {
      case Some(s :FormUpdateResponse) => Ok(toJson(s))
      case None => BadRequest
    }
  }

  /**
   * フォーム作成
   * @return
   */
  def create(): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
    val profiles = getProfiles(controllerComponents)(request)
    val userId = profiles.asScala.headOption.map(_.getId)
    val userGroup = getAttributeValue(profiles, "user_group")

    val res = userId.flatMap(id => {
      request.body.asJson.flatMap(r =>
        r.validate[FormUpdateRequest].map(f => {
          formService.insert(id, userGroup, f)
        }).asOpt)
    })

    res match {
      case Some(s :FormUpdateResponse) => Ok(toJson(s))
      case None => BadRequest
    }
  }

  /**
   * フォーム削除
   * DELETE /form/<form_id>
   * @param hashed_form_id フォームハッシュID
   * @return
   */
  def delete(hashed_form_id: String): Action[AnyContent] = Secure("HeaderClient")  { implicit request =>
  val profiles = getProfiles(controllerComponents)(request)
  val userGroup = getAttributeValue(profiles, "user_group")
    val res = formService.deleteForm(userGroup, hashed_form_id)
    Ok(toJson(res))
  }
}
