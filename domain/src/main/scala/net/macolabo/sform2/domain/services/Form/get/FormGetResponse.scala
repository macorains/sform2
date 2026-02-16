package net.macolabo.sform2.domain.services.Form.get

import play.api.libs.json.{Format, Json}

/**
 * フォーム取得API・フォーム項目・バリデーション
 * @param id ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param max_value 最大値
 * @param min_value 最小値
 * @param max_length 最大長
 * @param min_length 最小長
 * @param input_type 入力種別
 * @param required 入力必須
 */
case class FormColValidationGetReponse(
                                                 id: BigInt,
                                                 form_col_id: BigInt,
                                                 form_id: BigInt,
                                                 max_value: Option[Int],
                                                 min_value: Option[Int],
                                                 max_length: Option[Int],
                                                 min_length: Option[Int],
                                                 input_type: Int,
                                                 required: Boolean
                                               )

object FormColValidationGetReponse {
  implicit val format: Format[FormColValidationGetReponse] = Json.format[FormColValidationGetReponse]
}

/**
 * フォーム取得API・フォーム項目・選択リスト
 * @param id ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param select_index 順番
 * @param select_name 表示テキスト
 * @param select_value 値
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時CSSスタイル
 * @param view_style 参照時CSSスタイル
 */
case class FormColSelectGetReponse(
                                             id: BigInt,
                                             form_col_id: BigInt,
                                             form_id: BigInt,
                                             select_index: Int,
                                             select_name: String,
                                             select_value: String,
                                             is_default: Boolean,
                                             edit_style: String,
                                             view_style: String
                                           )

object FormColSelectGetReponse {
  implicit val format: Format[FormColSelectGetReponse] = Json.format[FormColSelectGetReponse]
}

/**
 * フォーム取得API・FormTransferTask
 * @param id FormTransferTask ID
 * @param transfer_config_id TransferConfig ID
 * @param transfer_config_name TransferConfig name
 * @param form_id フォームID
 * @param task_index 順番
 * @param name  name
 * @param form_transfer_task_conditions FormTransferTaskConditionのリスト
 * @param mail mail
 * @param salesforce salesforce
 */
case class FormTransferTaskGetResponse(
                             id: BigInt,
                             transfer_config_id: BigInt,
                             transfer_config_name: String,
                             form_id: BigInt,
                             task_index: Int,
                             name: String,
                             form_transfer_task_conditions: List[FormTransferTaskConditionGetReponse],
                             mail: Option[FormTransferTaskMailGetReponse],
                             salesforce: Option[FormTransferTaskSalesforceGetReponse]
                                              )

object FormTransferTaskGetResponse {
  implicit val format: Format[FormTransferTaskGetResponse] = Json.format[FormTransferTaskGetResponse]
}

/**
 * フォーム取得API・FormTransferTask・FormTransferTaskCondition
 * @param id FormTransferTaskCondition ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param form_id フォームID
 * @param form_col_id フォーム項目ID
 * @param operator 演算子
 * @param cond_value 値
 */
case class FormTransferTaskConditionGetReponse(
                                                         id: BigInt,
                                                         form_transfer_task_id: BigInt,
                                                         form_id: BigInt,
                                                         form_col_id: BigInt,
                                                         operator: String,
                                                         cond_value: String
                                                       )

object FormTransferTaskConditionGetReponse {
  implicit val format: Format[FormTransferTaskConditionGetReponse] = Json.format[FormTransferTaskConditionGetReponse]
}

/**
 * フォーム取得API・FormTransferTask・FormTransferTaskMail
 * @param id FormTransferTaskMail ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param from_address_id FROMに使うメールアドレスのID
 * @param to_address Toアドレス
 * @param cc_address Ccアドレス
 * @param bcc_address_id Bccに使うメールアドレスのID
 * @param replyto_address_id replyToに使うメールアドレスのID
 * @param subject 件名
 * @param body 本文
 */
case class FormTransferTaskMailGetReponse(
  id: BigInt,
  form_transfer_task_id: BigInt,
  from_address_id: BigInt,
  to_address: Option[String],
  to_address_id: Option[BigInt],
  to_address_field: Option[String],
  cc_address: Option[String],
  cc_address_id: Option[BigInt],
  cc_address_field: Option[String],
  bcc_address_id: Option[BigInt],
  replyto_address_id: Option[BigInt],
  subject: String,
  body: String
)

object FormTransferTaskMailGetReponse {
  implicit val format: Format[FormTransferTaskMailGetReponse] = Json.format[FormTransferTaskMailGetReponse]
}

/**
 * フォーム取得API・FormTransferTask・FormTransferTaskSalesforce
 * @param id FormTransferTaskSalesforce ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param object_name Salesforceオブジェクト名
 * @param fields フィールド割り当てリスト
 */
case class FormTransferTaskSalesforceGetReponse(
                                                          id: BigInt,
                                                          form_transfer_task_id: BigInt,
                                                          object_name: String,
                                                          fields: List[FormTransferTaskSalesforceFieldGetReponse]
                                                        )

object FormTransferTaskSalesforceGetReponse {
  implicit val format: Format[FormTransferTaskSalesforceGetReponse] = Json.format[FormTransferTaskSalesforceGetReponse]
}

/**
 * フォーム取得API・FormTransferTask・FormTransferTaskSalesforceField
 * @param id FormTransferTaskSalesforceField ID
 * @param form_transfer_task_salesforce_id FormTransferTaskSalesforce ID
 * @param form_column_id フォーム項目ID
 * @param field_name Salesforceフィールド名
 */
case class FormTransferTaskSalesforceFieldGetReponse(
                                                               id: BigInt,
                                                               form_transfer_task_salesforce_id: BigInt,
                                                               form_column_id: String,
                                                               field_name: String
                                                             )

object FormTransferTaskSalesforceFieldGetReponse {
  implicit val format: Format[FormTransferTaskSalesforceFieldGetReponse] = Json.format[FormTransferTaskSalesforceFieldGetReponse]
}

/**
 * フォーム取得API・フォーム項目
 * @param id ID
 * @param form_id フォームID
 * @param name 項目名
 * @param col_id 項目ID
 * @param col_index 順番
 * @param col_type 項目種別
 * @param default_value 初期値
 * @param select_list 選択リスト
 * @param validations バリデーション
 */
case class FormColGetReponse(
                              id: BigInt,
                              form_id: BigInt,
                              name: String,
                              col_id: String,
                              col_index: Int,
                              col_type: Int,
                              default_value: String,
                              select_list: List[FormColSelectGetReponse],
                              validations: Option[FormColValidationGetReponse]
                                     )

object FormColGetReponse {
  implicit val format: Format[FormColGetReponse] = Json.format[FormColGetReponse]
}

/**
 * フォーム取得API・フォームデータ
 * @param id フォームID
 * @param name フォーム名
 * @param form_index 順番
 * @param title タイトル
 * @param status ステータス
 * @param cancel_url キャンセル時遷移先URL
 * @param close_text フォームクローズ時文言
 * @param hashed_id ハッシュ化フォームID
 * @param complete_url 完了時遷移先URL
 * @param input_header 入力画面のヘッダ文言
 * @param complete_text 完了時の文言
 * @param confirm_header 確認画面のヘッダ文言
 * @param form_cols フォーム項目
 */
case class FormGetResponse(
                            id: BigInt,
                            name: String,
                            form_index: Int,
                            title: String,
                            status: Int,
                            cancel_url: String,
                            close_text: String,
                            hashed_id: String,
                            complete_url: String,
                            input_header: String,
                            complete_text: String,
                            confirm_header: String,
                            form_cols: List[FormColGetReponse],
                            form_transfer_tasks: List[FormTransferTaskGetResponse]
                              )

object FormGetResponse {
  implicit val format: Format[FormGetResponse] = Json.format[FormGetResponse]
}
