package net.macolabo.sform2.models.entity

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
                     id: Int,
                     form_id: Int,
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
  import FormCol._
  def insert: Int = create(this)
  def update: Int = save(this)

}

object FormCol extends SQLSyntaxSupport[FormCol] {
  override val tableName = "D_FORM_COL"
  def apply(rs: WrappedResultSet): FormCol = {
    FormCol(
      rs.int("id"),
      rs.int("form_id"),
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

  /**
   * フォーム項目一覧
   * @param formId フォームID
   * @param session DB Session
   * @return フォーム項目のリスト
   */
  def getList(userGroup: String, formId: Int)(implicit session: DBSession = autoSession): List[FormCol] = {
    val f = FormCol.syntax("f")
    withSQL(
      select
        .from(FormCol as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.user_group, userGroup)
    ).map(rs => FormCol(rs)).list().apply()
  }

  /**
   * フォーム項目作成
   * @param formCol フォーム項目
   * @param session DB Session
   * @return フォーム項目ID
   */
  def create(formCol: FormCol)(implicit session: DBSession = autoSession): Int = {
    withSQL{
      val c = FormCol.column
      insert.into(FormCol).namedValues(
        c.form_id -> formCol.form_id,
        c.name -> formCol.name,
        c.col_id -> formCol.col_id,
        c.col_index -> formCol.col_index,
        c.col_type -> formCol.col_type,
        c.default_value -> formCol.default_value,
        c.user_group -> formCol.user_group,
        c.created_user -> formCol.created_user,
        c.modified_user -> formCol.modified_user,
        c.created -> formCol.created,
        c.modified -> formCol.modified
      )
    }.update().apply()
  }

  /**
   * フォーム項目更新
   * @param formCol フォーム項目
   * @param session DB Session
   * @return
   */
  def save(formCol: FormCol)(implicit session: DBSession = autoSession): Int = {
    withSQL{
      update(FormCol).set(
        column.form_id -> formCol.form_id,
        column.name -> formCol.name,
        column.col_id -> formCol.col_id,
        column.col_index -> formCol.col_index,
        column.col_type -> formCol.col_type,
        column.default_value -> formCol.default_value,
        column.modified_user -> formCol.modified_user,
        column.modified -> formCol.modified
      ).where.eq(column.id, formCol.id)
    }.update().apply()
  }
}