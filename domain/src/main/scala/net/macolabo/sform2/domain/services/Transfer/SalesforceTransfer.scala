package net.macolabo.sform2.domain.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.services.Transfer.SalesforceTransfer.TransferTaskRequest
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, JsValue, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Salesforce Transfer
 * @param ws WSClient
 */
class SalesforceTransfer @Inject()(
  ws:WSClient
) extends BaseTransfer {
  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata) =>
      val taskBean = taskList.head

      getTransferConfigSalesforce(taskBean.transfer_config_id).map(tc => {
        val(sfUserName, sfPassword, sfClientId, sfClientSecret) = encodeSecrets(tc)
        loginToSalesforce(tc.api_url, sfClientId, sfClientSecret, sfUserName, sfPassword).map{
          case Some(apiToken) => postSalesforceObject(taskBean, postdata, apiToken, tc.api_url)
          case None => println("ほげー") // TODO 何か例外処理を実装する
        }
      })
      endTask(taskList, postdata, "")
  }

  /**
   * Salesforceへのログイン
   * @param apiUrl Salesforce REST API のURL
   * @param clientId client_id
   * @param clientSecret client_secret
   * @param username ユーザー名
   * @param password パスワード
   * @return BearerToken
   */
  private def loginToSalesforce(apiUrl: String, clientId: String, clientSecret: String, username: String, password: String): Future[Option[String]] = {
    val postdata = Map(
      "grant_type" -> "password",
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "username" -> username,
      "password" -> password
    )
    ws
      .url(apiUrl + "/services/oauth2/token")
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post(postdata)
      .map(res => Json.parse(res.body).validate[SalesforceLoginResponse].asOpt.map(res => res.access_token))
  }

  private def encodeSecrets(transferConfigSalesforce: TransferConfigSalesforce) = {
    ("a","b","c","d")
  }

  private def getTransferConfigSalesforce(transferConfigId: BigInt): Option[TransferConfigSalesforce] = {
    ???
  }

  private def postSalesforceObject(taskBean: TransferTaskBean, postdata: JsValue, apiToken: String, apiUrl: String) = {
    ws
      .url(apiUrl + "")
      .addHttpHeaders("Authorization" -> s"Bearer $apiToken")
      .addHttpHeaders("Content-Type" -> "application/json")
      .post(createSalesforcePostdata(taskBean, postdata))
  }

  private def createSalesforcePostdata(taskBean: TransferTaskBean, postdata: JsValue): JsValue = {

    ???
  }

  case class SalesforceLoginResponse(
    access_token: String,
    instance_url: String,
    id: String,
    token_type: String,
    issued_at: String,
    signature: String
  )

  implicit val salesforceLoginResponseFormat: Format[SalesforceLoginResponse] = (
    (JsPath \ "access_token").format[String] ~
      (JsPath \ "instance_url").format[String] ~
      (JsPath \ "id").format[String] ~
      (JsPath \ "token_type").format[String] ~
      (JsPath \ "issued_at").format[String] ~
      (JsPath \ "signature").format[String]
  )(SalesforceLoginResponse.apply, unlift(SalesforceLoginResponse.unapply))
}

object SalesforceTransfer {
  case class TransferTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue)
}

