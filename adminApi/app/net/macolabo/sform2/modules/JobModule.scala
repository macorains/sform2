package net.macolabo.sform2.modules

import net.codingwell.scalaguice.ScalaModule
import net.macolabo.sform2.jobs.Scheduler
import play.api.libs.concurrent.PekkoGuiceSupport

/**
 * The job module.
 */
class JobModule extends ScalaModule with PekkoGuiceSupport {

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
