package net.macolabo.sform2.domain.models.entity.transfer.salesforce

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath}

case class SalesforceSObjectsListResponse(
  encoding: String,
  sobjects: List[SalesforceSObjectsListResponseSObject]
)

case class SalesforceSObjectsListResponseSObject(
  name: String,
  label: String,
  createable: Boolean,
  updateable: Boolean,
  deletable: Boolean,
  queryable: Boolean,
  searchable: Boolean
)

trait SalesforceSObjectsListResponseJson {
  implicit val SalesforceSObjectsListResponseSObjectFormat: Format[SalesforceSObjectsListResponseSObject] = (
    (JsPath \ "name").format[String] ~
      (JsPath \ "label").format[String] ~
      (JsPath \ "createable").format[Boolean] ~
      (JsPath \ "updateable").format[Boolean] ~
      (JsPath \ "deletable").format[Boolean] ~
      (JsPath \ "queryable").format[Boolean] ~
      (JsPath \ "searchable").format[Boolean]
    )(SalesforceSObjectsListResponseSObject.apply, unlift(SalesforceSObjectsListResponseSObject.unapply))

  implicit val SalesforceSObjectsListResponseFormat: Format[SalesforceSObjectsListResponse] = (
    (JsPath \ "encoding").format[String] ~
      (JsPath \ "sobjects").format[List[SalesforceSObjectsListResponseSObject]]
    )(SalesforceSObjectsListResponse.apply, unlift(SalesforceSObjectsListResponse.unapply))
}
