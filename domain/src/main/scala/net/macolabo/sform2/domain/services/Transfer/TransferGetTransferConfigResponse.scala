package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Json, Reads, Writes}


case class TransferGetTransferResponseMailTransferConfigMailAddress(
  id: BigInt,
  transfer_config_mail_id: BigInt,
  address_index: Int,
  name: String,
  address: String
)

case class TransferGetTransferResponseMailTransferConfig(
  id: BigInt,
  transfer_config_id: BigInt,
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  mail_address_list: List[TransferGetTransferResponseMailTransferConfigMailAddress]
)

case class TransferGetTransferResponseSalesforceTransferConfigObjectField(
  id: BigInt,
  transfer_config_salesforce_object_id: BigInt,
  name: String,
  label: String,
  field_type: String,
  active: Boolean
)

case class TransferGetTransferResponseSalesforceTransferConfigObject(
  id: BigInt,
  transfer_config_salesforce_id: BigInt,
  name: String,
  label: String,
  active: Boolean,
  fields: List[TransferGetTransferResponseSalesforceTransferConfigObjectField]
)

case class TransferGetTransferResponseSalesforceTransferConfig(
  id: BigInt,
  transfer_config_id: BigInt,
  sf_domain: String,
  api_version: String,
  sf_user_name: String,
  sf_password: String,
  sf_client_id: String,
  sf_client_secret: String,
  objects: List[TransferGetTransferResponseSalesforceTransferConfigObject]
)

case class TransferGetTransferResponseConfigDetail(
  mail: Option[TransferGetTransferResponseMailTransferConfig],
  salesforce: Option[TransferGetTransferResponseSalesforceTransferConfig],
)

case class TransferGetTransferConfigResponse(
  id: BigInt,
  type_code: String,
  config_index: Int,
  name: String,
  status: Int,
  detail: TransferGetTransferResponseConfigDetail,
)

trait TransferGetTransferConfigResponseJson {
  implicit val TransferGetTransferResponseMailTransferConfigMailAddressWrites: Writes[TransferGetTransferResponseMailTransferConfigMailAddress] =
    (transferGetTransferResponseMailTransferConfigMailAddress:TransferGetTransferResponseMailTransferConfigMailAddress) =>Json.obj(
        "id" -> transferGetTransferResponseMailTransferConfigMailAddress.id,
    "transfer_config_mail_id" -> transferGetTransferResponseMailTransferConfigMailAddress.transfer_config_mail_id,
    "address_index" -> transferGetTransferResponseMailTransferConfigMailAddress.address_index,
    "name" -> transferGetTransferResponseMailTransferConfigMailAddress.name,
    "address" -> transferGetTransferResponseMailTransferConfigMailAddress.address
    )

  implicit val TransferGetTransferResponseMailTransferConfigMailAddressReads: Reads[TransferGetTransferResponseMailTransferConfigMailAddress] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_mail_id").read[BigInt] ~
      (JsPath \ "address_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "address").read[String]
  )(TransferGetTransferResponseMailTransferConfigMailAddress.apply _)

  implicit val TransferGetTransferResponseMailTransferConfigWrites: Writes[TransferGetTransferResponseMailTransferConfig] =
    (transferGetTransferResponseMailTransferConfig:TransferGetTransferResponseMailTransferConfig) => Json.obj(
    "id" -> transferGetTransferResponseMailTransferConfig.id,
    "transfer_config_id" -> transferGetTransferResponseMailTransferConfig.transfer_config_id,
    "use_cc" -> transferGetTransferResponseMailTransferConfig.use_cc,
    "use_bcc" -> transferGetTransferResponseMailTransferConfig.use_bcc,
    "use_replyto" -> transferGetTransferResponseMailTransferConfig.use_replyto,
      "mail_address_list" -> transferGetTransferResponseMailTransferConfig.mail_address_list
  )

  implicit val TransferGetTransferResponseMailTransferConfigReads: Reads[TransferGetTransferResponseMailTransferConfig] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "use_cc").read[Boolean] ~
      (JsPath \ "use_bcc").read[Boolean] ~
      (JsPath \ "use_replyto").read[Boolean] ~
      (JsPath \ "mail_address_list").read[List[TransferGetTransferResponseMailTransferConfigMailAddress]]
  )(TransferGetTransferResponseMailTransferConfig.apply _)

  implicit val TransferGetTransferResponseSalesforceTransferConfigObjectFieldWrites: Writes[TransferGetTransferResponseSalesforceTransferConfigObjectField] =
    (transferGetTransferResponseSalesforceTransferConfigObjectField: TransferGetTransferResponseSalesforceTransferConfigObjectField) => Json.obj(
      "id" -> transferGetTransferResponseSalesforceTransferConfigObjectField.id,
      "transfer_config_salesforce_object_id" -> transferGetTransferResponseSalesforceTransferConfigObjectField.transfer_config_salesforce_object_id,
      "name" -> transferGetTransferResponseSalesforceTransferConfigObjectField.name,
      "label" -> transferGetTransferResponseSalesforceTransferConfigObjectField.label,
      "field_type" -> transferGetTransferResponseSalesforceTransferConfigObjectField.field_type,
      "active" -> transferGetTransferResponseSalesforceTransferConfigObjectField.active
    )

  implicit val TransferGetTransferResponseSalesforceTransferConfigObjectFieldReads: Reads[TransferGetTransferResponseSalesforceTransferConfigObjectField] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_salesforce_object_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "field_type").read[String] ~
      (JsPath \ "active").read[Boolean]
    )(TransferGetTransferResponseSalesforceTransferConfigObjectField.apply _)

  implicit val TransferGetTransferResponseSalesforceTransferConfigObjectWrites: Writes[TransferGetTransferResponseSalesforceTransferConfigObject] =
    (transferGetTransferResponseSalesforceTransferConfigObject: TransferGetTransferResponseSalesforceTransferConfigObject) => Json.obj(
      "id" -> transferGetTransferResponseSalesforceTransferConfigObject.id,
      "transfer_config_salesforce_id" -> transferGetTransferResponseSalesforceTransferConfigObject.transfer_config_salesforce_id,
      "name" -> transferGetTransferResponseSalesforceTransferConfigObject.name,
      "label" -> transferGetTransferResponseSalesforceTransferConfigObject.label,
      "active" -> transferGetTransferResponseSalesforceTransferConfigObject.active,
      "fields" -> transferGetTransferResponseSalesforceTransferConfigObject.fields
    )

  implicit val TransferGetTransferResponseSalesforceTransferConfigObjectReads: Reads[TransferGetTransferResponseSalesforceTransferConfigObject] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_salesforce_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "active").read[Boolean] ~
      (JsPath \ "fields").read[List[TransferGetTransferResponseSalesforceTransferConfigObjectField]]
  )(TransferGetTransferResponseSalesforceTransferConfigObject.apply _)

  implicit val TransferGetTransferResponseSalesforceTransferConfigFormat: Format[TransferGetTransferResponseSalesforceTransferConfig] = (
    (JsPath \ "id").format[BigInt] ~
      (JsPath \ "transfer_config_id").format[BigInt] ~
      (JsPath \ "sf_domain").format[String] ~
      (JsPath \ "api_version").format[String] ~
      (JsPath \ "sf_user_name").format[String] ~
      (JsPath \ "sf_password").format[String] ~
      (JsPath \ "sf_client_id").format[String] ~
      (JsPath \ "sf_client_secret").format[String] ~
      (JsPath \ "objects").format[List[TransferGetTransferResponseSalesforceTransferConfigObject]]
    )(TransferGetTransferResponseSalesforceTransferConfig.apply, unlift(TransferGetTransferResponseSalesforceTransferConfig.unapply))

  implicit val TransferGetTransferResponseConfigDetailWrites: Writes[TransferGetTransferResponseConfigDetail] = (transferGetTransferResponseConfigDetail: TransferGetTransferResponseConfigDetail) => Json.obj(
    "mail" -> transferGetTransferResponseConfigDetail.mail,
    "salesforce" -> transferGetTransferResponseConfigDetail.salesforce
  )

  implicit val transferGetTransferResponseConfigDetailReads: Reads[TransferGetTransferResponseConfigDetail] = (
    (JsPath \ "mail").readNullable[TransferGetTransferResponseMailTransferConfig] ~
      (JsPath \ "salesforce").readNullable[TransferGetTransferResponseSalesforceTransferConfig]
    )(TransferGetTransferResponseConfigDetail.apply _)

  implicit val TransferGetTransferResponseWrites: Writes[TransferGetTransferConfigResponse] = (transferGetTransferResponse: TransferGetTransferConfigResponse) => Json.obj(
    "id" -> transferGetTransferResponse.id,
    "type_code" -> transferGetTransferResponse.type_code,
    "config_index" -> transferGetTransferResponse.config_index,
    "name" -> transferGetTransferResponse.name,
    "status" -> transferGetTransferResponse.status,
    "detail" -> transferGetTransferResponse.detail
  )

  implicit val TransferGetTransferResponseReads: Reads[TransferGetTransferConfigResponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "type_code").read[String] ~
      (JsPath \ "config_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "detail").read[TransferGetTransferResponseConfigDetail]
    )(TransferGetTransferConfigResponse.apply _)
}
