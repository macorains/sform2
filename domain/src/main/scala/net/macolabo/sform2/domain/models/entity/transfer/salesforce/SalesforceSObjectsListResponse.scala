package net.macolabo.sform2.domain.models.entity.transfer.salesforce

import play.api.libs.json.{Format, Json}

case class SalesforceSObjectsListResponse(
  encoding: String,
  sobjects: List[SalesforceSObjectsListResponseSObject]
)

object SalesforceSObjectsListResponse {
  implicit val format: Format[SalesforceSObjectsListResponse] = Json.format[SalesforceSObjectsListResponse]
}

case class SalesforceSObjectsListResponseSObject(
  name: String,
  label: String,
  createable: Boolean,
  updateable: Boolean,
  deletable: Boolean,
  queryable: Boolean,
  searchable: Boolean
)

object SalesforceSObjectsListResponseSObject {
  implicit val format: Format[SalesforceSObjectsListResponseSObject] = Json.format[SalesforceSObjectsListResponseSObject]
}
