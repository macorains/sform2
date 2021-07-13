package net.macolabo.sform2.services.Form.update

/**
 * フォーム更新API・フォームデータ
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
case class FormUpdateRequest(
                              id: Option[BigInt],
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
                              form_cols: List[FormColUpdateRequest],
                              form_transfer_tasks: List[FormTransferTaskUpdateRequest]
                            )

/**
 * フォーム更新API・フォーム項目
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
case class FormColUpdateRequest(
                                         id: Option[BigInt],
                                         form_id: Option[BigInt],
                                         name: String,
                                         col_id: String,
                                         col_index: Int,
                                         col_type: Int,
                                         default_value: String,
                                         select_list: List[FormColSelectUpdateRequest],
                                         validations: FormColValidationUpdateRequest
                                       )

/**
 * フォーム更新API・フォーム項目・バリデーション
 * @param id ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param max_value 最大値
 * @param min_value 最小値
 * @param max_length 最大長
 * @param min_length 最小長
 * @param input_type 入力種別
 */
case class FormColValidationUpdateRequest(
                                                   id: Option[BigInt],
                                                   form_col_id: Option[BigInt],
                                                   form_id: Option[BigInt],
                                                   max_value: Option[Int],
                                                   min_value: Option[Int],
                                                   max_length: Option[Int],
                                                   min_length: Option[Int],
                                                   input_type: Int,
                                                   required: Boolean
                                                 )

/**
 * フォーム更新API・フォーム項目・選択リスト
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
case class FormColSelectUpdateRequest(
                                               id: Option[BigInt],
                                               form_col_id: Option[BigInt],
                                               form_id: Option[BigInt],
                                               select_index: Int,
                                               select_name: String,
                                               select_value: String,
                                               is_default: Boolean,
                                               edit_style: String,
                                               view_style: String
                                             )

/**
 * フォーム更新API・FormTransferTask
 * @param id ID
 * @param transfer_config_id TransferConfig ID
 * @param form_id Form ID
 * @param task_index 順番
 * @param name 名前
 * @param form_transfer_task_conditions 実行条件データ
 * @param mail MailTransfer設定
 * @param salesforce SalesforceTransfer設定
 */
case class FormTransferTaskUpdateRequest(
                                          id: Option[BigInt],
                                          transfer_config_id: BigInt,
                                          form_id: BigInt,
                                          task_index: Int,
                                          name: String,
                                          form_transfer_task_conditions: List[FormTransferTaskConditionUpdateRequest],
                                          mail: Option[FormTransferTaskMailUpdateRequest],
                                          salesforce: Option[FormTransferTaskSalesforceUpdateRequest]
                                                )

/**
 * フォーム更新API・FormTransferTask・Condition
 * @param id FormTransferTaskCondition ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param form_id フォームID
 * @param form_col_id フォーム項目ID
 * @param operator 演算子
 * @param cond_value 値　
 */
case class FormTransferTaskConditionUpdateRequest(
                                                           id: Option[BigInt],
                                                           form_transfer_task_id: Option[BigInt],
                                                           form_id: Option[BigInt],
                                                           form_col_id: BigInt,
                                                           operator: String,
                                                           cond_value: String
                                                         )

/**
 * フォーム更新API・FormTransferTask・Mail
 * @param id ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param from_address_id FROMに使うメールアドレスのID
 * @param to_address Toアドレス
 * @param cc_address Ccアドレス
 * @param bcc_address_id Bccに使うメールアドレスのID
 * @param replyto_address_id replyToに使うメールアドレスのID
 * @param subject 件名
 * @param body 本文
 */
case class FormTransferTaskMailUpdateRequest(
                                                      id: Option[BigInt],
                                                      form_transfer_task_id: Option[BigInt],
                                                      from_address_id: BigInt,
                                                      to_address: String,
                                                      cc_address: Option[String],
                                                      bcc_address_id: Option[BigInt],
                                                      replyto_address_id: Option[BigInt],
                                                      subject: String,
                                                      body: String
                                                    )

/**
 * フォーム更新API・FormTransferTask・Salesforce
 * @param id ID
 * @param form_transfer_task_id FormTransferTask ID
 * @param object_name Salesforceオブジェクト名
 * @param fields フィールド割り当て情報
 */
case class FormTransferTaskSalesforceUpdateRequest(
                                                            id: Option[BigInt],
                                                            form_transfer_task_id: Option[BigInt],
                                                            object_name: String,
                                                            fields: List[FormTransferTaskSalesforceFieldUpdateRequest]
                                                          )

/**
 * フォーム更新API・FormTransferTask・Salesforce・Field
 * @param id FormTransferTaskSalesforceField ID
 * @param form_transfer_task_salesforce_id FormTransferTaskSalesforce ID
 * @param form_column_id フォーム項目ID
 * @param field_name Salesforceフィールド名
 */
case class FormTransferTaskSalesforceFieldUpdateRequest(
                                                                 id: Option[BigInt],
                                                                 form_transfer_task_salesforce_id: Option[BigInt],
                                                                 form_column_id: String,
                                                                 field_name: String
                                                               )

