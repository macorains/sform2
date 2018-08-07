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
  @Named("salesforce-data-register") salesforceDataRegister: ActorRef,
  @Named("mail-transfer-send-mail") mailTransferSendMail: ActorRef
) {

  QuartzSchedulerExtension(system).schedule("AuthTokenCleaner", authTokenCleaner, AuthTokenCleaner.Clean)
  //Salesforce登録バッチ処理起動
  QuartzSchedulerExtension(system).schedule("Every5Seconds", salesforceDataRegister, "555")
  // MailTransferメール送信バッチ処理起動
  QuartzSchedulerExtension(system).schedule("MailTransferSendMail", mailTransferSendMail, "Mail")

  authTokenCleaner ! AuthTokenCleaner.Clean
}