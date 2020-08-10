package net.macolabo.sform2.services.Form
import java.time.ZonedDateTime

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
case class FormGetFormResponseFormColValidation(
                                               id: Int,
                                               form_col_id: Int,
                                               form_id: Int,
                                               max_value: Int,
                                               min_value: Int,
                                               max_length: Int,
                                               min_length: Int,
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
case class FormGetFormResponseFormColSelect(
                                               id: Int,
                                               form_col_id: Int,
                                               form_id: Int,
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
 * @param form_id フォームID
 * @param task_index 順番
 * @param form_transfer_task_conditions FormTransferTaskConditionのリスト
 */
case class FormGetFormResponseFormTransferTask(
                                            id: Int,
                                            transfer_config_id: Int,
                                            form_id: Int,
                                            task_index: Int,
                                            name: String,
                                            form_transfer_task_conditions: List[FormGetFormResponseFormTransferTaskCondition],
                                            mail: Option[FormGetFormResponseFormTransferTaskMail],
                                            salesforce: Option[FormGetFormResponseFormTransferTaskSalesforce]
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
case class FormGetFormResponseFormTransferTaskCondition(
                                                         id: Int,
                                                         form_transfer_task_id: Int,
                                                         form_id: Int,
                                                         form_col_id: Int,
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
case class FormGetFormResponseFormTransferTaskMail(
                                                    id: Int,
                                                    form_transfer_task_id: Int,
                                                    from_address_id: Int,
                                                    to_address: String,
                                                    cc_address: String,
                                                    bcc_address_id: Int,
                                                    replyto_address_id: Int,
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
case class FormGetFormResponseFormTransferTaskSalesforce(
                                                          id: Int,
                                                          form_transfer_task_id: Int,
                                                          object_name: String,
                                                          fields: List[FormGetFormResponseFormTransferTaskSalesforceField]
                                                        )

/**
 * フォーム取得API・FormTransferTask・FormTransferTaskSalesforceField
 * @param id FormTransferTaskSalesforceField ID
 * @param form_transfer_task_salesforce_id FormTransferTaskSalesforce ID
 * @param form_column_id フォーム項目ID
 * @param field_name Salesforceフィールド名
 */
case class FormGetFormResponseFormTransferTaskSalesforceField(
                                                               id: Int,
                                                               form_transfer_task_salesforce_id: Int,
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
case class FormGetFormResponseFormCol(
                                     id: Int,
                                     form_id: Int,
                                     name: String,
                                     col_id: String,
                                     col_index: Int,
                                     col_type: Int,
                                     default_value: String,
                                     select_list: List[FormGetFormResponseFormColSelect],
                                     validations: Option[FormGetFormResponseFormColValidation]
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
case class FormGetFormResponse(
                              id: Int,
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
                              form_cols: List[FormGetFormResponseFormCol],
                              form_transfer_tasks: List[FormGetFormResponseFormTransferTask]
                              ) {

}

trait FormGetFormResponseJson {
  implicit val FormGetFormResponseFormColValidationWrites: Writes[FormGetFormResponseFormColValidation] = (formGetFormResponseFormColValidation: FormGetFormResponseFormColValidation) => Json.obj(
    "id" -> formGetFormResponseFormColValidation.id,
    "form_col_id" -> formGetFormResponseFormColValidation.form_col_id,
    "form_id" -> formGetFormResponseFormColValidation.form_id,
    "max_value" -> formGetFormResponseFormColValidation.max_value,
    "min_value" -> formGetFormResponseFormColValidation.min_value,
    "max_length" -> formGetFormResponseFormColValidation.max_length,
    "min_length" -> formGetFormResponseFormColValidation.min_length,
    "input_type" -> formGetFormResponseFormColValidation.input_type,
    "required" -> formGetFormResponseFormColValidation.required
  )

  implicit val FormGetFormResponseFormColValidationReads: Reads[FormGetFormResponseFormColValidation] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "max_value").read[Int] ~
      (JsPath \ "min_value").read[Int] ~
      (JsPath \ "max_length").read[Int] ~
      (JsPath \ "min_length").read[Int] ~
      (JsPath \ "input_type").read[Int] ~
      (JsPath \ "required").read[Boolean]
    )(FormGetFormResponseFormColValidation.apply _)

  implicit val FormGetFormResponseFormColSelectListWrites: Writes[FormGetFormResponseFormColSelect] = (formGetFormResponseFormColSelectList: FormGetFormResponseFormColSelect) => Json.obj(
    "id" -> formGetFormResponseFormColSelectList.id,
    "form_col_id" -> formGetFormResponseFormColSelectList.form_col_id,
    "form_id" -> formGetFormResponseFormColSelectList.form_id,
    "select_index" -> formGetFormResponseFormColSelectList.select_index,
    "select_name" -> formGetFormResponseFormColSelectList.select_name,
    "select_value" -> formGetFormResponseFormColSelectList.select_value,
    "is_default" -> formGetFormResponseFormColSelectList.is_default,
    "edit_style" -> formGetFormResponseFormColSelectList.edit_style,
    "view_style" -> formGetFormResponseFormColSelectList.view_style
  )

  implicit val FormGetFormResponseFormColSelectListReads: Reads[FormGetFormResponseFormColSelect] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
  )(FormGetFormResponseFormColSelect.apply _)

  implicit val formGetFormResponseFormTransferTaskWrites: Writes[FormGetFormResponseFormTransferTask] = (formGetFormResponseFormTransferTask: FormGetFormResponseFormTransferTask) => Json.obj(
    "id" -> formGetFormResponseFormTransferTask.id,
    "transfer_config_id" -> formGetFormResponseFormTransferTask.transfer_config_id,
    "form_id" -> formGetFormResponseFormTransferTask.form_id,
    "task_index" -> formGetFormResponseFormTransferTask.task_index,
    "name" -> formGetFormResponseFormTransferTask.name,
    "form_transfer_task_conditions" -> formGetFormResponseFormTransferTask.form_transfer_task_conditions,
    "mail" -> formGetFormResponseFormTransferTask.mail,
    "salesforce" -> formGetFormResponseFormTransferTask.salesforce
  )

  implicit val formGetFormResponseFormTransferTaskReads: Reads[FormGetFormResponseFormTransferTask] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "transfer_config_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "task_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_transfer_task_condition").read[List[FormGetFormResponseFormTransferTaskCondition]] ~
      (JsPath \ "mail").readNullable[FormGetFormResponseFormTransferTaskMail] ~
      (JsPath \ "salesforce").readNullable[FormGetFormResponseFormTransferTaskSalesforce]
  )(FormGetFormResponseFormTransferTask.apply _)

  implicit val formGetFormResponseFormTransferTaskConditionWrites: Writes[FormGetFormResponseFormTransferTaskCondition] = (formGetFormResponseFormTransferTaskCondition: FormGetFormResponseFormTransferTaskCondition) => Json.obj(
    "id" -> formGetFormResponseFormTransferTaskCondition.id,
    "form_transfer_task_id" -> formGetFormResponseFormTransferTaskCondition.form_transfer_task_id,
    "form_id" -> formGetFormResponseFormTransferTaskCondition.form_id,
    "form_col_id" -> formGetFormResponseFormTransferTaskCondition.form_col_id,
    "operator" -> formGetFormResponseFormTransferTaskCondition.operator,
    "cond_value" -> formGetFormResponseFormTransferTaskCondition.cond_value
  )

  implicit val formGetFormResponseFormTransferTaskConditionReads: Reads[FormGetFormResponseFormTransferTaskCondition] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_transfer_task_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "operator").read[String] ~
      (JsPath \ "cond_value").read[String]
  )(FormGetFormResponseFormTransferTaskCondition.apply _)

  implicit val FormGetFormResponseFormTransferTaskMailWrites: Writes[FormGetFormResponseFormTransferTaskMail] = (formGetFormResponseFormTransferTaskMail:FormGetFormResponseFormTransferTaskMail) => Json.obj(
    "id" -> formGetFormResponseFormTransferTaskMail.id,
    "form_transfer_task_id" -> formGetFormResponseFormTransferTaskMail.form_transfer_task_id,
    "from_address_id" -> formGetFormResponseFormTransferTaskMail.from_address_id,
    "to_address" -> formGetFormResponseFormTransferTaskMail.to_address,
    "cc_address" -> formGetFormResponseFormTransferTaskMail.cc_address,
    "bcc_address_id" -> formGetFormResponseFormTransferTaskMail.bcc_address_id,
    "replyto_address_id" -> formGetFormResponseFormTransferTaskMail.replyto_address_id,
    "subject" -> formGetFormResponseFormTransferTaskMail.subject,
    "body" -> formGetFormResponseFormTransferTaskMail.body
  )

  implicit val FormGetFormResponseFormTransferTaskMailReads: Reads[FormGetFormResponseFormTransferTaskMail] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_transfer_task_id").read[Int] ~
      (JsPath \ "from_address_id").read[Int] ~
      (JsPath \ "to_address").read[String] ~
      (JsPath \ "cc_address").read[String] ~
      (JsPath \ "bcc_address_id").read[Int] ~
      (JsPath \ "replyto_address_id").read[Int] ~
      (JsPath \ "subject").read[String] ~
      (JsPath \ "body").read[String]
  )(FormGetFormResponseFormTransferTaskMail.apply _)

  implicit val FormGetFormResponseFormTransferTaskSalesforceFieldWrites: Writes[FormGetFormResponseFormTransferTaskSalesforceField]
  = (formGetFormResponseFormTransferTaskSalesforceField:FormGetFormResponseFormTransferTaskSalesforceField) => Json.obj(
    "id" -> formGetFormResponseFormTransferTaskSalesforceField.id,
    "form_transfer_task_salesforce_id" -> formGetFormResponseFormTransferTaskSalesforceField.form_transfer_task_salesforce_id,
    "form_column_id" -> formGetFormResponseFormTransferTaskSalesforceField.form_column_id,
    "field_name" -> formGetFormResponseFormTransferTaskSalesforceField.field_name
  )

  implicit val FormGetFormResponseFormTransferTaskSalesforceFieldReads: Reads[FormGetFormResponseFormTransferTaskSalesforceField] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "transfer_task_salesforce_id").read[Int] ~
      (JsPath \ "form_column_id").read[String] ~
      (JsPath \ "field_name").read[String]
  )(FormGetFormResponseFormTransferTaskSalesforceField.apply _)

  implicit val FormGetFormResponseFormTransferTaskSalesforceWrites: Writes[FormGetFormResponseFormTransferTaskSalesforce] = (formGetFormResponseFormTransferTaskSalesforce:FormGetFormResponseFormTransferTaskSalesforce) => Json.obj(
    "id" -> formGetFormResponseFormTransferTaskSalesforce.id,
    "form_transfer_task_id" -> formGetFormResponseFormTransferTaskSalesforce.form_transfer_task_id,
    "object_name" -> formGetFormResponseFormTransferTaskSalesforce.object_name,
    "fields" -> formGetFormResponseFormTransferTaskSalesforce.fields
  )

  implicit val FormGetFormResponseFormTransferTaskSalesforceReads: Reads[FormGetFormResponseFormTransferTaskSalesforce] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_transfer_task_id").read[Int] ~
      (JsPath \ "object_name").read[String] ~
      (JsPath \ "fields").read[List[FormGetFormResponseFormTransferTaskSalesforceField]]
  )(FormGetFormResponseFormTransferTaskSalesforce.apply _)

  implicit val FormGetFormResponseFormColWrites: Writes[FormGetFormResponseFormCol] = (formGetFormResponseFormCol: FormGetFormResponseFormCol) => Json.obj(
    "id" -> formGetFormResponseFormCol.id,
    "form_id" -> formGetFormResponseFormCol.form_id,
    "name" -> formGetFormResponseFormCol.name,
    "col_id" -> formGetFormResponseFormCol.col_id,
    "col_index" -> formGetFormResponseFormCol.col_index,
    "col_type" -> formGetFormResponseFormCol.col_type,
    "default_value" -> formGetFormResponseFormCol.default_value,
    "select_list" -> formGetFormResponseFormCol.select_list,
    "validations" -> formGetFormResponseFormCol.validations
  )

  implicit val FormGetFormResponseFormColReads: Reads[FormGetFormResponseFormCol] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormGetFormResponseFormColSelect]] ~
      (JsPath \ "validations").readNullable[FormGetFormResponseFormColValidation]
  )(FormGetFormResponseFormCol.apply _)

  implicit val FormGetFormResponseWrites: Writes[FormGetFormResponse] = (formGetFormResponse: FormGetFormResponse) => Json.obj(
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

  implicit val FormGetFormResponseReads: Reads[FormGetFormResponse] = (
    (JsPath \ "id").read[Int] ~
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
      (JsPath \ "form_cols").read[List[FormGetFormResponseFormCol]] ~
      (JsPath \ "form_transfer_tasks").read[List[FormGetFormResponseFormTransferTask]]
  )(FormGetFormResponse.apply _)
}