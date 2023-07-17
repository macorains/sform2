package net.macolabo.sform2.domain.services.External.Salesforce

import com.google.inject.Inject
import com.sforce.soap.partner._
import com.sforce.soap.partner.sobject._
import net.macolabo.sform2.domain.models.entity.transfer.salesforce.{SalesforceSObjectsDescribeResponse, SalesforceSObjectsDescribeResponseJson, SalesforceSObjectsListResponse, SalesforceSObjectsListResponseJson}
import net.macolabo.sform2.domain.services.Transfer.{SalesforceLoginResponse, SalesforceLoginResponseJson, TransferGetTransferResponseSalesforceTransferConfig}
import net.macolabo.sform2.domain.utils.Logger
import play.api.libs.json.{JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SalesforceConnectionService @Inject()(
  ws:WSClient
) extends Logger
  with SalesforceLoginResponseJson
  with SalesforceSObjectsListResponseJson
  with SalesforceSObjectsDescribeResponseJson
{

  /**
   * 疎通確認
   * @param request SalesforceCheckConnectionRequest
   * @return SalesforceCheckConnectionResponse
   */
  def checkConnection(request: SalesforceCheckConnectionRequest): Future[Option[String]] = {
    getConnection(request.username, request.password, request.client_id, request.client_secret, request.domain, request.api_version)
  }

  /**
   * SalesforceへのREST APIログイン
   * @param username ユーザー名
   * @param password パスワード
   * @param clientId クライアントID
   * @param clientSecret クライアントシークレット
   * @param domain ドメイン https://hogehoge.my.salesforce.com
   * @param version APIバージョン v57.0
   * @return JWTトークン
   */
  def getConnection(username: String, password: String, clientId: String, clientSecret: String, domain: String, version: String): Future[Option[String]] = {
    val postdata = Map(
      "grant_type" -> "password",
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "username" -> username,
      "password" -> password
    )
    ws
      .url(domain + "/services/oauth2/token")
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post(postdata)
      .map(res => res.status match {
        case 200 => Json.parse(res.body)
          .validate[SalesforceLoginResponse]
          .asOpt
          .map(res => res.access_token)
        case _ =>
          logger.error(res.body)
          None
      })
  }

  /**
   * sObjectのname,labelのリスト取得
   * @param config TransferConfigSalesforce
   * @return SalesforceGetObjectResponseのリスト
   */
  def getObject(config: TransferGetTransferResponseSalesforceTransferConfig): Future[Option[List[SalesforceGetObjectResponse]]] = {
    getConnection(
      config.sf_user_name,
      config.sf_password,
      config.sf_client_id,
      config.sf_client_secret,
      config.sf_domain,
      config.api_version)
      .map {
        case Some(token: String) =>
          Await.result(ws
            .url(config.sf_domain + s"/services/data/${config.api_version}/sobjects")
            .addHttpHeaders("Authorization" -> s"Bearer $token")
            .addHttpHeaders("Content-Type" -> "application/json")
            .get()
            .map(res => res.status match {
              case 200 =>
                res.json.validate[SalesforceSObjectsListResponse] match {
                  case s: JsSuccess[SalesforceSObjectsListResponse] =>
                    Some(s.value.sobjects
                      .filter(so => so.createable)
                      .filter(so => so.updateable)
                      .filter(so => so.deletable)
                      .filter(so => so.queryable)
                      .filter(so => so.searchable)
                      .map(so => SalesforceGetObjectResponse(so.name, so.label)))
                  case _ => None
                }
              case _ => None
            }), Duration.Inf)
        case _ => None
      }
  }

  /**
   * sObjectフィールドの情報取得
   * @param config TransferConfigSalesforce
   * @param objectName オブジェクト名
   * @return SalesforceGetFieldResponseのリスト
   */
  def getField(config: TransferGetTransferResponseSalesforceTransferConfig, objectName: String): Future[Option[List[SalesforceGetFieldResponse]]] = {
    getConnection(
      config.sf_user_name,
      config.sf_password,
      config.sf_client_id,
      config.sf_client_secret,
      config.sf_domain,
      config.api_version)
      .map {
        case Some(token: String) =>
          Await.result(ws
            .url(config.sf_domain + s"/services/data/${config.api_version}/sobjects/$objectName/describe")
            .addHttpHeaders("Authorization" -> s"Bearer $token")
            .addHttpHeaders("Content-Type" -> "application/json")
            .get()
            .map(res => res.status match {
              case 200 =>
                res.json.validate[SalesforceSObjectsDescribeResponse] match {
                  case s: JsSuccess[SalesforceSObjectsDescribeResponse] =>
                    Some(s.value.fields
                      .filter(so => so.createable)
                      .filter(so => so.updateable)
                      .filter(so => !so.auto_number)
                      .filter(so => !so.calculated)
                      .map(so => SalesforceGetFieldResponse(so.name, so.label, so._type)))
                  case _ => None
                }
              case _ => None
            }), Duration.Inf)
        case _ => None
      }
  }

  // 開発時用ダミー
  def getDummyConnection(user: String, password: String, securityToken: String): Option[PartnerConnection] = {
    None
  }


  def create(connection: PartnerConnection, sObjectArray: Array[SObject]): Array[SaveResult] = {
    ???
    // TODO 必要ならREST APIを使う形に直す
//    val res = connection.create(sObjectArray)
//
//    res.foreach(r => r.getErrors.foreach(e => logger.debug(e.getMessage)))
//    res
  }

  def getSObjectInfo(connection: PartnerConnection, sObjects: List[String]): List[DescribeSObjectResult] = {
    ???
    // TODO 必要ならREST APIを使う形に直す
    // connection.describeSObjects(sObjects.toArray).toList
  }
}
object SalesforceConnectionService {

}
