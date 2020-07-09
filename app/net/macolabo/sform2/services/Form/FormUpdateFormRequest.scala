package net.macolabo.sform2.services.Form

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

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
case class FormUpdateFormRequestFormColValidation(
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
case class FormUpdateFormRequestFormColSelect(
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
case class FormUpdateFormRequestFormCol(
                                       id: Int,
                                       form_id: Int,
                                       name: String,
                                       col_id: String,
                                       col_index: Int,
                                       col_type: Int,
                                       default_value: String,
                                       select_list: List[FormUpdateFormRequestFormColSelect],
                                       validations: Option[FormUpdateFormRequestFormColValidation]
                                     )

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
case class FormUpdateFormRequest(
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
                                form_cols: List[FormUpdateFormRequestFormCol]
                              ) {

}

trait FormUpdateFormRequestJson {
  implicit val FormUpdateFormRequestFormColValidationWrites: Writes[FormUpdateFormRequestFormColValidation] = (formUpdateFormRequestFormColValidation: FormUpdateFormRequestFormColValidation) => Json.obj(
    "id" -> formUpdateFormRequestFormColValidation.id,
    "form_col_id" -> formUpdateFormRequestFormColValidation.form_col_id,
    "form_id" -> formUpdateFormRequestFormColValidation.form_id,
    "max_value" -> formUpdateFormRequestFormColValidation.max_value,
    "min_value" -> formUpdateFormRequestFormColValidation.min_value,
    "max_length" -> formUpdateFormRequestFormColValidation.max_length,
    "min_length" -> formUpdateFormRequestFormColValidation.min_length,
    "input_type" -> formUpdateFormRequestFormColValidation.input_type
  )

  implicit val FormUpdateFormRequestFormColValidationReads: Reads[FormUpdateFormRequestFormColValidation] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "max_value").read[Int] ~
      (JsPath \ "min_value").read[Int] ~
      (JsPath \ "max_length").read[Int] ~
      (JsPath \ "min_length").read[Int] ~
      (JsPath \ "input_type").read[Int]
    )(FormUpdateFormRequestFormColValidation.apply _)

  implicit val FormUpdateFormRequestFormColSelectListWrites: Writes[FormUpdateFormRequestFormColSelect] = (FormUpdateFormRequestFormColSelect: FormUpdateFormRequestFormColSelect) => Json.obj(
    "id" -> FormUpdateFormRequestFormColSelect.id,
    "form_col_id" -> FormUpdateFormRequestFormColSelect.form_col_id,
    "form_id" -> FormUpdateFormRequestFormColSelect.form_id,
    "select_index" -> FormUpdateFormRequestFormColSelect.select_index,
    "select_name" -> FormUpdateFormRequestFormColSelect.select_name,
    "select_value" -> FormUpdateFormRequestFormColSelect.select_value,
    "is_default" -> FormUpdateFormRequestFormColSelect.is_default,
    "edit_style" -> FormUpdateFormRequestFormColSelect.edit_style,
    "view_style" -> FormUpdateFormRequestFormColSelect.view_style
  )

  implicit val FormUpdateFormRequestFormColSelectListReads: Reads[FormUpdateFormRequestFormColSelect] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_col_id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormUpdateFormRequestFormColSelect.apply _)

  implicit val FormUpdateFormRequestFormColWrites: Writes[FormUpdateFormRequestFormCol] = (formUpdateFormRequestFormCol: FormUpdateFormRequestFormCol) => Json.obj(
    "id" -> formUpdateFormRequestFormCol.id,
    "form_id" -> formUpdateFormRequestFormCol.form_id,
    "name" -> formUpdateFormRequestFormCol.name,
    "col_id" -> formUpdateFormRequestFormCol.col_id,
    "col_index" -> formUpdateFormRequestFormCol.col_index,
    "col_type" -> formUpdateFormRequestFormCol.col_type,
    "default_value" -> formUpdateFormRequestFormCol.default_value,
    "select_list" -> formUpdateFormRequestFormCol.select_list,
    "validations" -> formUpdateFormRequestFormCol.validations
  )

  implicit val FormUpdateFormRequestFormColReads: Reads[FormUpdateFormRequestFormCol] = (
    (JsPath \ "id").read[Int] ~
      (JsPath \ "form_id").read[Int] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormUpdateFormRequestFormColSelect]] ~
      (JsPath \ "validations").readNullable[FormUpdateFormRequestFormColValidation]
    )(FormUpdateFormRequestFormCol.apply _)

  implicit val FormUpdateFormRequestWrites: Writes[FormUpdateFormRequest] = (formUpdateFormRequest: FormUpdateFormRequest) => Json.obj(
    "id" -> formUpdateFormRequest.id,
    "name" -> formUpdateFormRequest.name,
    "form_index" -> formUpdateFormRequest.form_index,
    "title" -> formUpdateFormRequest.title,
    "status" -> formUpdateFormRequest.status,
    "cancel_url" -> formUpdateFormRequest.cancel_url,
    "close_text" -> formUpdateFormRequest.close_text,
    "hashed_id" -> formUpdateFormRequest.hashed_id,
    "complete_url" -> formUpdateFormRequest.complete_url,
    "input_header" -> formUpdateFormRequest.input_header,
    "complete_text" -> formUpdateFormRequest.complete_text,
    "confirm_header" -> formUpdateFormRequest.confirm_header,
    "form_cols" -> formUpdateFormRequest.form_cols
  )

  implicit val FormUpdateFormRequestReads: Reads[FormUpdateFormRequest] = (
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
      (JsPath \ "form_cols").read[List[FormUpdateFormRequestFormCol]]
    )(FormUpdateFormRequest.apply _)
}