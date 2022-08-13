package net.macolabo.sform2.domain.services.Transfer

import akka.actor.{Actor, PoisonPill, Props, Terminated}
import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{FormDAO, FormTransferTaskConditionDAO, FormTransferTaskDAO, FormTransferTaskMailDAO, FormTransferTaskSalesforceDAO, FormTransferTaskSalesforceFieldDAO, TransferConfigMailAddressDAOImpl}

class TransferSupervisor @Inject()(
  transferConfigMailAddressDAO: TransferConfigMailAddressDAOImpl,
  formDAO: FormDAO,
  formTransferTaskDAO: FormTransferTaskDAO,
  formTransferTaskConditionDAO: FormTransferTaskConditionDAO,
  formTransferTaskMailDAO: FormTransferTaskMailDAO,
  formTransferTaskSalesforceDAO: FormTransferTaskSalesforceDAO,
  formTransferTaskSalesforceFieldDAO: FormTransferTaskSalesforceFieldDAO
)extends Actor {


  // 各Transfer用のActor
  private val mailTransfer = context.actorOf(Props(classOf[MailTransfer], transferConfigMailAddressDAO), "actor_mail_transfer")
  context.watch(mailTransfer)
  private val salesforceTransfer = context.actorOf(Props(classOf[SalesforceTransfer]), "actor_salesforce_transfer")
  context.watch(salesforceTransfer)

  private val transferReceiver = context.actorOf(
    Props(classOf[TransferReceiver],
      formDAO,
      formTransferTaskDAO,
      formTransferTaskConditionDAO,
      formTransferTaskMailDAO,
      formTransferTaskSalesforceDAO,
      formTransferTaskSalesforceFieldDAO,
      mailTransfer,
      salesforceTransfer
    ), "actor_transfer_receiver")
  context.watch(transferReceiver)

  def receive: Receive = {
    case Terminated(`mailTransfer`) => self ! PoisonPill
    case Terminated(`salesforceTransfer`) => self ! PoisonPill
    case Terminated(`transferReceiver`) => self ! PoisonPill
  }
}
