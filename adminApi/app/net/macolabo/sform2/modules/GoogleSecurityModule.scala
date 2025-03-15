package net.macolabo.sform2.modules

import com.google.inject.Inject
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.oauth.client.Google2Client
import org.pac4j.play.store.PlayCookieSessionStore
class GoogleSecurityModule @Inject()(sessionStore: PlayCookieSessionStore) {

  private val googleClient = new Google2Client(
    "your-google-client-id",
    "your-google-client-secret"
  )

  googleClient.setScope("profile email") // メール & プロフィール情報を取得

  val clients = new Clients("http://localhost:9000/callback", googleClient)

  val config = new Config(clients)
  config.setSessionStore(sessionStore)

  def getConfig: Config = config
}
