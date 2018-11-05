package controllers
import javax.inject._
import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.Environment
import models._
import models.daos.TransfersDAO
import models.services.UserService
import play.api.i18n.I18nSupport
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._
import models.daos.TransferConfig.BaseTransferConfigDAO

import scala.concurrent.{ ExecutionContext, Future }

class TransferController @Inject() (
  env: Environment,
  dbapi: DBApi,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  transfersDAO: TransfersDAO
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  case class transferGetConfigRequest(transferName: String)
  /*
  object transferGetConfigRequest {
    //implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetConfigRequest] = Json.writes[transferGetConfigRequest]
    //implicit def jsonTransferGetConfigRequestReads: Reads[transferGetConfigRequest] = Json.reads[transferGetConfigRequest]

  }
  */
  /*
  implicit val jsonTransferGetConfigRequestWrites: Writes[transferGetConfigRequest] = (
    (__ \ "transferName").write[String]
    )(unlift(transferGetConfigRequest.unapply))
  implicit val  jsonTransferGetConfigRequestReads: Reads[transferGetConfigRequest] = (
    (__ \ "transferName").read[String]
    )(transferGetConfigRequest.apply _)
  */

  case class transferSaveConfigRequest(transferName: String, config: JsValue)
  object transferSaveConfigRequest {
    implicit def jsonTransferSaveConfigRequestWrites: Writes[transferSaveConfigRequest] = Json.writes[transferSaveConfigRequest]
    implicit def jsonTransferSaveConfigRequestReads: Reads[transferSaveConfigRequest] = Json.reads[transferSaveConfigRequest]
  }

  // ?
  def getList: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }

  // GET /transfer/config/:transfer_name
  def getConfig(transfer_name: String): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val transferConfig = Class.forName("models.daos.TransferConfig." + transfer_name + "TransferConfigDAO")
      .getDeclaredConstructor(classOf[TransfersDAO])
      .newInstance(transfersDAO).asInstanceOf[BaseTransferConfigDAO]
    val config = transferConfig.getTransferConfig
    val res = RsResultSet("OK", "OK", config)
    Future.successful(Ok(Json.toJson(res)))
  }

  /*
  def getConfig: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      data.validate[transferGetConfigRequest] match {
        case s: JsSuccess[transferGetConfigRequest] =>
          val transferConfig = Class.forName("models.daos.TransferConfig." + s.get.transferName + "TransferConfigDAO")
            .getDeclaredConstructor(classOf[TransfersDAO])
            .newInstance(transfersDAO).asInstanceOf[BaseTransferConfigDAO]
          val config = transferConfig.getTransferConfig
          RsResultSet("OK", "OK", config)
        case _: JsError =>
          RsResultSet("NG", "NG", Json.parse("""{}"""))
      }
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(Json.toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }
  }
  */

  // GET /transfer
  def getTransferList: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    // ToDo グループによる制御必要
    val res = transfersDAO.getTransferList
    Future.successful(Ok(Json.toJson(res)))
  }

  // POST /transfer/config
  def saveConfig(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val identity = request.identity
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      data.validate[transferSaveConfigRequest] match {
        case s: JsSuccess[transferSaveConfigRequest] =>
          val transferConfig = Class.forName("models.daos.TransferConfig." + s.get.transferName + "TransferConfigDAO")
            .getDeclaredConstructor(classOf[TransfersDAO])
            .newInstance(transfersDAO).asInstanceOf[BaseTransferConfigDAO]
          val config = s.get.config
          val result = transferConfig.saveTransferConfig(config, identity)
          RsResultSet("OK", "OK", result)
        case _: JsError =>
          RsResultSet("NG", "NG", Json.parse("""{}"""))
      }
    }.getOrElse {
      None
    }
    res match {
      case r: RsResultSet => Future.successful(Ok(Json.toJson(r)))
      case _ => Future.successful(BadRequest("Bad!"))
    }

  }
}
