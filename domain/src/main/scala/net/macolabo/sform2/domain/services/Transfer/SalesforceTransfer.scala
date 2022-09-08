package net.macolabo.sform2.domain.services.Transfer

import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.TransferConfigSalesforceDAO
import net.macolabo.sform2.domain.models.entity.CryptoConfig
import net.macolabo.sform2.domain.models.entity.transfer.TransferConfigSalesforce
import net.macolabo.sform2.domain.models.entity.transfer.salesforce.{SalesforceSObjectsDescribeResponse, SalesforceSObjectsDescribeResponseJson}
import net.macolabo.sform2.domain.services.Transfer.SalesforceTransfer.TransferTaskRequest
import net.macolabo.sform2.domain.utils.Crypto
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsError, JsPath, JsSuccess, JsValue, Json}
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
) extends BaseTransfer
  with SalesforceSObjectsDescribeResponseJson
{
  override def receive: Receive = {
    case TransferTaskRequest(taskList, postdata, cryptoConfig) =>
      val taskBean = taskList.head
      taskBean.t_salesforce.flatMap(ts => {
        getTransferConfigSalesforce(taskBean.transfer_config_id).map(tc => {
          val(sfUserName, sfPassword, sfClientId, sfClientSecret) = encodeSecrets(tc, cryptoConfig)
          loginToSalesforce(tc.api_url, sfClientId, sfClientSecret, sfUserName, sfPassword).map{
            case Some(apiToken) =>
                postSalesforceObject(ts, postdata, apiToken, tc.api_url)
            case None => println("ほげー") // TODO 何か例外処理を実装する
          }
        })
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
        case _ => None // TODO ログに何か吐く
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

  def postSalesforceObject(taskBeanSalesforce: TransferTaskBeanSalesforce, postdata: JsValue, apiToken: String, apiUrl: String): Future[Option[JsValue]] = {
    // TODO apiUrlに何所まで含めるか? APIのバージョンをconfigに入れるか?
    // TODO SF上に存在しない項目が混じった場合どうなるか確認する
    println(apiUrl + "/services/data/v54.0/sobjects/" + taskBeanSalesforce.object_name + "/")
    ws
      .url(apiUrl + "/services/data/v54.0/sobjects/" + taskBeanSalesforce.object_name + "/")
      .addHttpHeaders("Authorization" -> s"Bearer $apiToken")
      .addHttpHeaders("Content-Type" -> "application/json")
      .post(createSalesforcePostdata(taskBeanSalesforce, postdata))
      .map(res => res.status match { // TODO 何故か成功時にNoneが返ってくるので要確認
        case 200 => Some(res.json)
        case _ =>
          println(res.json) // TODO ログに吐く
          None
      })
  }

  // TODO たぶんいらない。参考用。
  /*
  def getSalesforceObjectFields(taskBeanSalesforce: TransferTaskBeanSalesforce, apiToken: String, apiUrl: String): Future[Option[SalesforceSObjectsDescribeResponse]] = {
    // apiUrl はhttps://d7f00000010vcuai-dev-ed.my.salesforce.com/ まで。これ以降は足す必要あり。
    ws
      .url(apiUrl + s"/services/data/v54.0/sobjects/${taskBeanSalesforce.object_name}/describe")
      .addHttpHeaders("Authorization" -> s"Bearer $apiToken")
      .addHttpHeaders("Content-Type" -> "application/json")
      .get()
      .map(res => res.status match {
        case 200 =>
          res.json.validate[SalesforceSObjectsDescribeResponse] match {
            case s:JsSuccess[SalesforceSObjectsDescribeResponse] => Some(s.value)
            case _ => None
          }
        case _ => None
      })
  }
   */


  def createSalesforcePostdata(taskBeanSalesforce: TransferTaskBeanSalesforce, postdata: JsValue): String = {
      val data = taskBeanSalesforce.fields.map(f => {
        f.field_name -> (postdata \ f.form_column_id).asOpt[String].getOrElse("")
      }).toMap
      Json.toJson(data).toString()
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

