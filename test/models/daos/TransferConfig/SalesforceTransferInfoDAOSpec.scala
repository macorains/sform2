package models.daos.TransferConfig

//import org.specs2.mock.Mockito
//import play.api.test.PlaySpecification
import com.sforce.soap.partner.{ DescribeSObjectResult, Field }
import com.typesafe.config.ConfigFactory
import models.connector.SalesforceConnector
import models.daos.TransfersDAO
import models.json.SalesforceTransferJson
import org.specs2.mock._
import org.specs2.mutable._
import play.api.libs.json.{ Json, Writes }
import scalikejdbc.{ AutoSession, ConnectionPool }
import setting.TestDBSettings

class SalesforceTransferInfoDAOSpec extends Specification with TestDBSettings with SalesforceTransferJson {
  //sequential

  "getSObjectInfo" should {
    "Test1" in {
      val transfersDao = new TransfersDAO
      val salesforceConnector = new SalesforceConnector
      val salesforceTransferInfoDAO = new SalesforceTransferConfigDAO(transfersDao, salesforceConnector)
      val connection = salesforceConnector.getConnection("mac.rainshrine@macolabo.net", "NIFYttQ07jpM", "O9NxPFFATdgkpaNwMYF7x2ILy")
      val names: List[String] = salesforceConnector.getSObjectNames(connection.get)
      val res = salesforceConnector.getSObjectInfo(connection.get, names)
      res.foreach(r => {
        val t = Json.toJson(r)
        //println(r.getLabel)
        println(t.toString())
      })
      "a" must equalTo("a")
    }
  }

}
