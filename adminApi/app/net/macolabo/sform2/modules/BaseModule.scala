package net.macolabo.sform2.modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.domain.models.daos.{ApiTokenDAO, ApiTokenDAOImpl, AuthTokenDAO, AuthTokenDAOImpl, FormDAO, FormDAOImpl, PostdataDAO, PostdataDAOImpl, TransferConfigDAO, TransferConfigDAOImpl, TransferConfigMailAddressDAO, TransferConfigMailAddressDAOImpl, TransferConfigMailDAO, TransferConfigMailDAOImpl, TransferConfigSalesforceDAO, TransferConfigSalesforceDAOImpl, TransferConfigSalesforceObjectDAO, TransferConfigSalesforceObjectDAOImpl, TransferConfigSalesforceObjectFieldDAO, TransferConfigSalesforceObjectFieldDAOImpl, TransferDetailLogDAO, TransferDetailLogDAOImpl, TransferLogDAO, TransferLogDAOImpl, UserDAO, UserDAOImpl}
import net.macolabo.sform2.domain.services.ApiToken.{ApiTokenService, ApiTokenServiceImpl}
import net.macolabo.sform2.domain.services.AuthToken.{AuthTokenService, AuthTokenServiceImpl}
import net.macolabo.sform2.domain.services.GoogleAuth.{GoogleAuthService, GoogleAuthServiceImpl}
import net.macolabo.sform2.domain.services.TransferConfig.{TransferConfigService, TransferConfigServiceImpl}
import net.macolabo.sform2.domain.services.User.{UserService, UserServiceImpl}

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  override def configure(): Unit = {
    // DAOs
    bind[ApiTokenDAO].to[ApiTokenDAOImpl]
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[FormDAO].to[FormDAOImpl]
    bind[PostdataDAO].to[PostdataDAOImpl]
    bind[TransferConfigDAO].to[TransferConfigDAOImpl]
    bind[TransferConfigMailDAO].to[TransferConfigMailDAOImpl]
    bind[TransferConfigMailAddressDAO].to[TransferConfigMailAddressDAOImpl]
    bind[TransferConfigSalesforceDAO].to[TransferConfigSalesforceDAOImpl]
    bind[TransferConfigSalesforceObjectDAO].to[TransferConfigSalesforceObjectDAOImpl]
    bind[TransferConfigSalesforceObjectFieldDAO].to[TransferConfigSalesforceObjectFieldDAOImpl]
    bind[TransferDetailLogDAO].to[TransferDetailLogDAOImpl]
    bind[TransferLogDAO].to[TransferLogDAOImpl]
    bind[UserDAO].to[UserDAOImpl]
    // Services
    bind[ApiTokenService].to[ApiTokenServiceImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[GoogleAuthService].to[GoogleAuthServiceImpl]
    bind[TransferConfigService].to[TransferConfigServiceImpl]
    bind[UserService].to[UserServiceImpl]
  }
}
