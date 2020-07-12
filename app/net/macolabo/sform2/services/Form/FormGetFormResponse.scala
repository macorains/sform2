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
  */
case class FormGetFormResponseFormColValidation(
                                               id: Int,
                                               form_col_id: Int,
                                               form_id: Int,
                                               max_value: Int,
                                               min_value: Int,
                                               max_length: Int,
                                               min_length: Int,
                                               input_type: Int
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
                                            form_transfer_task_conditions: List[FormGetFormResponseFormTransferTaskCondition]
                                          )

/**i
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
                                                         cond_value: String,
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
                              transfer_tasks: List[FormGetFormResponseFormTransferTask]
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
    "input_type" -> formGetFormResponseFormColValidation.input_type
  )

  implicit val FormGetFormResponseFormColValidationReads: Reads[FormGetFormResponseFormColValidation] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "max_value").read[Int] ~
      (JsPath \ "min_value").read[Int] ~
      (JsPath \ "max_length").read[Int] ~
      (JsPath \ "min_length").read[Int] ~
      (JsPath \ "input_type").read[Int]
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
    "form_transfer_task_conditions" -> formGetFormResponseFormTransferTask.form_transfer_task_conditions
  )

  implicit val formGetFormResponseFormTransferTaskReads: Reads[FormGetFormResponseFormTransferTask] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "transfer_config_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "task_index").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "form_transfer_task_condition").read[List[FormGetFormResponseFormTransferTaskCondition]]
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
    "transfer_tasks" -> formGetFormResponse.transfer_tasks
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
      (JsPath \ "transfer_tasks").read[List[FormGetFormResponseFormTransferTask]]
  )(FormGetFormResponse.apply _)
}