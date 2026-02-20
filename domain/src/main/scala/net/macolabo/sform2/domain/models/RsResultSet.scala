package net.macolabo.sform2.domain.models

import play.api.libs.json.{Format, JsValue, Json}

case class RsResultSet(result: String, message: String, dataset: JsValue) {
  def getDataset: JsValue = dataset
}
object RsResultSet {
  implicit val format: Format[RsResultSet] = Json.format[RsResultSet]
}
