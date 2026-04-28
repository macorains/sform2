package net.macolabo.sform2.domain.services.Transfer

import play.api.libs.json.{Format, Json}

case class TransferInsertTransferRequestSesMailTransferConfigMailAddress(
                                                                     transfer_config_mail_id: BigInt,
                                                                     address_index: Int,
                                                                     name: String,
                                                                     address: String
                                                                   )

object TransferInsertTransferRequestSesMailTransferConfigMailAddress {
  implicit val format: Format[TransferInsertTransferRequestSesMailTransferConfigMailAddress] = Json.format[TransferInsertTransferRequestSesMailTransferConfigMailAddress]
}

case class TransferInsertTransferRequestSesMailTransferConfig(
                                                          transfer_config_id: BigInt,
                                                          use_cc: Boolean,
                                                          use_bcc: Boolean,
                                                          use_replyto: Boolean,
                                                          mail_address_list: List[TransferInsertTransferRequestSesMailTransferConfigMailAddress]
                                                        )

object TransferInsertTransferRequestSesMailTransferConfig {
  implicit val format: Format[TransferInsertTransferRequestSesMailTransferConfig] = Json.format[TransferInsertTransferRequestSesMailTransferConfig]
}

case class TransferInsertTransferRequestSalesforceTransferConfigObject(
                                                                      transfer_config_salesforce_id: BigInt,
                                                                      name: String,
                                                                      label: String,
                                                                      active: Boolean,
                                                                      fields: List[TransferInsertTransferRequestSalesforceTransferConfigObjectField]
                                                                      )

object TransferInsertTransferRequestSalesforceTransferConfigObject {
  implicit val format: Format[TransferInsertTransferRequestSalesforceTransferConfigObject] = Json.format[TransferInsertTransferRequestSalesforceTransferConfigObject]
}

case class TransferInsertTransferRequestSalesforceTransferConfigObjectField(
                                                                             transfer_config_salesforce_object_id: BigInt,
                                                                             name: String,
                                                                             label: String,
                                                                             field_type: String,
                                                                             active: Boolean
                                                                           )

object TransferInsertTransferRequestSalesforceTransferConfigObjectField {
  implicit val format: Format[TransferInsertTransferRequestSalesforceTransferConfigObjectField] = Json.format[TransferInsertTransferRequestSalesforceTransferConfigObjectField]
}

case class TransferInsertTransferRequestSalesforceTransferConfig(
                                                                transfer_config_id: BigInt,
                                                                sf_user_name: String,
                                                                sf_password: String,
                                                                sf_security_token: String,
                                                                objects: List[TransferInsertTransferRequestSalesforceTransferConfigObject]
                                                              )

object TransferInsertTransferRequestSalesforceTransferConfig {
  implicit val format: Format[TransferInsertTransferRequestSalesforceTransferConfig] = Json.format[TransferInsertTransferRequestSalesforceTransferConfig]
}

case class TransferInsertTransferRequestConfigDetail(
                                                    mail: Option[TransferInsertTransferRequestSesMailTransferConfig],
                                                    salesforce: Option[TransferInsertTransferRequestSalesforceTransferConfig],
                                                  )

object TransferInsertTransferRequestConfigDetail {
  implicit val format: Format[TransferInsertTransferRequestConfigDetail] = Json.format[TransferInsertTransferRequestConfigDetail]
}

case class TransferInsertTransferConfigRequest(
                                              type_code: String,
                                              config_index: Int,
                                              name: String,
                                              status: Int,
                                              detail: TransferInsertTransferRequestConfigDetail,
                                            )

object TransferInsertTransferConfigRequest {
  implicit val format: Format[TransferInsertTransferConfigRequest] = Json.format[TransferInsertTransferConfigRequest]
}
