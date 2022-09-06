package net.macolabo.sform2.domain.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.TransferConfigSalesforceDAO
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.services.Transfer.SalesforceTransfer.TransferTaskRequest
import net.macolabo.sform2.domain.utils.Crypto
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, JsSuccess, JsValue, Json}
import play.api.libs.ws.WSClient
import scalikejdbc.DB

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Salesforce Transfer
 * @param ws WSClient
 */
class SalesforceTransfer @Inject()(
  ws:WSClient,
  transferConfigSalesforceDAO: TransferConfigSalesforceDAO
) extends BaseTransfer {
  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata, cryptoConfig) =>
      val taskBean = taskList.head

      getTransferConfigSalesforce(taskBean.transfer_config_id).map(tc => {
        val(sfUserName, sfPassword, sfClientId, sfClientSecret) = encodeSecrets(tc, cryptoConfig)
        loginToSalesforce(tc.api_url, sfClientId, sfClientSecret, sfUserName, sfPassword).map{
          case Some(apiToken) =>
            taskBean.t_salesforce.map(ts => {
              postSalesforceObject(ts, postdata, apiToken, tc.api_url)
            }).getOrElse(println("hoge")) // TODO 何か例外処理を実装する
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
  def loginToSalesforce(apiUrl: String, clientId: String, clientSecret: String, username: String, password: String): Future[Option[String]] = {
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
      .map(res => res.status match {
        case 200 => Json.parse(res.body)
          .validate[SalesforceLoginResponse]
          .asOpt
          .map(res => res.access_token)
        case _ => None
      })
  }

  private def encodeSecrets(tcs: TransferConfigSalesforce, cc: CryptoConfig) = {
    val crypto = Crypto(cc.secret_key_string, cc.cipher_algorithm, cc.secret_key_algorithm, cc.charset)
    (
      crypto.decrypt(tcs.sf_user_name, tcs.iv_user_name),
      crypto.decrypt(tcs.sf_password, tcs.iv_password),
      crypto.decrypt(tcs.sf_client_id, tcs.iv_client_id),
      crypto.decrypt(tcs.sf_client_secret, tcs.iv_client_secret)
    )
  }

  private def getTransferConfigSalesforce(transferConfigId: BigInt): Option[TransferConfigSalesforce] = {
    DB.localTx(implicit session => {
      transferConfigSalesforceDAO.get(transferConfigId)
    })
  }

  def postSalesforceObject(taskBeanSalesforce: TransferTaskBeanSalesforce, postdata: JsValue, apiToken: String, apiUrl: String) = {
      ws
        .url(apiUrl + "sobjects/" + taskBeanSalesforce.object_name)
        .addHttpHeaders("Authorization" -> s"Bearer $apiToken")
        .addHttpHeaders("Content-Type" -> "application/json")
        .post(createSalesforcePostdata(taskBeanSalesforce, postdata))
        .map(res => res.status match {
          case 200 => res.json
          case _ => None
        })
  }

  def getSalesforceObjectFields(taskBeanSalesforce: TransferTaskBeanSalesforce, apiToken: String, apiUrl: String) = {
      ws
        .url(apiUrl + "")
        .addHttpHeaders("Authorization" -> s"Bearer $apiToken")
        .addHttpHeaders("Content-Type" -> "application/json")
  }


  private def createSalesforcePostdata(taskBeanSalesforce: TransferTaskBeanSalesforce, postdata: JsValue): JsValue = {
    ???
    // TODO SFから挿入対象オブジェクトのフィールド情報を取る
    // SF上の型により、作成するデータの型を決める

    /*
      taskBeanSalesforce.fields.map(f => {
        f.field_name -> (postdata \ f.form_column_id).getOrElse(Json.parse("{}"))
      })

     */
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
  case class TransferTaskRequest(taskList: List[TransferTaskBean], postdata: JsValue, cryptoConfig: CryptoConfig)
}

