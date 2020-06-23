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
import play.api.i18n.I18nSupport
import utils.auth.{DefaultEnv, WithProvider}
import com.mohiva.play.silhouette.api._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._
import models.daos.TransferConfig.BaseTransferConfigDAO
import net.macolabo.sform2.services.UserService

import scala.concurrent.{ExecutionContext, Future}

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

  case class TransferGetConfigRequest(transferName: String)

  case class TransferSaveConfigRequest(transferName: String, config: JsValue)
  object TransferSaveConfigRequest {
    implicit def jsonTransferSaveConfigRequestWrites: Writes[TransferSaveConfigRequest] = Json.writes[TransferSaveConfigRequest]
    implicit def jsonTransferSaveConfigRequestReads: Reads[TransferSaveConfigRequest] = Json.reads[TransferSaveConfigRequest]
  }

  // ?
  def getList: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.toJson("Not Implemented.")))
  }

  // GET /transfer/config/:transfer_name
  def getConfig(transfer_name: String): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val transferConfig = Class.forName("models.daos.TransferConfig." + transfer_name + "TransferConfigDAO")
      .getDeclaredConstructor(classOf[TransfersDAO], classOf[Configuration])
      .newInstance(transfersDAO, configuration).asInstanceOf[BaseTransferConfigDAO]
    val config = transferConfig.getTransferConfig
    val res = RsResultSet("OK", "OK", config)
    Future.successful(Ok(Json.toJson(res)))
  }

  // GET /transfer
  def getTransferList: Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    // ToDo グループによる制御必要
    val res = transfersDAO.getTransferListJson
    Future.successful(Ok(Json.toJson(res)))
  }

  // POST /transfer/config
  def saveConfig(): Action[AnyContent] = silhouette.SecuredAction(WithProvider[DefaultEnv#A](CredentialsProvider.ID, List("admin", "operator"))).async { implicit request =>
    val identity = request.identity
    val jsonBody: Option[JsValue] = request.body.asJson
    val res = jsonBody.map { json =>
      val data = (json \ "rcdata").as[JsValue]
      data.validate[TransferSaveConfigRequest] match {
        case s: JsSuccess[TransferSaveConfigRequest] =>
          val transferConfig = Class.forName("models.daos.TransferConfig." + s.value.transferName + "TransferConfigDAO")
            .getDeclaredConstructor(classOf[TransfersDAO], classOf[Configuration])
            .newInstance(transfersDAO, configuration).asInstanceOf[BaseTransferConfigDAO]
          val config = s.value.config
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
