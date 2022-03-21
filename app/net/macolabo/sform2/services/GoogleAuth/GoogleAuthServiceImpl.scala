package net.macolabo.sform2.services.GoogleAuth

import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.sys.env

class GoogleAuthServiceImpl @Inject()(
                                 ws:WSClient
                                 )(implicit ex: ExecutionContext)
  extends GoogleAuthService with GoogleTokenGetResponseJson
{
  def getToken(code: String): Unit = {
    val googleAuthInfo = env.get("GCP_AUTH_JSON").flatMap(authJson => {
      Json.parse(authJson).\("web").validate[GoogleTokenGetResponse].asOpt
    })
    println(env.get("GCP_AUTH_JSON").getOrElse("None"))
    println("**********")
    println(googleAuthInfo.toString)
    println("**********")

    googleAuthInfo.foreach(authInfo => {
      val postData = Map(
        "code" -> code,
        "client_id" -> authInfo.client_id,
        "client_secret" -> authInfo.client_secret,
        "redirect_url" -> "https://admin.it.sform.app/",
        "grant_type" -> "authorization_code"
      )

      val wsRequest: WSRequest =
        ws.url(authInfo.token_uri)
          .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      val response = wsRequest.post(postData)
      response.foreach(res => {
        println("**********")
        println(res.body)
        println("**********")
      })
    })
  }

}
