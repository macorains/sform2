package models.daos

import org.specs2.mutable.Specification
import play.api.libs.json.{ JsError, JsSuccess }
import setting.TestDBSettings

class TransfersDAOSpec extends Specification with TestDBSettings {

  "getTransferList" should {
    "Test1" in {
      val transfersDao = new TransfersDAO
      val result = transfersDao.getTransferList()
      println(result)
      val v = result.validate[List[transfersDao.TransferListJson]]
      v.isSuccess mustEqual (true)
    }
  }

}
