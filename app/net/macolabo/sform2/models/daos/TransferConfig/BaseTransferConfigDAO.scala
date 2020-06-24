package net.macolabo.sform2.models.daos.TransferConfig

import net.macolabo.sform2.models.User
import play.api.libs.json.{JsValue, Json}

class BaseTransferConfigDAO {
  val transferType: Int = 0;
  def getTransferConfig: JsValue = {
    println("BaseTransferInfoDAO")
    Json.toJson("""{}""")
  }
  def saveTransferConfig(config: JsValue, identity: User): JsValue = {
    Json.toJson("""{}""")
  }

}
object BaseTransferConfigDAO {

}
