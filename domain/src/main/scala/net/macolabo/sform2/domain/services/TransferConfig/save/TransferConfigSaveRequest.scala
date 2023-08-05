package net.macolabo.sform2.domain.services.TransferConfig.save


case class TransferConfigSaveRequest(
  id: Option[BigInt],
  type_code: String,
  config_index: Int,
  name: String,
  status: Int,
  detail: TransferConfigDetailSaveRequest,
)

case class TransferConfigDetailSaveRequest(
  mail: Option[MailTransferConfigSaveRequest],
  salesforce: Option[SalesforceTransferConfigSaveRequest],
)

case class MailTransferConfigSaveRequest(
  id: Option[BigInt],
  transfer_config_id: BigInt,
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  mail_address_list: List[MailTransferConfigMailAddressSaveRequest]
)

case class MailTransferConfigMailAddressSaveRequest(
  id: Option[BigInt],
  transfer_config_mail_id: BigInt,
  address_index: Int,
  name: String,
  address: String
)

case class SalesforceTransferConfigSaveRequest(
  id: Option[BigInt],
  transfer_config_id: BigInt,
  sf_domain: String,
  api_version: String,
  sf_user_name: String,
  sf_password: String,
  sf_client_id: String,
  sf_client_secret: String,
  objects: List[SalesforceTransferConfigObjectSaveRequest]
)

case class SalesforceTransferConfigObjectSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_id: BigInt,
  name: String,
  label: String,
  active: Boolean,
  fields: List[SalesforceTransferConfigObjectFieldSaveRequest]
)

case class SalesforceTransferConfigObjectFieldSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_object_id: BigInt,
  name: String,
  label: String,
  field_type: String,
  active: Boolean
)