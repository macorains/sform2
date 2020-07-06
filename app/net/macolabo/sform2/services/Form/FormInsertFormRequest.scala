package net.macolabo.sform2.services.Form
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * フォーム作成API・フォーム項目・バリデーション
 * @param max_value 最大値
 * @param min_value 最小値
 * @param max_length 最大長
 * @param min_length 最小長
 * @param input_type 入力種別
 */
case class FormInsertFormRequestFormColValidation(
                                                   max_value: Int,
                                                   min_value: Int,
                                                   max_length: Int,
                                                   min_length: Int,
                                                   input_type: Int
                                                 )

/**
 * フォーム作成API・フォーム項目・選択リスト
 * @param select_index 順番
 * @param select_name 表示テキスト
 * @param select_value 値　
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時CSSスタイル
 * @param view_style 参照時CSSスタイル
 */
case class FormInsertFormRequestFormColSelect(
                                               select_index: Int,
                                               select_name: String,
                                               select_value: String,
                                               is_default: Boolean,
                                               edit_style: String,
                                               view_style: String
                                             )

/**
 * フォーム作成API・フォーム項目
 * @param name 項目名
 * @param col_id 項目ID
 * @param col_index 順番
 * @param col_type 項目種別
 * @param default_value 初期値
 * @param select_list 選択リスト
 * @param validations バリデーション
 */
case class FormInsertFormRequestFormCol(
                                         name: String,
                                         col_id: String,
                                         col_index: Int,
                                         col_type: Int,
                                         default_value: String,
                                         select_list: List[FormInsertFormRequestFormColSelect],
                                         validations: Option[FormInsertFormRequestFormColValidation]
                                       )

/**
 * フォーム作成API・フォームデータ
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
case class FormInsertFormRequest(
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
                                  form_cols: List[FormInsertFormRequestFormCol]
                                ) {

}

trait FormInsertFormRequestJson {
  implicit val FormInsertFormRequestFormColValidationWrites: Writes[FormInsertFormRequestFormColValidation] = (formInsertFormRequestFormColValidation: FormInsertFormRequestFormColValidation) => Json.obj(
    "max_value" -> formInsertFormRequestFormColValidation.max_value,
    "min_value" -> formInsertFormRequestFormColValidation.min_value,
    "max_length" -> formInsertFormRequestFormColValidation.max_length,
    "min_length" -> formInsertFormRequestFormColValidation.min_length,
    "input_type" -> formInsertFormRequestFormColValidation.input_type
  )

  implicit val FormInsertFormRequestFormColValidationReads: Reads[FormInsertFormRequestFormColValidation] = (
      (JsPath \ "max_value").read[Int] ~
      (JsPath \ "min_value").read[Int] ~
      (JsPath \ "max_length").read[Int] ~
      (JsPath \ "min_length").read[Int] ~
      (JsPath \ "input_type").read[Int]
    )(FormInsertFormRequestFormColValidation.apply _)

  implicit val FormInsertFormRequestFormColSelectListWrites: Writes[FormInsertFormRequestFormColSelect] = (formInsertFormRequestFormColSelectList: FormInsertFormRequestFormColSelect) => Json.obj(
    "select_index" -> formInsertFormRequestFormColSelectList.select_index,
    "select_name" -> formInsertFormRequestFormColSelectList.select_name,
    "select_value" -> formInsertFormRequestFormColSelectList.select_value,
    "is_default" -> formInsertFormRequestFormColSelectList.is_default,
    "edit_style" -> formInsertFormRequestFormColSelectList.edit_style,
    "view_style" -> formInsertFormRequestFormColSelectList.view_style
  )

  implicit val FormInsertFormRequestFormColSelectListReads: Reads[FormInsertFormRequestFormColSelect] = (
      (JsPath \ "select_index").read[Int] ~
      (JsPath \ "select_name").read[String] ~
      (JsPath \ "select_value").read[String] ~
      (JsPath \ "is_default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String]
    )(FormInsertFormRequestFormColSelect.apply _)

  implicit val FormInsertFormRequestFormColWrites: Writes[FormInsertFormRequestFormCol] = (formInsertFormRequestFormCol: FormInsertFormRequestFormCol) => Json.obj(
    "name" -> formInsertFormRequestFormCol.name,
    "col_id" -> formInsertFormRequestFormCol.col_id,
    "col_index" -> formInsertFormRequestFormCol.col_index,
    "col_type" -> formInsertFormRequestFormCol.col_type,
    "default_value" -> formInsertFormRequestFormCol.default_value,
    "select_list" -> formInsertFormRequestFormCol.select_list,
    "validations" -> formInsertFormRequestFormCol.validations
  )

  implicit val FormInsertFormRequestFormColReads: Reads[FormInsertFormRequestFormCol] = (
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "col_index").read[Int] ~
      (JsPath \ "col_type").read[Int] ~
      (JsPath \ "default_value").read[String] ~
      (JsPath \ "select_list").read[List[FormInsertFormRequestFormColSelect]] ~
      (JsPath \ "validations").readNullable[FormInsertFormRequestFormColValidation]
    )(FormInsertFormRequestFormCol.apply _)

  implicit val FormInsertFormRequestWrites: Writes[FormInsertFormRequest] = (formInsertFormRequest: FormInsertFormRequest) => Json.obj(
    "name" -> formInsertFormRequest.name,
    "form_index" -> formInsertFormRequest.form_index,
    "title" -> formInsertFormRequest.title,
    "status" -> formInsertFormRequest.status,
    "cancel_url" -> formInsertFormRequest.cancel_url,
    "close_text" -> formInsertFormRequest.close_text,
    "hashed_id" -> formInsertFormRequest.hashed_id,
    "complete_url" -> formInsertFormRequest.complete_url,
    "input_header" -> formInsertFormRequest.input_header,
    "complete_text" -> formInsertFormRequest.complete_text,
    "confirm_header" -> formInsertFormRequest.confirm_header,
    "form_cols" -> formInsertFormRequest.form_cols
  )

  implicit val FormInsertFormRequestReads: Reads[FormInsertFormRequest] = (
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
      (JsPath \ "form_cols").read[List[FormInsertFormRequestFormCol]]
    )(FormInsertFormRequest.apply _)
}