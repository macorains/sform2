package net.macolabo.sform2.domain.services.TransferConfig.save

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, JsPath}

case class TransferConfigSaveRequest(
  id: Option[BigInt],
  type_code: String,
  config_index: Option[Int],
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
  transfer_config_id: Option[BigInt],
  use_cc: Boolean,
  use_bcc: Boolean,
  use_replyto: Boolean,
  mail_address_list: List[MailTransferConfigMailAddressSaveRequest]
)

case class MailTransferConfigMailAddressSaveRequest(
  id: Option[BigInt],
  transfer_config_mail_id: Option[BigInt],
  address_index: Int,
  name: String,
  address: String
)

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

case class SalesforceTransferConfigObjectSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_id: Option[BigInt],
  name: String,
  label: String,
  active: Boolean,
  fields: List[SalesforceTransferConfigObjectFieldSaveRequest]
)

case class SalesforceTransferConfigObjectFieldSaveRequest(
  id: Option[BigInt],
  transfer_config_salesforce_object_id: Option[BigInt],
  name: String,
  label: String,
  field_type: String,
  active: Boolean
)

trait TransferConfigSaveRequestJson {
  implicit val mailTransferConfigMailAddressSaveRequestFormat: Format[MailTransferConfigMailAddressSaveRequest] = (
      (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "transfer_config_mail_id").formatNullable[BigInt] ~
      (JsPath \ "address_index").format[Int] ~
      (JsPath \ "name").format[String] ~
      (JsPath \ "address").format[String]
  ) (MailTransferConfigMailAddressSaveRequest.apply, unlift(MailTransferConfigMailAddressSaveRequest.unapply))

  implicit val mailTransferConfigSaveRequestFormat: Format[MailTransferConfigSaveRequest] = (
    (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "transfer_config_id").formatNullable[BigInt] ~
      (JsPath \ "use_cc").format[Boolean] ~
      (JsPath \ "use_bcc").format[Boolean] ~
      (JsPath \ "use_replyto").format[Boolean] ~
      (JsPath \ "mail_address_list").format[List[MailTransferConfigMailAddressSaveRequest]]
    )(MailTransferConfigSaveRequest.apply, unlift(MailTransferConfigSaveRequest.unapply))

  implicit val salesforceTransferConfigObjectFieldSaveRequestFormat: Format[SalesforceTransferConfigObjectFieldSaveRequest] = (
    (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "transfer_config_salesforce_object_id").formatNullable[BigInt] ~
      (JsPath \ "name").format[String] ~
      (JsPath \ "label").format[String] ~
      (JsPath \ "field_type").format[String] ~
      (JsPath \ "active").format[Boolean]
    )(SalesforceTransferConfigObjectFieldSaveRequest.apply, unlift(SalesforceTransferConfigObjectFieldSaveRequest.unapply))

  implicit val salesforceTransferConfigObjectSaveRequestFormat: Format[SalesforceTransferConfigObjectSaveRequest] = (
    (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "transfer_config_salesforce_id").formatNullable[BigInt] ~
      (JsPath \ "name").format[String] ~
      (JsPath \ "label").format[String] ~
      (JsPath \ "active").format[Boolean] ~
      (JsPath \ "fields").format[List[SalesforceTransferConfigObjectFieldSaveRequest]]
    )(SalesforceTransferConfigObjectSaveRequest.apply, unlift(SalesforceTransferConfigObjectSaveRequest.unapply))

  implicit val salesforceTransferConfigSaveRequestFormat: Format[SalesforceTransferConfigSaveRequest] = (
    (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "transfer_config_id").formatNullable[BigInt] ~
      (JsPath \ "sf_domain").format[String] ~
      (JsPath \ "api_version").format[String] ~
      (JsPath \ "sf_user_name").format[String] ~
      (JsPath \ "sf_password").format[String] ~
      (JsPath \ "sf_client_id").format[String] ~
      (JsPath \ "sf_client_secret").format[String] ~
      (JsPath \ "objects").format[List[SalesforceTransferConfigObjectSaveRequest]]
    )(SalesforceTransferConfigSaveRequest.apply, unlift(SalesforceTransferConfigSaveRequest.unapply))

  implicit val transferConfigDetailSaveRequestFormat: Format[TransferConfigDetailSaveRequest] = (
    (JsPath \ "mail").formatNullable[MailTransferConfigSaveRequest] ~
      (JsPath \ "salesforce").formatNullable[SalesforceTransferConfigSaveRequest]
    )(TransferConfigDetailSaveRequest.apply, unlift(TransferConfigDetailSaveRequest.unapply))

  implicit val TransferConfigSaveRequestFormat: Format[TransferConfigSaveRequest] = (
    (JsPath \ "id").formatNullable[BigInt] ~
      (JsPath \ "type_code").format[String] ~
      (JsPath \ "config_index").formatNullable[Int] ~
      (JsPath \ "name").format[String] ~
      (JsPath \ "status").format[Int] ~
      (JsPath \ "detail").format[TransferConfigDetailSaveRequest]
    )(TransferConfigSaveRequest.apply, unlift(TransferConfigSaveRequest.unapply))
}
