package controllers

import javax.inject._

import play.api._
import play.api.db.DBApi
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.i18n.I18nSupport
import play.api.Environment
import play.api.libs.mailer._
import models._
import models.daos.{ FormsDAO, TransferTaskDAO, TransfersDAO, UserDAO }
import models.services.UserService
import utils.auth.DefaultEnv
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util._
import org.webjars.play.WebJarsUtil
import com.mohiva.play.silhouette.impl.providers._
import models.connector.SalesforceConnector
import models.daos.TransferConfig.BaseTransferConfigDAO

import scala.concurrent.{ ExecutionContext, Future }

class RcController @Inject() (
  env: Environment,
  dbapi: DBApi,
  mailerClient: MailerClient,
  components: ControllerComponents,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock,
  formsDAO: FormsDAO,
  userDAO: UserDAO,
  transfersDAO: TransfersDAO,
  transferTaskDAO: TransferTaskDAO,
  salesforceConnector: SalesforceConnector
)(
  implicit
  webJarsUtil: WebJarsUtil,
  ex: ExecutionContext
) extends AbstractController(components) with I18nSupport {

  case class transferGetConfigRequest(transferName: String)
  object transferGetConfigRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetConfigRequest] = Json.writes[transferGetConfigRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetConfigRequest] = Json.reads[transferGetConfigRequest]
  }

  case class transferSaveConfigRequest(transferName: String, config: JsValue)
  object transferSaveConfigRequest {
    implicit def jsonTransferSaveConfigRequestWrites: Writes[transferSaveConfigRequest] = Json.writes[transferSaveConfigRequest]
    implicit def jsonTransferSaveConfigRequestReads: Reads[transferSaveConfigRequest] = Json.reads[transferSaveConfigRequest]
  }

  case class transferGetTaskRequest(formId: String)
  object transferGetTaskRequest {
    implicit def jsonTransferGetConfigRequestWrites: Writes[transferGetTaskRequest] = Json.writes[transferGetTaskRequest]
    implicit def jsonTransferGetConfigRequestReads: Reads[transferGetTaskRequest] = Json.reads[transferGetTaskRequest]
  }

  implicit val rcparamReads: Reads[RCParam] = (
    (__ \ "objtype").read[String] and
    (__ \ "action").read[String] and
    (__ \ "rcdata").read[JsValue]
  )(RCParam.apply _)

  // ToDo ユーザーグループごとにデータを出すようにする
  // ユーザーグループはrequest.identity.gruopでとれるはず。。。
  def receive() = silhouette.SecuredAction.async { request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    val identity = request.identity
    jsonBody.map { json =>
      val action = (json \ "action").as[String]
      val data = (json \ "rcdata").as[JsValue]
      val res = (json \ "objtype").as[String] match {
        case "Form" => {
          action match {
            case "list" => formsDAO.getList(identity)
            case "create" => {
              val formInsertResult = formsDAO.insert(data, identity)
              (formInsertResult.getDataset \ "id").as[String] match {
                case s: String if s != "failed" => {
                  transferTaskDAO.bulkSave(data, identity)
                  formInsertResult
                }
                case _ => formInsertResult
              }
            }
            case "delete" => formsDAO.delete(data)
            case "gethtml" => formsDAO.getHtml(data, request.host)
            case "validate" => formsDAO.validate(data, request.host)
            case "getdata" => formsDAO.getData(data)
            case _ => RsResultSet("NG", "NG", Json.parse("""{}"""))
          }
        }
        case "User" => {
          action match {
            case "list" => formsDAO.getList(identity)
          }
        }
        case "Transfer" => {
          action match {
            case "getConfig" => {
              val data = (json \ "rcdata").as[JsValue]
              data.validate[transferGetConfigRequest] match {
                case s: JsSuccess[transferGetConfigRequest] => {
                  val transferConfig = Class.forName("models.daos.TransferConfig." + s.get.transferName + "TransferConfigDAO")
                    .getDeclaredConstructor(classOf[TransfersDAO])
                    .newInstance(transfersDAO).asInstanceOf[BaseTransferConfigDAO]
                  val config = transferConfig.getTransferConfig
                  RsResultSet("OK", "OK", config)
                }
                case e: JsError => {
                  RsResultSet("NG", "NG", Json.parse("""{}"""))
                }
              }
            }
            case "getTransferList" => {
              RsResultSet("OK", "OK", transfersDAO.getTransferList())
            }

            case "saveConfig" => {
              print("***saveConfig***")
              val data = (json \ "rcdata").as[JsValue]
              data.validate[transferSaveConfigRequest] match {
                case s: JsSuccess[transferSaveConfigRequest] => {
                  print(s.get)

                  val transferConfig = Class.forName("models.daos.TransferConfig." + s.get.transferName + "TransferConfigDAO")
                    .getDeclaredConstructor(classOf[TransfersDAO])
                    .newInstance(transfersDAO).asInstanceOf[BaseTransferConfigDAO]
                  val config = s.get.config
                  val result = transferConfig.saveTransferConfig(config, identity)
                  RsResultSet("OK", "OK", result)
                }
                case e: JsError => {
                  RsResultSet("NG", "NG", Json.parse("""{}"""))
                }
              }

            }
            case _ => {
              RsResultSet("NG", "NG", Json.parse("""{}"""))
            }
          }
        }
        case "TransferTask" => {
          action match {
            case "getTransferTaskListByFormId" => {
              val data = (json \ "rcdata").as[JsValue]
              println("### getTransferTaskListByFormId ###")
              println(data)
              data.validate[transferGetTaskRequest] match {
                case s: JsSuccess[transferGetTaskRequest] => {
                  Logger.info("RcController.receive TransferTask.getTransferTaskListByFormId success.")
                  RsResultSet("OK", "OK", transferTaskDAO.getTransferTaskListByFormId(s.get.formId))
                }
                case e: JsError => {
                  Logger.error("RcController.receive TransferTask.getTransferTaskListByFormId failed.(1)")
                  RsResultSet("NG", "NG", Json.parse("""{}"""))
                }
              }
            }
          }
        }
      }
      Future.successful(Ok(Json.toJson(res)))
    }.getOrElse {
      Logger.error("RcController.receive TransferTask.getTransferTaskListByFormId failed.(2)")
      Future.successful(BadRequest("Bad!"))
    }
  }

  def t() = silhouette.UnsecuredAction.async { implicit request =>
    request.body.asJson match {
      case Some(f) => {
        println(f \ "rcdata")
        Future.successful(Ok("OK"))
      }
      case None => Future.successful(Ok(Json.toJson("error")))
      case _ => Future.successful(Ok(Json.toJson("a")))
    }
  }

  def checkUser(user: String) = {
    println(user)
  }

  def getjs(path: String) = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.js.sform.render(path)).as("text/javascript utf-8"))

  }

}