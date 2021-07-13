package net.macolabo.sform2.modules

import com.google.inject.AbstractModule
import net.macolabo.sform2.models.daos.{AuthTokenDAO, AuthTokenDAOImpl, FormDAO, FormDAOImpl}
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.services.AuthToken.{AuthTokenService, AuthTokenServiceImpl}

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[FormDAO].to[FormDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }
}
