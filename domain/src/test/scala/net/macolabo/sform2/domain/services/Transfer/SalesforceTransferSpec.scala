package net.macolabo.sform2.domain.services.Transfer

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import net.macolabo.sform2.domain.models.daos.TransferConfigSalesforceDAOImpl
import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Span}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.ws.WSClient
import play.api.test.Injecting

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class SalesforceTransferSpec extends TestKit(ActorSystem("test"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with GuiceOneAppPerTest
  with Injecting
  with SformTestHelper
  //with BeforeAndAfterAll
{
  "SalesforceTransfer" must {
    "login to Salesforce" in {
      val dao = inject[TransferConfigSalesforceDAOImpl]
      val ws = inject[WSClient]

      val testActorRef = TestActorRef(new SalesforceTransfer(ws, dao))
      val transfer = testActorRef.underlyingActor
      val token = Await.result(
        transfer.loginToSalesforce(
          sys.env("SF_API_URL"),
          sys.env("SF_CLIENT_ID"),
          sys.env("SF_CLIENT_SECRET"),
          sys.env("SF_USERNAME"),
          sys.env("SF_PASSWORD")
        ), Duration.Inf).get

      val transferTaskBeanSalesforce = TransferTaskBeanSalesforce(
        1,
        1,
        "Accounts",
        "hoge",
        List.empty
      )



    }
  }
}
