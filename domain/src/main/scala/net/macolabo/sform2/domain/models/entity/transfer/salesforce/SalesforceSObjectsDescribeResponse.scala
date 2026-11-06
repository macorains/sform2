package net.macolabo.sform2.domain.models.entity.transfer.salesforce

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath, Json}

case class SalesforceSObjectsDescribeResponse(
  name: String,
  fields: List[SalesforceSObjectsDescribeResponseField]
)

object SalesforceSObjectsDescribeResponse {
  implicit val format: Format[SalesforceSObjectsDescribeResponse] = Json.format[SalesforceSObjectsDescribeResponse]
}

case class SalesforceSObjectsDescribeResponseField(
  name: String,
  label: String,
  _type: String,
  length: Int,
  createable: Boolean,
  updateable: Boolean,
  auto_number: Boolean,
  calculated: Boolean,
)

object SalesforceSObjectsDescribeResponseField {
  // Custom format required: JSON keys "type" and "autoNumber" differ from field names "_type" and "auto_number"
  implicit val format: Format[SalesforceSObjectsDescribeResponseField] = (
    (JsPath \ "name").format[String] ~
      (JsPath \ "label").format[String] ~
      (JsPath \ "type").format[String] ~
      (JsPath \ "length").format[Int] ~
      (JsPath \ "createable").format[Boolean] ~
      (JsPath \ "updateable").format[Boolean] ~
      (JsPath \ "autoNumber").format[Boolean] ~
      (JsPath \ "calculated").format[Boolean]
  )(SalesforceSObjectsDescribeResponseField.apply, unlift(SalesforceSObjectsDescribeResponseField.unapply))
}
