package net.macolabo.sform2.models.entity.form

import java.time.ZonedDateTime

import scalikejdbc._

/**
 * フォーム項目
 *
 * @param id　フォーム項目ID
 * @param form_id フォームID
 * @param name 項目名
 * @param col_id 項目ID
 * @param col_index 項目番号
 * @param col_type 項目種別
 * @param default_value デフォルト値
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class FormCol (
                     id: BigInt,
                     form_id: BigInt,
                     name: String,
                     col_id: String,
                     col_index: Int,
                     col_type: Int,
                     default_value: Option[String],
                     user_group: String,
                     created_user: String,
                     modified_user: String,
                     created: ZonedDateTime,
                     modified: ZonedDateTime
                   ){
}

object FormCol extends SQLSyntaxSupport[FormCol] {
  override val tableName = "d_form_col"

  def apply(rs: WrappedResultSet): FormCol = {
    FormCol(
      rs.bigInt("id"),
      rs.bigInt("form_id"),
      rs.string("name"),
      rs.string("col_id"),
      rs.int("col_index"),
      rs.int("col_type"),
      rs.stringOpt("default_value"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }
}