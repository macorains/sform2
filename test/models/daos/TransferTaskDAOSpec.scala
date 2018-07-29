package models.daos

import org.specs2.mutable.Specification
import setting.TestDBSettings

class TransferTaskDAOSpec extends Specification with TestDBSettings {

  "getTransferTaskListByFormId" should {
    "Test1" in {
      var transferTaskDAO = new TransferTaskDAO
      var result = transferTaskDAO.getTransferTaskListByFormId("11")
      println(result)
      result must_!= (None)
    }
  }

}
