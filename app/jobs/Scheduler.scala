package jobs

import akka.actor.{ ActorRef, ActorSystem }
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

/**
 * Schedules the jobs.
 */
class Scheduler @Inject() (
  system: ActorSystem,
  @Named("auth-token-cleaner") authTokenCleaner: ActorRef,
  //@Named("salesforce-data-register") salesforceDataRegister: ActorRef,
  //@Named("mail-transfer-send-mail") mailTransferSendMail: ActorRef
  @Named("transfer-job-manager") transferJobManager: ActorRef
) {

  QuartzSchedulerExtension(system).schedule("AuthTokenCleaner", authTokenCleaner, AuthTokenCleaner.Clean)
  //Salesforce登録バッチ処理起動
  //QuartzSchedulerExtension(system).schedule("Every5Seconds", salesforceDataRegister, "555")
  // MailTransferメール送信バッチ処理起動
  // QuartzSchedulerExtension(system).schedule("MailTransferSendMail", mailTransferSendMail, "Mail")
  // TransferJobManager起動
  // TODO 検証のため一時停止 (2019/03/15)
  QuartzSchedulerExtension(system).schedule("Every5Seconds", transferJobManager, "_Exec")

  authTokenCleaner ! AuthTokenCleaner.Clean
}
