package net.macolabo.sform2.modules

import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.jobs.{AuthTokenCleaner, Scheduler}
import play.api.libs.concurrent.AkkaGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with AkkaGuiceSupport {

  /**
   * Configures the module.
   */
  override def configure() = {
    // bindActor[SalesforceDataRegister]("salesforce-data-register")
    // bindActor[MailTransferSendMail]("mail-transfer-send-mail")
    // bindActor[TransferJobManager]("transfer-job-manager")
    bind[Scheduler].asEagerSingleton()
  }
}
