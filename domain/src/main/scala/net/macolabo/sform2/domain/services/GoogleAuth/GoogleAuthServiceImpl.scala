package net.macolabo.sform2.domain.services.GoogleAuth

import net.macolabo.sform2.domain.utils.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}

import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.sys.env

class GoogleAuthServiceImpl @Inject()(
                                 ws:WSClient
                                 )(implicit ex: ExecutionContext)
  extends GoogleAuthService
    with GoogleAuthCodeGetResponseJson
    with GoogleAuthTokenGetResponseJson
    with Logger
{
  def getToken(code: String) = {
    val googleAuthInfo = env.get("GCP_AUTH_JSON").flatMap(authJson => {
      Json.parse(authJson).\("web").validate[GoogleAuthCodeGetResponse].asOpt
    })

    googleAuthInfo.flatMap(authInfo => {
      val postData = Map(
        "code" -> code,
        "client_id" -> authInfo.client_id,
        "client_secret" -> authInfo.client_secret,
        // TODO ホスト名部分を動的に入れるようにしたい。とりあえず本番用でテストする。
        // "redirect_uri" -> "http://localhost:9001/oauthToken",
        "redirect_uri" -> "https://admin.sform.app/oauthToken",
        "grant_type" -> "authorization_code"
      )

      val wsRequest: WSRequest =
        ws.url(authInfo.token_uri)
          .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")

      val accessToken = wsRequest.post(postData).map(res => {
        logger.info("*** GoogleAuthService getToken ***")
        logger.info(res.json.toString())
        Json.parse(res.body).validate[GoogleAuthTokenGetResponse].asOpt.map(_.access_token)
      })
      Await.result(accessToken, Duration.Inf)
    })
  }

}
