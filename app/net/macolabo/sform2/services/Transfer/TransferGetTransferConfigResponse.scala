package net.macolabo.sform2.services.Transfer

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class TransferGetTransferResponseMailTransferConfig(
                                                          id: Int,
                                                          transfer_config_id: Int,
                                                          subject: String,
                                                          reply_to: String,
                                                          from_address: String,
                                                          to_address: String,
                                                          cc_address: String,
                                                          bcc_address: String,
                                                          body: String,
                                                       )

case class TransferGetTransferResponseSalesforceTransferConfig(
                                                                id: Int,
                                                                transfer_config_id: Int,
                                                                sf_user_name: String,
                                                                sf_password: String,
                                                                sf_security_token: String,
                                                             )

case class TransferGetTransferResponseConfigDetail(
                                                    mail: Option[TransferGetTransferResponseMailTransferConfig],
                                                    salesforce: Option[TransferGetTransferResponseSalesforceTransferConfig],
                                                  )

case class TransferGetTransferConfigResponse(
                                        id: Int,
                                        type_code: String,
                                        config_index: Int,
                                        name: String,
                                        status: Int,
                                        detail: TransferGetTransferResponseConfigDetail,
                                      )

trait TransferGetTransferConfigResponseJson {
  implicit val TransferGetTransferResponseMailTransferConfigWrites: Writes[TransferGetTransferResponseMailTransferConfig] =
    (transferGetTransferResponseMailTransferConfig:TransferGetTransferResponseMailTransferConfig) => Json.obj(
    "id" -> transferGetTransferResponseMailTransferConfig.id,
    "transfer_config_id" -> transferGetTransferResponseMailTransferConfig.transfer_config_id,
    "subject" -> transferGetTransferResponseMailTransferConfig.subject,
    "reply_to" -> transferGetTransferResponseMailTransferConfig.reply_to,
    "from_address" -> transferGetTransferResponseMailTransferConfig.from_address,
    "to_address" -> transferGetTransferResponseMailTransferConfig.to_address,
    "cc_address" -> transferGetTransferResponseMailTransferConfig.cc_address,
    "bcc_address" -> transferGetTransferResponseMailTransferConfig.bcc_address,
    "body" -> transferGetTransferResponseMailTransferConfig.body,
  )

  implicit val TransferGetTransferResponseMailTransferConfigReads: Reads[TransferGetTransferResponseMailTransferConfig] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "transfer_config_id").read[Int] ~
      (JsPath \ "subject").read[String] ~
      (JsPath \ "reply_to").read[String] ~
      (JsPath \ "from_address").read[String] ~
      (JsPath \ "to_address").read[String] ~
      (JsPath \ "cc_address").read[String] ~
      (JsPath \ "bcc_address").read[String] ~
      (JsPath \ "body").read[String]
  )(TransferGetTransferResponseMailTransferConfig.apply _)

  implicit val TransferGetTransferResponseSalesforceTransferConfigWrites: Writes[TransferGetTransferResponseSalesforceTransferConfig] =
    (transferGetTransferResponseSalesforceTransferConfig: TransferGetTransferResponseSalesforceTransferConfig) => Json.obj(
    "id" -> transferGetTransferResponseSalesforceTransferConfig.id,
    "transfer_config_id" -> transferGetTransferResponseSalesforceTransferConfig.transfer_config_id,
    "sf_user_name" -> transferGetTransferResponseSalesforceTransferConfig.sf_user_name,
    "sf_password" -> transferGetTransferResponseSalesforceTransferConfig.sf_password,
    "sf_security_token" -> transferGetTransferResponseSalesforceTransferConfig.sf_security_token
  )

  implicit val TransferGetTransferResponseSalesforceTransferConfigReads: Reads[TransferGetTransferResponseSalesforceTransferConfig] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "transfer_config_id").read[Int] ~
      (JsPath \ "sf_user_name").read[String] ~
      (JsPath \ "sf_password").read[String] ~
      (JsPath \ "sf_security_token").read[String]
  )(TransferGetTransferResponseSalesforceTransferConfig.apply _)

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
    (JsPath \ "id").read[Int] ~
      (JsPath \ "type_code").read[String] ~
      (JsPath \ "config_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "detail").read[TransferGetTransferResponseConfigDetail]
    )(TransferGetTransferConfigResponse.apply _)
}