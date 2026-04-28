package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class TransferGetTransferResponseSesMailTransferConfigMailAddress(
  id: BigInt,
  transfer_config_mail_id: BigInt,
  address_index: Int,
  name: String,
  address: String
)

object TransferGetTransferResponseSesMailTransferConfigMailAddress {
  implicit val format: Format[TransferGetTransferResponseSesMailTransferConfigMailAddress] = Json.format[TransferGetTransferResponseSesMailTransferConfigMailAddress]
}

case class TransferGetTransferResponseSesMailTransferConfig(
  id: BigInt,
  transfer_config_id: BigInt,
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  mail_address_list: List[TransferGetTransferResponseSesMailTransferConfigMailAddress]
)

object TransferGetTransferResponseSesMailTransferConfig {
  implicit val format: Format[TransferGetTransferResponseSesMailTransferConfig] = Json.format[TransferGetTransferResponseSesMailTransferConfig]
}

case class TransferGetTransferResponseSalesforceTransferConfigObjectField(
  id: BigInt,
  transfer_config_salesforce_object_id: BigInt,
  name: String,
  label: String,
  field_type: String,
  active: Boolean
)

object TransferGetTransferResponseSalesforceTransferConfigObjectField {
  implicit val format: Format[TransferGetTransferResponseSalesforceTransferConfigObjectField] = Json.format[TransferGetTransferResponseSalesforceTransferConfigObjectField]
}

case class TransferGetTransferResponseSalesforceTransferConfigObject(
  id: BigInt,
  transfer_config_salesforce_id: BigInt,
  name: String,
  label: String,
  active: Boolean,
  fields: List[TransferGetTransferResponseSalesforceTransferConfigObjectField]
)

object TransferGetTransferResponseSalesforceTransferConfigObject {
  implicit val format: Format[TransferGetTransferResponseSalesforceTransferConfigObject] = Json.format[TransferGetTransferResponseSalesforceTransferConfigObject]
}

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

object TransferGetTransferResponseSalesforceTransferConfig {
  implicit val format: Format[TransferGetTransferResponseSalesforceTransferConfig] = Json.format[TransferGetTransferResponseSalesforceTransferConfig]
}

case class TransferGetTransferResponseConfigDetail(
  mail: Option[TransferGetTransferResponseSesMailTransferConfig],
  salesforce: Option[TransferGetTransferResponseSalesforceTransferConfig],
)

object TransferGetTransferResponseConfigDetail {
  implicit val format: Format[TransferGetTransferResponseConfigDetail] = Json.format[TransferGetTransferResponseConfigDetail]
}

case class TransferGetTransferConfigResponse(
  id: BigInt,
  type_code: String,
  config_index: Int,
  name: String,
  status: Int,
  detail: TransferGetTransferResponseConfigDetail,
)

object TransferGetTransferConfigResponse {
  implicit val format: Format[TransferGetTransferConfigResponse] = Json.format[TransferGetTransferConfigResponse]
}
