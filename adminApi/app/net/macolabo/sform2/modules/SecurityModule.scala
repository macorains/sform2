package net.macolabo.sform2.modules

import org.apache.pekko.actor.ActorSystem
import com.google.inject.{AbstractModule, Inject, Provides}
import com.nimbusds.jose.JWSAlgorithm
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.domain.models.daos.UserDAOImpl
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.jwt.config.signature.{RSASignatureConfiguration, SecretSignatureConfiguration}
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import net.macolabo.sform2.views.models.security.{DatabaseExecutionContext, DemoHttpActionAdapter, SqlAuthencator}
import org.pac4j.core.authorization.generator.DefaultRolesAuthorizationGenerator
import org.pac4j.core.client.Clients
import org.pac4j.http.client.indirect.FormClient
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator
import org.pac4j.play.{CallbackController, LogoutController}
import play.api.{Configuration, Environment}

import java.nio.charset.StandardCharsets
import org.pac4j.play.store.{PlayCookieSessionStore, ShiroAesDataEncrypter}
import org.pac4j.core.config.Config
import org.pac4j.core.context.FrameworkParameters
import org.pac4j.core.context.session.{SessionStore, SessionStoreFactory}
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.DirectFormClient
import org.pac4j.oidc.client.OidcClient
import org.pac4j.oidc.config.OidcConfiguration
import org.pac4j.play.scala.{DefaultSecurityComponents, Pac4jScalaTemplateHelper, SecurityComponents}


/**
 * Guice DI module to be included in application.conf
 *
 * (1)とりあえずajaxからのフォーム認証を使えるように
 * (2)一通り動くようになったらopenidなどを追加
 * (3)jwt使えないか？
 */
class SecurityModule(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  val system: ActorSystem = ActorSystem("security")
  implicit val databaseExecutionContext: DatabaseExecutionContext = new DatabaseExecutionContext(system)

  private val baseUrl: String = configuration.get[String]("baseUrl")

  override def configure(): Unit = {

    val sKey = configuration.get[String]("play.http.secret.key").substring(0, 16)
    val dataEncrypter = new ShiroAesDataEncrypter(sKey.getBytes(StandardCharsets.UTF_8))

    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)

    bind(classOf[SessionStore]).toInstance(playSessionStore)
    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])
    bind(classOf[Pac4jScalaTemplateHelper[CommonProfile]])


    // callback
    val callbackController = new CallbackController()
    callbackController.setDefaultUrl("/oidcTest")
    bind(classOf[CallbackController]).toInstance(callbackController)

    // logout
    val logoutController = new LogoutController()
    logoutController.setDefaultUrl("/oidcTest")
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
    val userDAO = new UserDAOImpl()
    val client = new DirectFormClient()
    client.setAuthenticator(new SqlAuthencator(userDAO))
    client
  }

  @Provides
  def provideHeaderClient: HeaderClient = {
    val jwtAuthenticator = new JwtAuthenticator()
    // TODO secretを環境変数からとるようにする
    jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration("12345678901234567890123456789012"))
    val client = new HeaderClient()
    client.setHeaderName("X-Auth-Token")
    client.setAuthenticator(jwtAuthenticator)
    client
  }

  @Provides
  def provideOidcClient: OidcClient = {
    val oidcConfiguration = new OidcConfiguration()
    oidcConfiguration.setClientId(configuration.get[String]("sform.oauth.client_id"))
    oidcConfiguration.setSecret(configuration.get[String]("sform.oauth.client_secret"))
    oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration")
    oidcConfiguration.addCustomParam("prompt", "consent")
    oidcConfiguration.setScope("openid email profile")
    oidcConfiguration.setResponseType("code")
    oidcConfiguration.setUseNonce(true)

    // JWT の署名アルゴリズムを RS256 に設定
    val rsaSignatureConfig = new RSASignatureConfiguration()
    rsaSignatureConfig.setAlgorithm(JWSAlgorithm.RS256)
    oidcConfiguration.setPreferredJwsAlgorithm(rsaSignatureConfig.getAlgorithm)

    val client = new OidcClient()
    client.setConfiguration(oidcConfiguration)
    client.addAuthorizationGenerator(new DefaultRolesAuthorizationGenerator)
    client.setAjaxRequestResolver(new DefaultAjaxRequestResolver())
    client.setCallbackUrl(configuration.get[String]("baseUrl") + "/callback")
    client
  }

  @Provides
  def provideConfig(formClient: FormClient, directFormClient: DirectFormClient, headerClient: HeaderClient, oidcClient: OidcClient, sessionStore: SessionStore): Config = {
    val clients = new Clients(configuration.get[String]("baseUrl") + "/callback", formClient, directFormClient, headerClient, oidcClient)
    val config = new Config(clients)

    // セッションストアの設定
    config.setSessionStoreFactory(new SessionStoreFactory {
      override def newSessionStore(parameters: FrameworkParameters): SessionStore = sessionStore
    })

    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config
  }
}
