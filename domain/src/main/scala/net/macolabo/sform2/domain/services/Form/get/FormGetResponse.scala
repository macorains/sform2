package net.macolabo.sform2.domain.services.Form.get

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

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
                              ) {

}

trait FormGetResponseJson {
  implicit val FormGetResponseFormColValidationWrites: Writes[FormColValidationGetReponse] = (formColValidation: FormColValidationGetReponse) => Json.obj(
    "id" -> formColValidation.id,
    "form_col_id" -> formColValidation.form_col_id,
    "form_id" -> formColValidation.form_id,
    "max_value" -> JsString(formColValidation.max_value.map(v=>v.toString).getOrElse("")),
    "min_value" -> JsString(formColValidation.min_value.map(v=>v.toString).getOrElse("")),
    "max_length" -> JsString(formColValidation.max_length.map(v=>v.toString).getOrElse("")),
    "min_length" -> JsString(formColValidation.min_length.map(v=>v.toString).getOrElse("")),
    "input_type" -> formColValidation.input_type,
    "required" -> formColValidation.required
  )

  implicit val FormColValidationReads: Reads[FormColValidationGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "form_col_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "max_value").readNullable[Int] ~
      (JsPath \ "min_value").readNullable[Int] ~
      (JsPath \ "max_length").readNullable[Int] ~
      (JsPath \ "min_length").readNullable[Int] ~
      (JsPath \ "input_type").read[Int] ~
      (JsPath \ "required").read[Boolean]
    )(FormColValidationGetReponse.apply _)

  implicit val FormColSelectListWrites: Writes[FormColSelectGetReponse] = (formColSelectList: FormColSelectGetReponse) => Json.obj(
    "id" -> formColSelectList.id,
    "form_col_id" -> formColSelectList.form_col_id,
    "form_id" -> formColSelectList.form_id,
    "select_index" -> formColSelectList.select_index,
    "select_name" -> formColSelectList.select_name,
    "select_value" -> formColSelectList.select_value,
    "is_default" -> formColSelectList.is_default,
    "edit_style" -> formColSelectList.edit_style,
    "view_style" -> formColSelectList.view_style
  )

  implicit val FormColSelectListReads: Reads[FormColSelectGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "form_col_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormColSelectGetReponse.apply _)

  implicit val FormTransferTaskWrites: Writes[FormTransferTaskGetResponse] = (formTransferTask: FormTransferTaskGetResponse) => Json.obj(
    "id" -> formTransferTask.id,
    "transfer_config_id" -> formTransferTask.transfer_config_id,
    "transfer_config_name" -> formTransferTask.transfer_config_name,
    "form_id" -> formTransferTask.form_id,
    "task_index" -> formTransferTask.task_index,
    "name" -> formTransferTask.name,
    "form_transfer_task_conditions" -> formTransferTask.form_transfer_task_conditions,
    "mail" -> formTransferTask.mail,
    "salesforce" -> formTransferTask.salesforce
  )

  implicit val FormTransferTaskReads: Reads[FormTransferTaskGetResponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_config_id").read[BigInt] ~
      (JsPath \ "transfer_config_name").read[String] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "task_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_transfer_task_condition").read[List[FormTransferTaskConditionGetReponse]] ~
      (JsPath \ "mail").readNullable[FormTransferTaskMailGetReponse] ~
      (JsPath \ "salesforce").readNullable[FormTransferTaskSalesforceGetReponse]
    )(FormTransferTaskGetResponse.apply _)

  implicit val FormTransferTaskConditionWrites: Writes[FormTransferTaskConditionGetReponse] = (formTransferTaskCondition: FormTransferTaskConditionGetReponse) => Json.obj(
    "id" -> formTransferTaskCondition.id,
    "form_transfer_task_id" -> formTransferTaskCondition.form_transfer_task_id,
    "form_id" -> formTransferTaskCondition.form_id,
    "form_col_id" -> formTransferTaskCondition.form_col_id,
    "operator" -> formTransferTaskCondition.operator,
    "cond_value" -> formTransferTaskCondition.cond_value
  )

  implicit val FormTransferTaskConditionReads: Reads[FormTransferTaskConditionGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "form_transfer_task_id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "form_col_id").read[BigInt] ~
      (JsPath \ "operator").read[String] ~
      (JsPath \ "cond_value").read[String]
    )(FormTransferTaskConditionGetReponse.apply _)

  implicit val FormTransferTaskMailFormat: Format[FormTransferTaskMailGetReponse] = (
    (JsPath \ "id").format[BigInt] ~
      (JsPath \ "form_transfer_task_id").format[BigInt] ~
      (JsPath \ "from_address_id").format[BigInt] ~
      (JsPath \ "to_address").formatNullable[String] ~
      (JsPath \ "to_address_id").formatNullable[BigInt] ~
      (JsPath \ "to_address_field").formatNullable[String] ~
      (JsPath \ "cc_address").formatNullable[String] ~
      (JsPath \ "cc_address_id").formatNullable[BigInt] ~
      (JsPath \ "cc_address_field").formatNullable[String] ~
      (JsPath \ "bcc_address_id").formatNullable[BigInt] ~
      (JsPath \ "replyto_address_id").formatNullable[BigInt] ~
      (JsPath \ "subject").format[String] ~
      (JsPath \ "body").format[String]
    )(FormTransferTaskMailGetReponse.apply, unlift(FormTransferTaskMailGetReponse.unapply))

  implicit val FormTransferTaskSalesforceFieldWrites: Writes[FormTransferTaskSalesforceFieldGetReponse]
  = (formTransferTaskSalesforceField:FormTransferTaskSalesforceFieldGetReponse) => Json.obj(
    "id" -> formTransferTaskSalesforceField.id,
    "form_ransfer_task_salesforce_id" -> formTransferTaskSalesforceField.form_transfer_task_salesforce_id,
    "form_column_id" -> formTransferTaskSalesforceField.form_column_id,
    "field_name" -> formTransferTaskSalesforceField.field_name
  )

  implicit val FormTransferTaskSalesforceFieldReads: Reads[FormTransferTaskSalesforceFieldGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "transfer_task_salesforce_id").read[BigInt] ~
      (JsPath \ "form_column_id").read[String] ~
      (JsPath \ "field_name").read[String]
    )(FormTransferTaskSalesforceFieldGetReponse.apply _)

  implicit val FormTransferTaskSalesforceWrites: Writes[FormTransferTaskSalesforceGetReponse] = (formTransferTaskSalesforce:FormTransferTaskSalesforceGetReponse) => Json.obj(
    "id" -> formTransferTaskSalesforce.id,
    "form_transfer_task_id" -> formTransferTaskSalesforce.form_transfer_task_id,
    "object_name" -> formTransferTaskSalesforce.object_name,
    "fields" -> formTransferTaskSalesforce.fields
  )

  implicit val FormTransferTaskSalesforceReads: Reads[FormTransferTaskSalesforceGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "form_transfer_task_id").read[BigInt] ~
      (JsPath \ "object_name").read[String] ~
      (JsPath \ "fields").read[List[FormTransferTaskSalesforceFieldGetReponse]]
    )(FormTransferTaskSalesforceGetReponse.apply _)

  implicit val FormColWrites: Writes[FormColGetReponse] = (formCol: FormColGetReponse) => Json.obj(
    "id" -> formCol.id,
    "form_id" -> formCol.form_id,
    "name" -> formCol.name,
    "col_id" -> formCol.col_id,
    "col_index" -> formCol.col_index,
    "col_type" -> formCol.col_type,
    "default_value" -> formCol.default_value,
    "select_list" -> formCol.select_list,
    "validations" -> formCol.validations
  )

  implicit val FormColReads: Reads[FormColGetReponse] = (
    (JsPath \ "id").read[BigInt] ~
      (JsPath \ "form_id").read[BigInt] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormColSelectGetReponse]] ~
      (JsPath \ "validations").readNullable[FormColValidationGetReponse]
    )(FormColGetReponse.apply _)

  implicit val FormGetResponseWrites: Writes[FormGetResponse] = (formGetFormResponse: FormGetResponse) => Json.obj(
    "id" -> formGetFormResponse.id,
    "name" -> formGetFormResponse.name,
    "form_index" -> formGetFormResponse.form_index,
    "title" -> formGetFormResponse.title,
    "status" -> formGetFormResponse.status,
    "cancel_url" -> formGetFormResponse.cancel_url,
    "close_text" -> formGetFormResponse.close_text,
    "hashed_id" -> formGetFormResponse.hashed_id,
    "complete_url" -> formGetFormResponse.complete_url,
    "input_header" -> formGetFormResponse.input_header,
    "complete_text" -> formGetFormResponse.complete_text,
    "confirm_header" -> formGetFormResponse.confirm_header,
    "form_cols" -> formGetFormResponse.form_cols,
    "form_transfer_tasks" -> formGetFormResponse.form_transfer_tasks
  )

  implicit val FormGetResponseReads: Reads[FormGetResponse] = (
    (JsPath \ "id").read[BigInt] ~
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
      (JsPath \ "form_cols").read[List[FormColGetReponse]] ~
      (JsPath \ "form_transfer_tasks").read[List[FormTransferTaskGetResponse]]
    )(FormGetResponse.apply _)
}
