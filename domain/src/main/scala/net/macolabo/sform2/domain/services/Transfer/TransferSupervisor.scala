package net.macolabo.sform2.domain.services.Transfer

import org.apache.pekko.actor.{Actor, PoisonPill, Props, Terminated}
import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{FormDAO, FormTransferTaskConditionDAO, FormTransferTaskDAO, FormTransferTaskSesMailDAO, FormTransferTaskSalesforceDAO, FormTransferTaskSalesforceFieldDAO, TransferConfigMailAddressDAOImpl, TransferConfigSalesforceDAOImpl}
import play.api.libs.ws.WSClient

class TransferSupervisor @Inject()(
  transferConfigMailAddressDAO: TransferConfigMailAddressDAOImpl,
  transferConfigSalesforceDAO: TransferConfigSalesforceDAOImpl,
  formDAO: FormDAO,
  formTransferTaskDAO: FormTransferTaskDAO,
  formTransferTaskConditionDAO: FormTransferTaskConditionDAO,
  formTransferTaskSesMailDAO: FormTransferTaskSesMailDAO,
  formTransferTaskSalesforceDAO: FormTransferTaskSalesforceDAO,
  formTransferTaskSalesforceFieldDAO: FormTransferTaskSalesforceFieldDAO,
  ws: WSClient
)extends Actor {


  // 各Transfer用のActor
  private val sesMailTransfer = context.actorOf(Props(classOf[SesMailTransfer], transferConfigMailAddressDAO), "actor_sesmail_transfer")
  context.watch(sesMailTransfer)
  private val salesforceTransfer = context.actorOf(Props(classOf[SalesforceTransfer], ws, transferConfigSalesforceDAO), "actor_salesforce_transfer")
  context.watch(salesforceTransfer)

  private val transferReceiver = context.actorOf(
    Props(classOf[TransferReceiver],
      formDAO,
      formTransferTaskDAO,
      formTransferTaskConditionDAO,
      formTransferTaskSesMailDAO,
      formTransferTaskSalesforceDAO,
      formTransferTaskSalesforceFieldDAO,
      sesMailTransfer,
      salesforceTransfer
    ), "actor_transfer_receiver")
  context.watch(transferReceiver)

  def receive: Receive = {
    case Terminated(`sesMailTransfer`) => self ! PoisonPill
    case Terminated(`salesforceTransfer`) => self ! PoisonPill
    case Terminated(`transferReceiver`) => self ! PoisonPill
  }
}
