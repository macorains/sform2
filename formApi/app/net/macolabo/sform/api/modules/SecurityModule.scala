package net.macolabo.sform.api.modules

import org.apache.pekko.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.domain.models.daos.ApiTokenDAOImpl
import net.macolabo.sform.api.security.{DatabaseExecutionContext, DemoHttpActionAdapter, SqlAuthencator}
import org.pac4j.core.client.Clients
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.config.Config
import org.pac4j.core.context.FrameworkParameters
import org.pac4j.core.context.session.{SessionStore, SessionStoreFactory}
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.{DirectFormClient, HeaderClient}
import org.pac4j.http.client.indirect.FormClient
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.play.scala.{DefaultSecurityComponents, Pac4jScalaTemplateHelper, SecurityComponents}
import org.pac4j.play.store.{PlayCookieSessionStore, ShiroAesDataEncrypter}
import org.pac4j.play.{CallbackController, LogoutController}
import play.api.{Configuration, Environment}

import java.nio.charset.StandardCharsets

/**
 * Guice DI module to be included in application.conf
 * */
class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  val baseUrl: String = configuration.get[String]("baseUrl")
  val system: ActorSystem = ActorSystem("security")
  implicit val databaseExecutionContext: DatabaseExecutionContext = new DatabaseExecutionContext(system)

  override def configure(): Unit = {

    val sKey = configuration.get[String]("play.http.secret.key").substring(0, 16)
    val dataEncrypter = new ShiroAesDataEncrypter(sKey.getBytes(StandardCharsets.UTF_8))

    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)
    bind(classOf[SessionStore]).toInstance(playSessionStore)
    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])
    bind(classOf[Pac4jScalaTemplateHelper[CommonProfile]])

    // callback
    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/?defaulturlafterlogout")
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new LogoutController()
    logoutController.setDefaultUrl("/")
    bind(classOf[LogoutController]).toInstance(logoutController)
  }

  @Provides
  def provideFormClient: FormClient = {
    val client = new FormClient()
    client.setLoginUrl(baseUrl + "/loginForm")
    client.setAuthenticator(new SimpleTestUsernamePasswordAuthenticator())
    client
  }

  @Provides
  def provideDirectFormClient: DirectFormClient = {
    val apiTokenDAO = new ApiTokenDAOImpl()
    val client = new DirectFormClient()
    client.setAuthenticator(new SqlAuthencator(apiTokenDAO))
    client
  }

  @Provides
  def provideHeaderClient: HeaderClient = {
    val jwtAuthenticator = new JwtAuthenticator()
    jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration("12345678901234567890123456789012"))
    val client = new HeaderClient()
    client.setHeaderName("X-Auth-Token")
    client.setAuthenticator(jwtAuthenticator)
    client
  }

  @Provides
  def provideConfig(formClient: FormClient, directFormClient: DirectFormClient, headerClient: HeaderClient, sessionStore: SessionStore): Config = {
    val clients = new Clients(configuration.get[String]("baseUrl") + "/callback", formClient, directFormClient, headerClient, new AnonymousClient())
    val config = new Config(clients)

    // セッションストアの設定
    config.setSessionStoreFactory(new SessionStoreFactory {
      override def newSessionStore(parameters: FrameworkParameters): SessionStore = sessionStore
    })

    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config
  }
}
