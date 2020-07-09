package net.macolabo.sform2.models

import play.api.libs.json._

case class RCParam(objtype: String, action: String, rcdata: JsValue)
case class RsResultSet(result: String, message: String, dataset: JsValue) {
  def getDataset = dataset
}
object RsResultSet {
  implicit def jsonWrites = Json.writes[RsResultSet]
  implicit def jsonReads = Json.reads[RsResultSet]
}
