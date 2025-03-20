package net.macolabo.sform2.modules

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Inject, Provides}
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.domain.models.daos.UserDAOImpl
import org.pac4j.http.client.direct.HeaderClient
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
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
import org.pac4j.core.client.direct.AnonymousClient
import org.pac4j.core.config.Config
import org.pac4j.core.context.session.SessionStore
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

  val baseUrl: String = configuration.get[String]("baseUrl")
  val system: ActorSystem = ActorSystem("security")
  implicit val databaseExecutionContext: DatabaseExecutionContext = new DatabaseExecutionContext(system)
  //val userDAO = new UserDAOImpl()

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
  def provideFormClient: FormClient = new FormClient(baseUrl + "/loginForm", new SimpleTestUsernamePasswordAuthenticator())

  @Provides
  def provideDirectFormClient: DirectFormClient = {
    val userDAO = new UserDAOImpl()
    new DirectFormClient(new SqlAuthencator(userDAO))
  }

  @Provides
  def provideHeaderClient: HeaderClient = {
    val jwtAuthenticator = new JwtAuthenticator()
    jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration("12345678901234567890123456789012"))
    val headerClient = new HeaderClient("X-Auth-Token", jwtAuthenticator)
    headerClient
  }

  @Provides
  def provideOidcClient: OidcClient = {
    val oidcConfiguration = new OidcConfiguration()
    oidcConfiguration.setClientId(configuration.get[String]("sform.oauth.client_id"))
    oidcConfiguration.setSecret(configuration.get[String]("sform.oauth.client_secret"))
    oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration")
    oidcConfiguration.addCustomParam("prompt", "consent")
    val oidcClient = new OidcClient(oidcConfiguration)
    oidcClient.addAuthorizationGenerator(new DefaultRolesAuthorizationGenerator)
    oidcClient
  }
  @Provides
  def provideConfig(formClient: FormClient, directFormClient: DirectFormClient, headerClient: HeaderClient, oidcClient: OidcClient): Config = {
    val clients = new Clients(baseUrl + "/callback", formClient, directFormClient, headerClient, oidcClient, new AnonymousClient())

    val config = new Config(clients)
//    config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"))
//    config.addAuthorizer("hoge", new RequireAnyRoleAuthorizer("ROLE_HOGE"))
//    config.addAuthorizer("custom", new CustomAuthorizer)
//    config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected\\.html$"))
    config.setHttpActionAdapter(new DemoHttpActionAdapter())
    config
  }

}
