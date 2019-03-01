package models.json

import play.api.libs.json._

trait FormJson {
  /**
    * フォーム定義
    * @param index 表示順
    * @param id フォームID
    * @param status フォーム状態
    * @param name フォーム名
    * @param title フォームタイトル
    * @param extLink1 外部連携1使用フラグ（当面はSF専用）
    * @param cancelUrl キャンセル時遷移先URL
    * @param completeUrl  完了時遷移先URL
    * @param inputHeader フォーム入力画面ヘッダ文面
    * @param confirmHeader フォーム確認画面ヘッダ文面
    * @param completeText フォーム完了画面文面
    * @param formCols フォーム項目定義
    * @param closeText 無効時表示文
    * @param replymailFrom メール返信元アドレス
    * @param replymailSubject 自動返信メール件名
    * @param replymailText 自動返信メール文面
    * @param noticemailSend 通知メール
    * @param noticemailText 通知メール文面
    */

  case class FormDef(index: String, id: Option[String], hashed_id: Option[String], status: String, name: String, title: String,
                     extLink1: Option[Boolean], cancelUrl: String, completeUrl: String, inputHeader: String,
                     confirmHeader: String, completeText: String, closeText: Option[String], replymailFrom: Option[String],
                     replymailSubject: Option[String], replymailText: Option[String], noticemailSend: Option[String], noticemailText: Option[String], formCols: JsObject) {
    def replaceId(newId: Int, newHashedId: String): FormDef = {
      FormDef(index, Option(newId.toString), Option(newHashedId), status, name, title, extLink1, cancelUrl, completeUrl, inputHeader, confirmHeader, completeText,
        closeText, replymailFrom, replymailSubject, replymailText, noticemailSend, noticemailText, formCols)
    }
  }
  object FormDef {
    implicit def jsonFormDefWrites: Writes[FormDef] = Json.writes[FormDef]
    implicit def jsonFormDefReads: Reads[FormDef] = Json.reads[FormDef]
  }

  /**
    * フォーム項目定義
    * @param index 表示順
    * @param name フォーム項目名
    * @param colId フォーム項目ID
    * @param coltype フォーム項目種別
    * 　　　　　　　1 : テキスト            2 : コンボボックス      3 : チェックボックス    4 : ラジオボタン
    * 5 : テキストエリア      6 : 隠しテキスト        7 : 表示テキスト（非入力項目）
    * @param default 初期値
    * @param validations バリデーション定義リスト
    * @param selectList 選択肢定義リスト（ラジオ、チェックボックス、セレクトリスト用）
    */
  case class FormDefCol(index: String, name: String, colId: String, coltype: Option[String], default: Option[String], validations: JsObject, selectList: JsObject)
  object FormDefCol {
    implicit def jsonFormDefColWrites: Writes[FormDefCol] = Json.writes[FormDefCol]
    implicit def jsonFormDefColReads: Reads[FormDefCol] = Json.reads[FormDefCol]
  }

  /**
    * フォームバリデーション定義
    * @param inputType 入力種別
    * @param minValue 最小値
    * @param maxValue 最大値
    * @param minLength 最小文字数
    * @param maxLength 最大文字数
    * @param required 必須項目
    */
  case class FormDefColValidation(inputType: String, minValue: String, maxValue: String, minLength: String, maxLength: String, required: Option[Boolean])
  object FormDefColValidation {
    implicit def jsonFormDefColValidationWrites: Writes[FormDefColValidation] = Json.writes[FormDefColValidation]
    implicit def jsonFormDefColValidationReads: Reads[FormDefColValidation] = Json.reads[FormDefColValidation]
  }

  /**
    * 選択リスト定義
    * @param index 表示順
    * @param displayText 表示文字列
    * @param value リスト値
    * @param default デフォルト選択状態にするか
    * @param viewStyle 確認画面時スタイル定義
    * @param editStyle 入力画面時スタイル定義
    */
  case class FormDefColSelectList(index: String, displayText: String, value: String, default: String, viewStyle: String, editStyle: String)
  object FormDefColSelectList {
    implicit def jsonFormDefColSelectListWrites: Writes[FormDefColSelectList] = Json.writes[FormDefColSelectList]
    implicit def jsonFormDefColSelectListReads: Reads[FormDefColSelectList] = Json.reads[FormDefColSelectList]
  }

  /**
    * レスポンスJSON用クラス
    * @param id id
    */
  case class insertFormResponce(id: String)
  object insertFormResponce {
    implicit def jsonInsertFormResponceWrites: Writes[insertFormResponce] = Json.writes[insertFormResponce]
    implicit def jsonInsertFormResponceReads: Reads[insertFormResponce] = Json.reads[insertFormResponce]
  }

  /**
    * バリデーション要求クラス
    * @param formid フォームのhashed_id
    * @param postdata フォーム送信データ
    */
  case class FormSaveRequest(formid: String, postdata: JsValue)
  object FormSaveRequest {
    implicit def jsonFormSaveRequestWrites: Writes[FormSaveRequest] = Json.writes[FormSaveRequest]
    implicit def jsonFormSaveRequestReads: Reads[FormSaveRequest] = Json.reads[FormSaveRequest]
  }

}
