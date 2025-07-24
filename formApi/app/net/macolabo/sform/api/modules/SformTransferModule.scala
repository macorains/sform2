package net.macolabo.sform.api.modules

import org.apache.pekko.actor.{ActorRef, ActorSelection, ActorSystem}
import com.google.inject.{AbstractModule, Inject, Provides}
import net.macolabo.sform2.domain.services.Transfer.{MailTransfer, SalesforceTransfer, TransferReceiver, TransferSupervisor}
import play.api.libs.concurrent.PekkoGuiceSupport

import javax.inject.Named

class SformTransfer @Inject()(
  @Named("sform_transfer_supervisor") sformTransferSupervisor: ActorRef
)

class SformTransferModule extends AbstractModule with PekkoGuiceSupport
{
  override def configure() :Unit = {
    bindActor[TransferSupervisor]("sform_transfer_supervisor")
    bindActor[TransferReceiver]("actor_transfer_receiver")
    bindActor[MailTransfer]("actor_mail_transfer")
    bindActor[SalesforceTransfer]("actor_salesforce_transfer")
  }

  @Provides
  @Named("actor_transfer_receiver")
  def provideTransferReceiver(system: ActorSystem): ActorSelection = {
    system.actorSelection("/user/sform_transfer_supervisor/actor_transfer_receiver")
  }
}
