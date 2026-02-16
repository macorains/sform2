package net.macolabo.sform2.domain.services.TransferConfig.update

import play.api.libs.json.{Format, Json}

case class TransferUpdateTransferRequestMailTransferConfigMailAddress(
                                                                     id: Option[BigInt],
                                                                     transfer_config_mail_id: BigInt,
                                                                     address_index: Int,
                                                                     name: String,
                                                                     address: String
                                                                   )

object TransferUpdateTransferRequestMailTransferConfigMailAddress {
  implicit val format: Format[TransferUpdateTransferRequestMailTransferConfigMailAddress] = Json.format[TransferUpdateTransferRequestMailTransferConfigMailAddress]
}

case class TransferUpdateTransferRequestMailTransferConfig(
                                                          id: BigInt,
                                                          transfer_config_id: BigInt,
                                                          use_cc: Boolean,
                                                          use_bcc: Boolean,
                                                          use_replyto: Boolean,
                                                          mail_address_list: List[TransferUpdateTransferRequestMailTransferConfigMailAddress]
                                                        )

object TransferUpdateTransferRequestMailTransferConfig {
  implicit val format: Format[TransferUpdateTransferRequestMailTransferConfig] = Json.format[TransferUpdateTransferRequestMailTransferConfig]
}

case class TransferUpdateTransferRequestSalesforceTransferConfigObject(
                                                                      id: Option[BigInt],
                                                                      transfer_config_salesforce_id: BigInt,
                                                                      name: String,
                                                                      label: String,
                                                                      active: Boolean,
                                                                      fields: List[TransferUpdateTransferRequestSalesforceTransferConfigObjectField]
                                                                      )

object TransferUpdateTransferRequestSalesforceTransferConfigObject {
  implicit val format: Format[TransferUpdateTransferRequestSalesforceTransferConfigObject] = Json.format[TransferUpdateTransferRequestSalesforceTransferConfigObject]
}

case class TransferUpdateTransferRequestSalesforceTransferConfigObjectField(
                                                                             id: Option[BigInt],
                                                                             transfer_config_salesforce_object_id: BigInt,
                                                                             name: String,
                                                                             label: String,
                                                                             field_type: String,
                                                                             active: Boolean
                                                                           )

object TransferUpdateTransferRequestSalesforceTransferConfigObjectField {
  implicit val format: Format[TransferUpdateTransferRequestSalesforceTransferConfigObjectField] = Json.format[TransferUpdateTransferRequestSalesforceTransferConfigObjectField]
}

case class TransferUpdateTransferRequestSalesforceTransferConfig(
  id: BigInt,
  transfer_config_id: BigInt,
  sf_domain: String,
  api_version: String,
  sf_user_name: String,
  sf_password: String,
  sf_client_id: String,
  sf_client_secret: String,
  objects: List[TransferUpdateTransferRequestSalesforceTransferConfigObject]
)

object TransferUpdateTransferRequestSalesforceTransferConfig {
  implicit val format: Format[TransferUpdateTransferRequestSalesforceTransferConfig] = Json.format[TransferUpdateTransferRequestSalesforceTransferConfig]
}

case class TransferUpdateTransferRequestConfigDetail(
                                                    mail: Option[TransferUpdateTransferRequestMailTransferConfig],
                                                    salesforce: Option[TransferUpdateTransferRequestSalesforceTransferConfig],
                                                  )

object TransferUpdateTransferRequestConfigDetail {
  implicit val format: Format[TransferUpdateTransferRequestConfigDetail] = Json.format[TransferUpdateTransferRequestConfigDetail]
}

case class TransferUpdateTransferConfigRequest(
                                              id: BigInt,
                                              type_code: String,
                                              config_index: Int,
                                              name: String,
                                              status: Int,
                                              detail: TransferUpdateTransferRequestConfigDetail,
                                            )

object TransferUpdateTransferConfigRequest {
  implicit val format: Format[TransferUpdateTransferConfigRequest] = Json.format[TransferUpdateTransferConfigRequest]
}
