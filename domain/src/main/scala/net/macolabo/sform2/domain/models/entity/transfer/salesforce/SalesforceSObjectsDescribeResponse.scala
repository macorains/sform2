package net.macolabo.sform2.domain.models.entity.transfer.salesforce

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath}

case class SalesforceSObjectsDescribeResponse(
  name: String,
  fields: List[SalesforceSObjectsDescribeResponseField]
)

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

trait SalesforceSObjectsDescribeResponseJson {
  implicit val SalesforceSObjectsDescribeResponseFieldFormat: Format[SalesforceSObjectsDescribeResponseField] = (
    (JsPath \ "name").format[String] ~
      (JsPath \ "label").format[String] ~
      (JsPath \ "type").format[String] ~
      (JsPath \ "length").format[Int] ~
      (JsPath \ "createable").format[Boolean] ~
      (JsPath \ "updateable").format[Boolean] ~
      (JsPath \ "autoNumber").format[Boolean] ~
      (JsPath \ "calculated").format[Boolean]
  )(SalesforceSObjectsDescribeResponseField.apply, unlift(SalesforceSObjectsDescribeResponseField.unapply))

  implicit val salesforceSObjectsDescribeResponseFormat: Format[SalesforceSObjectsDescribeResponse] = (
    (JsPath \ "name").format[String] ~
      (JsPath \ "fields").format[List[SalesforceSObjectsDescribeResponseField]]
  )(SalesforceSObjectsDescribeResponse.apply, unlift(SalesforceSObjectsDescribeResponse.unapply))
}
