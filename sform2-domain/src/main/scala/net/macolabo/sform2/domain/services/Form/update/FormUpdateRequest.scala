package net.macolabo.sform2.domain.services.Form.update

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import net.macolabo.sform2.utils.StringUtils.StringImprovements

/**
 * フォーム更新API・フォームデータ
 *
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


trait FormUpdateRequestJson {
  implicit val FormTransferTaskSalesforceFieldUpdateRequestWrites: Writes[FormTransferTaskSalesforceFieldUpdateRequest] = (formTransferTaskSalesforceFieldUpdateRequest:FormTransferTaskSalesforceFieldUpdateRequest) => Json.obj(
    "id" -> formTransferTaskSalesforceFieldUpdateRequest.id,
    "form_transfer_task_salesforce_id" -> formTransferTaskSalesforceFieldUpdateRequest.form_transfer_task_salesforce_id,
    "form_column_id" -> formTransferTaskSalesforceFieldUpdateRequest.form_column_id,
    "field_name" -> formTransferTaskSalesforceFieldUpdateRequest.field_name
  )

  implicit val FormTransferTaskSalesforceFieldUpdateRequestReads: Reads[FormTransferTaskSalesforceFieldUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_salesforce_id").readNullable[BigInt] ~
      (JsPath \ "form_column_id").read[String] ~
      (JsPath \ "field_name").read[String]
    )(FormTransferTaskSalesforceFieldUpdateRequest.apply _)

  implicit val FormTransferTaskConditionUpdateRequestWrites: Writes[FormTransferTaskConditionUpdateRequest] = (formTransferTaskConditionUpdateRequest:FormTransferTaskConditionUpdateRequest) => Json.obj(
    "id" -> formTransferTaskConditionUpdateRequest.id,
    "form_transfer_task_id" -> formTransferTaskConditionUpdateRequest.form_transfer_task_id,
    "form_id" -> formTransferTaskConditionUpdateRequest.form_id,
    "form_col_id" -> formTransferTaskConditionUpdateRequest.form_col_id,
    "operator" -> formTransferTaskConditionUpdateRequest.operator,
    "cond_value" -> formTransferTaskConditionUpdateRequest.cond_value
  )

  implicit val FormTransferTaskConditionUpdateFormRequestReads: Reads[FormTransferTaskConditionUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").readNullable[BigInt] ~
      (JsPath \ "form_id").readNullable[BigInt] ~
      (JsPath \ "form_col_id").read[BigInt] ~
      (JsPath \ "operator").read[String] ~
      (JsPath \ "cond_value").read[String]
    )(FormTransferTaskConditionUpdateRequest.apply _)

  implicit val FormTransferTaskMailUpdateRequestWrites: Writes[FormTransferTaskMailUpdateRequest] = (formTransferTaskMailUpdateRequest:FormTransferTaskMailUpdateRequest) => Json.obj(
    "id" -> formTransferTaskMailUpdateRequest.id,
    "form_transfer_task_id" -> formTransferTaskMailUpdateRequest.form_transfer_task_id,
    "from_address_id" -> formTransferTaskMailUpdateRequest.from_address_id,
    "to_address" -> formTransferTaskMailUpdateRequest.to_address,
    "cc_address" -> formTransferTaskMailUpdateRequest.cc_address,
    "bcc_address_id" -> formTransferTaskMailUpdateRequest.bcc_address_id,
    "replyto_address_id" -> formTransferTaskMailUpdateRequest.replyto_address_id,
    "subject" -> formTransferTaskMailUpdateRequest.subject,
    "body" -> formTransferTaskMailUpdateRequest.body
  )

  implicit val FormTransferTaskMailUpdateRequestReads: Reads[FormTransferTaskMailUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").readNullable[BigInt] ~
      (JsPath \ "from_address_id").read[BigInt] ~
      (JsPath \ "to_address").read[String] ~
      (JsPath \ "cc_address").readNullable[String] ~
      (JsPath \ "bcc_address_id").readNullable[BigInt] ~
      (JsPath \ "replyto_address_id").readNullable[BigInt] ~
      (JsPath \ "subject").read[String] ~
      (JsPath \ "body").read[String]
    )(FormTransferTaskMailUpdateRequest.apply _)

  implicit val FormTransferTaskSalesforceUpdateRequestWrites: Writes[FormTransferTaskSalesforceUpdateRequest] = (formTransferTaskSalesforceUpdateRequest:FormTransferTaskSalesforceUpdateRequest) => Json.obj(
    "id" -> formTransferTaskSalesforceUpdateRequest.id,
    "form_transfer_task_id" -> formTransferTaskSalesforceUpdateRequest.form_transfer_task_id,
    "object_name" -> formTransferTaskSalesforceUpdateRequest.object_name,
    "fields" -> formTransferTaskSalesforceUpdateRequest.fields
  )

  implicit val FormTransferTaskSalesforceUpdateRequestReads: Reads[FormTransferTaskSalesforceUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_transfer_task_id").readNullable[BigInt] ~
      (JsPath \ "object_name").read[String] ~
      (JsPath \ "fields").read[List[FormTransferTaskSalesforceFieldUpdateRequest]]
    )(FormTransferTaskSalesforceUpdateRequest.apply _)

  implicit val FormTransferTaskUpdateRequestWrites: Writes[FormTransferTaskUpdateRequest] = (formTransferTaskUpdateRequest:FormTransferTaskUpdateRequest) => Json.obj(
    "id" -> formTransferTaskUpdateRequest.id,
    "transfer_config_id" -> formTransferTaskUpdateRequest.transfer_config_id,
    "form_id" -> formTransferTaskUpdateRequest.form_id,
    "task_index" -> formTransferTaskUpdateRequest.task_index,
    "name" -> formTransferTaskUpdateRequest.name,
    "form_transfer_task_conditions" -> formTransferTaskUpdateRequest.form_transfer_task_conditions,
    "mail" -> formTransferTaskUpdateRequest.mail,
    "salesforce" -> formTransferTaskUpdateRequest.salesforce
  )

  implicit val FormTransferTaskUpdateRequestReads: Reads[FormTransferTaskUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "task_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_transfer_task_conditions").read[List[FormTransferTaskConditionUpdateRequest]] ~
      (JsPath \ "mail").readNullable[FormTransferTaskMailUpdateRequest] ~
      (JsPath \ "salesforce").readNullable[FormTransferTaskSalesforceUpdateRequest]
    )(FormTransferTaskUpdateRequest.apply _)

  implicit val FormColValidationUpdateRequestWrites: Writes[FormColValidationUpdateRequest] = (formColValidationUpdateRequest: FormColValidationUpdateRequest) => Json.obj(
    "id" -> formColValidationUpdateRequest.id,
    "form_col_id" -> formColValidationUpdateRequest.form_col_id,
    "form_id" -> formColValidationUpdateRequest.form_id,
    "max_value" -> formColValidationUpdateRequest.max_value,
    "min_value" -> formColValidationUpdateRequest.min_value,
    "max_length" -> formColValidationUpdateRequest.max_length,
    "min_length" -> formColValidationUpdateRequest.min_length,
    "input_type" -> formColValidationUpdateRequest.input_type
  )

  implicit val FormColValidationUpdateRequestReads: Reads[FormColValidationUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_col_id").readNullable[BigInt] ~
      (JsPath \ "form_id").readNullable[BigInt] ~
      (JsPath \ "max_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_value").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "max_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "min_length").readNullable[String].map[Option[Int]](os => os.flatMap(s => s.toIntOpt)) ~
      (JsPath \ "input_type").read[Int] ~
      (JsPath \ "required").read[Boolean]
    )(FormColValidationUpdateRequest.apply _)

  implicit val FormColSelectListUpdateRequestWrites: Writes[FormColSelectUpdateRequest] = (formColSelectUpdateRequest: FormColSelectUpdateRequest) => Json.obj(
    "id" -> formColSelectUpdateRequest.id,
    "form_col_id" -> formColSelectUpdateRequest.form_col_id,
    "form_id" -> formColSelectUpdateRequest.form_id,
    "select_index" -> formColSelectUpdateRequest.select_index,
    "select_name" -> formColSelectUpdateRequest.select_name,
    "select_value" -> formColSelectUpdateRequest.select_value,
    "is_default" -> formColSelectUpdateRequest.is_default,
    "edit_style" -> formColSelectUpdateRequest.edit_style,
    "view_style" -> formColSelectUpdateRequest.view_style
  )

  implicit val FormColSelectUpdateRequestReads: Reads[FormColSelectUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_col_id").readNullable[BigInt] ~
      (JsPath \ "form_id").readNullable[BigInt] ~
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormColSelectUpdateRequest.apply _)

  implicit val FormColUpdateRequestWrites: Writes[FormColUpdateRequest] = (formColUpdateRequest: FormColUpdateRequest) => Json.obj(
    "id" -> formColUpdateRequest.id,
    "form_id" -> formColUpdateRequest.form_id,
    "name" -> formColUpdateRequest.name,
    "col_id" -> formColUpdateRequest.col_id,
    "col_index" -> formColUpdateRequest.col_index,
    "col_type" -> formColUpdateRequest.col_type,
    "default_value" -> formColUpdateRequest.default_value,
    "select_list" -> formColUpdateRequest.select_list,
    "validations" -> formColUpdateRequest.validations
  )

  implicit val FormColUpdateRequestReads: Reads[FormColUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "form_id").readNullable[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormColSelectUpdateRequest]] ~
      (JsPath \ "validations").read[FormColValidationUpdateRequest]
    )(FormColUpdateRequest.apply _)

  implicit val FormUpdateRequestWrites: Writes[FormUpdateRequest] = (formUpdateRequest: FormUpdateRequest) => Json.obj(
    "id" -> formUpdateRequest.id,
    "name" -> formUpdateRequest.name,
    "form_index" -> formUpdateRequest.form_index,
    "title" -> formUpdateRequest.title,
    "status" -> formUpdateRequest.status,
    "cancel_url" -> formUpdateRequest.cancel_url,
    "close_text" -> formUpdateRequest.close_text,
    "hashed_id" -> formUpdateRequest.hashed_id,
    "complete_url" -> formUpdateRequest.complete_url,
    "input_header" -> formUpdateRequest.input_header,
    "complete_text" -> formUpdateRequest.complete_text,
    "confirm_header" -> formUpdateRequest.confirm_header,
    "form_cols" -> formUpdateRequest.form_cols
  )

  implicit val FormUpdateRequestReads: Reads[FormUpdateRequest] = (
    (JsPath \ "id").readNullable[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "cancel_url").read[String] ~
      (JsPath \ "close_text").read[String] ~
      (JsPath \ "hashed_id").read[String] ~
      (JsPath \ "complete_url").read[String] ~
      (JsPath \ "input_header").read[String] ~
      (JsPath \ "complete_text").read[String] ~
      (JsPath \ "confirm_header").read[String] ~
      (JsPath \ "form_cols").read[List[FormColUpdateRequest]] ~
      (JsPath \ "form_transfer_tasks").read[List[FormTransferTaskUpdateRequest]]
    )(FormUpdateRequest.apply _)
}
