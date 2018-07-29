package modules

import jobs.{ AuthTokenCleaner, MailTransferSendMail, SalesforceDataRegister, Scheduler }
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  def configure() = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bindActor[SalesforceDataRegister]("salesforce-data-register")
    bindActor[MailTransferSendMail]("mail-transfer-send-mail")
    bind[Scheduler].asEagerSingleton()
  }
}
