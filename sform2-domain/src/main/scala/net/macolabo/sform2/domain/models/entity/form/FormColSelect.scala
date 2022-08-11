package net.macolabo.sform2.domain.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * フォーム項目 選択肢
 * @param id フォーム項目 選択肢ID
 * @param form_col_id フォーム項目ID
 * @param form_id フォームID
 * @param select_index 選択項目番号
 * @param select_name 選択項目名
 * @param select_value 選択項目の値
 * @param is_default デフォルト値とするか
 * @param edit_style 編集時cssスタイル
 * @param view_style 表示時cssスタイル
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class FormColSelect(
                          id: BigInt,
                          form_col_id: BigInt,
                          form_id: BigInt,
                          select_index: Int,
                          select_name: String,
                          select_value: String,
                          is_default: Boolean,
                          edit_style: Option[String],
                          view_style: Option[String],
                          user_group: String,
                          created_user: String,
                          modified_user: String,
                          created: ZonedDateTime,
                          modified: ZonedDateTime
                        ){
}

object FormColSelect extends SQLSyntaxSupport[FormColSelect] {
  override val tableName = "d_form_col_select"

  def apply(rs: WrappedResultSet): FormColSelect = {
    FormColSelect(
      rs.bigInt("id"),
      rs.bigInt("form_col_id"),
      rs.bigInt("form_id"),
      rs.int("select_index"),
      rs.string("select_name"),
      rs.string("select_value"),
      rs.boolean("is_default"),
      rs.stringOpt("edit_style"),
      rs.stringOpt("view_style"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}
