package net.macolabo.sform2.views.models

import play.api.libs.json.{JsValue, Json, Writes, Reads}

case class RsResultSet(result: String, message: String, dataset: JsValue) {
  def getDataset: JsValue = dataset
}
object RsResultSet {
  implicit def jsonWrites: Writes[RsResultSet] = Json.writes[RsResultSet]
  implicit def jsonReads: Reads[RsResultSet] = Json.reads[RsResultSet]
}
