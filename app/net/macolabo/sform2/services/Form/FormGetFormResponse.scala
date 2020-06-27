package net.macolabo.sform2.services.Form
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
                                               id: Long,
                                               form_col_id: Long,
                                               form_id: Long,
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
  * @param index 順番
  * @param value 値　
  * @param default デフォルト値とするか
  * @param edit_style 編集時CSSスタイル
  * @param view_style 参照時CSSスタイル
  * @param display_text 表示テキスト
  */
case class FormGetFormResponseFormColSelectList(
                                               id: Long,
                                               form_col_id: Long,
                                               form_id: Long,
                                               index: Int,
                                               value: String,
                                               default: Boolean,
                                               edit_style: String,
                                               view_style: String,
                                               display_text: String
                                               )

/**
  * フォーム取得API・フォーム項目
  * @param id ID
  * @param form_id フォームID
  * @param name 項目名
  * @param col_id 項目ID
  * @param index 順番
  * @param col_type 項目種別
  * @param default 初期値
  * @param select_list 選択リスト
  * @param validations バリデーション
  */
case class FormGetFormResponseFormCol(
                                     id: Long,
                                     form_id: Long,
                                     name: String,
                                     col_id: String,
                                     index: Int,
                                     col_type: Int,
                                     default: String,
                                     select_list: List[FormGetFormResponseFormColSelectList],
                                     validations: List[FormGetFormResponseFormColValidation]
                                     )

/**
  * フォーム取得API・フォームデータ
  * @param id フォームID
  * @param name フォーム名
  * @param index 順番
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
                              id: Long,
                              name: String,
                              index: Int,
                              title: String,
                              status: Int,
                              cancel_url: String,
                              close_text: String,
                              hashed_id: String,
                              complete_url: String,
                              input_header: String,
                              complete_text: String,
                              confirm_header: String,
                              form_cols: List[FormGetFormResponseFormCol]
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
    (JsPath \ "id").read[Long] ~
      (JsPath \ "form_col_id").read[Long] ~
      (JsPath \ "form_id").read[Long] ~
      (JsPath \ "max_value").read[Int] ~
      (JsPath \ "min_value").read[Int] ~
      (JsPath \ "max_length").read[Int] ~
      (JsPath \ "min_length").read[Int] ~
      (JsPath \ "input_type").read[Int]
    )(FormGetFormResponseFormColValidation.apply _)

  implicit val FormGetFormResponseFormColSelectListWrites: Writes[FormGetFormResponseFormColSelectList] = (formGetFormResponseFormColSelectList: FormGetFormResponseFormColSelectList) => Json.obj(
    "id" -> formGetFormResponseFormColSelectList.id,
    "form_col_id" -> formGetFormResponseFormColSelectList.form_col_id,
    "form_id" -> formGetFormResponseFormColSelectList.form_id,
    "index" -> formGetFormResponseFormColSelectList.index,
    "value" -> formGetFormResponseFormColSelectList.value,
    "default" -> formGetFormResponseFormColSelectList.default,
    "edit_style" -> formGetFormResponseFormColSelectList.edit_style,
    "view_style" -> formGetFormResponseFormColSelectList.view_style,
    "display_text" -> formGetFormResponseFormColSelectList.display_text
  )

  implicit val FormGetFormResponseFormColSelectListReads: Reads[FormGetFormResponseFormColSelectList] = (
    (JsPath \ "id").read[Long] ~
      (JsPath \ "form_col_id").read[Long] ~
      (JsPath \ "form_id").read[Long] ~
      (JsPath \ "index").read[Int] ~
      (JsPath \ "value").read[String] ~
      (JsPath \ "default").read[Boolean] ~
      (JsPath \ "edit_style").read[String] ~
      (JsPath \ "view_style").read[String] ~
      (JsPath \ "display_text").read[String]
  )(FormGetFormResponseFormColSelectList.apply _)

  implicit val FormGetFormResponseFormColWrites: Writes[FormGetFormResponseFormCol] = (formGetFormResponseFormCol: FormGetFormResponseFormCol) => Json.obj(
    "id" -> formGetFormResponseFormCol.id,
    "form_id" -> formGetFormResponseFormCol.form_id,
    "name" -> formGetFormResponseFormCol.name,
    "col_id" -> formGetFormResponseFormCol.col_id,
    "index" -> formGetFormResponseFormCol.index,
    "col_type" -> formGetFormResponseFormCol.col_type,
    "default" -> formGetFormResponseFormCol.default,
    "select_list" -> formGetFormResponseFormCol.select_list,
    "validations" -> formGetFormResponseFormCol.validations
  )

  implicit val FormGetFormResponseFormColReads: Reads[FormGetFormResponseFormCol] = (
    (JsPath \ "id").read[Long] ~
      (JsPath \ "form_id").read[Long] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "col_id").read[String] ~
      (JsPath \ "index").read[Int] ~
      (JsPath \ "default").read[String] ~
      (JsPath \ "select_list").read[List[FormGetFormResponseFormColSelectList]] ~
      (JsPath \ "validations").read[List[FormGetFormResponseFormColValidation]]
  )(FormGetFormResponseFormCol.apply _)

  implicit val FormGetFormResponseWrites: Writes[FormGetFormResponse] = (formGetFormResponse: FormGetFormResponse) => Json.obj(
    "id" -> formGetFormResponse.id,
    "name" -> formGetFormResponse.name,
    "index" -> formGetFormResponse.index,
    "title" -> formGetFormResponse.title,
    "status" -> formGetFormResponse.status,
    "cancel_url" -> formGetFormResponse.cancel_url,
    "close_text" -> formGetFormResponse.close_text,
    "hashed_id" -> formGetFormResponse.hashed_id,
    "complete_url" -> formGetFormResponse.complete_url,
    "input_header" -> formGetFormResponse.input_header,
    "complete_text" -> formGetFormResponse.complete_text,
    "confirm_header" -> formGetFormResponse.confirm_header,
    "form_cols" -> formGetFormResponse.form_cols
  )

  implicit val FormGetFormResponseReads: Reads[FormGetFormResponse] = (
    (JsPath \ "id").read[Long] ~
      (JsPath \ "name").read[String] ~
      (JsPath \ "index").read[Int] ~
      (JsPath \ "title").read[String] ~
      (JsPath \ "status").read[Int] ~
      (JsPath \ "cancel_url").read[String] ~
      (JsPath \ "close_text").read[String] ~
      (JsPath \ "hashed_id").read[String] ~
      (JsPath \ "complete_url").read[String] ~
      (JsPath \ "input_header").read[String] ~
      (JsPath \ "complete_text").read[String] ~
      (JsPath \ "confirm_header").read[String] ~
      (JsPath \ "form_cols").read[List[FormGetFormResponseFormCol]]
  )(FormGetFormResponse.apply _)
}