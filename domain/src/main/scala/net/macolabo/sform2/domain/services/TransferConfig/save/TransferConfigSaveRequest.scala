package net.macolabo.sform2.domain.services.TransferConfig.save

import play.api.libs.json.{Format, Json}

case class TransferConfigSaveRequest(
  id: Option[BigInt],
  type_code: String,
  config_index: Option[Int],
  name: String,
  status: Int,
  detail: TransferConfigDetailSaveRequest,
)

object TransferConfigSaveRequest {
  implicit val format: Format[TransferConfigSaveRequest] = Json.format[TransferConfigSaveRequest]
}

case class TransferConfigDetailSaveRequest(
  mail: Option[SesMailTransferConfigSaveRequest],
  salesforce: Option[SalesforceTransferConfigSaveRequest],
)

object TransferConfigDetailSaveRequest {
  implicit val format: Format[TransferConfigDetailSaveRequest] = Json.format[TransferConfigDetailSaveRequest]
}

case class SesMailTransferConfigSaveRequest(
  id: Option[BigInt],
  transfer_config_id: Option[BigInt],
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  mail_address_list: List[SesMailTransferConfigMailAddressSaveRequest]
)

object SesMailTransferConfigSaveRequest {
  implicit val format: Format[SesMailTransferConfigSaveRequest] = Json.format[SesMailTransferConfigSaveRequest]
}

case class SesMailTransferConfigMailAddressSaveRequest(
  id: Option[BigInt],
  transfer_config_id: Option[BigInt],
  address_index: Int,
  name: String,
  address: String
)

object SesMailTransferConfigMailAddressSaveRequest {
  implicit val format: Format[SesMailTransferConfigMailAddressSaveRequest] = Json.format[SesMailTransferConfigMailAddressSaveRequest]
}

case class SalesforceTransferConfigSaveRequest(
  id: Option[BigInt],
  transfer_config_id: Option[BigInt],
  sf_domain: String,
  api_version: String,
  sf_user_name: String,
  sf_password: String,
  sf_client_id: String,
  sf_client_secret: String,
  objects: List[SalesforceTransferConfigObjectSaveRequest]
)

object SalesforceTransferConfigSaveRequest {
  implicit val format: Format[SalesforceTransferConfigSaveRequest] = Json.format[SalesforceTransferConfigSaveRequest]
}

case class SalesforceTransferConfigObjectSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_id: Option[BigInt],
  name: String,
  label: String,
  active: Boolean,
  fields: List[SalesforceTransferConfigObjectFieldSaveRequest]
)

object SalesforceTransferConfigObjectSaveRequest {
  implicit val format: Format[SalesforceTransferConfigObjectSaveRequest] = Json.format[SalesforceTransferConfigObjectSaveRequest]
}

case class SalesforceTransferConfigObjectFieldSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_object_id: Option[BigInt],
  name: String,
  label: String,
  field_type: String,
  active: Boolean
)

object SalesforceTransferConfigObjectFieldSaveRequest {
  implicit val format: Format[SalesforceTransferConfigObjectFieldSaveRequest] = Json.format[SalesforceTransferConfigObjectFieldSaveRequest]
}
