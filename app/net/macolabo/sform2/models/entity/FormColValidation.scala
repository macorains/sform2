package net.macolabo.sform2.models.entity

import java.time.ZonedDateTime

import scalikejdbc._

case class FormColValidation(
                              id: Int,
                              form_col_id: Int,
                              form_id: Int,
                              max_value: Int,
                              min_value: Int,
                              max_length: Int,
                              min_Length: Int,
                              input_type: Int,
                              user_group: String,
                              created_user: String,
                              modified_user: String,
                              created: ZonedDateTime,
                              modified: ZonedDateTime
                            ){
  import FormColValidation._
  def insert: Int = create(this)
  def update: Int = save(this)

}

object FormColValidation extends SQLSyntaxSupport[FormColValidation] {
  override val tableName = "D_FORM_COL_VALIDATION"
  def apply(rs: WrappedResultSet) :FormColValidation = {
    FormColValidation(
      rs.int("id"),
      rs.int("form_col_id"),
      rs.int("form_id"),
      rs.int("max_value"),
      rs.int("min_value"),
      rs.int("max_length"),
      rs.int("min_length"),
      rs.int("input_type"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * フォーム項目バリデーションのリスト取得
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param formColId フォーム項目ID
   * @param session DB Session
   * @return フォーム項目バリデーションのリスト
   */
  def getList(userGroup: String, formId: Int, formColId: Int)(implicit session: DBSession = autoSession): List[FormColValidation] = {
    val f = FormColValidation.syntax("f")
    withSQL (
      select
        .from(FormColValidation as f)
        .where
        .eq(f.form_id, formId)
        .and
        .eq(f.form_col_id, formColId)
        .and
        .eq(f.user_group, userGroup)
      ).map(rs => FormColValidation(rs)).list().apply()
  }

  /**
   * フォーム項目バリデーションの作成
   * @param formColValidation フォーム項目バリデーション
   * @param session DB Session
   * @return
   */
  def create(formColValidation: FormColValidation)(implicit session: DBSession = autoSession): Int = {
    withSQL{
      val c = FormColValidation.column
      insert.into(FormColValidation).namedValues(
        c.form_col_id -> formColValidation.form_col_id,
        c.form_id -> formColValidation.form_id,
        c.max_value -> formColValidation.max_value,
        c.min_value -> formColValidation.min_value,
        c.max_length -> formColValidation.max_length,
        c.min_Length -> formColValidation.min_Length,
        c.input_type -> formColValidation.input_type,
        c.user_group -> formColValidation.user_group,
        c.created_user -> formColValidation.created_user,
        c.modified_user -> formColValidation.modified_user,
        c.created -> formColValidation.created,
        c.modified -> formColValidation.modified
      )
    }.update().apply()
  }

  /**
   * フォーム項目バリデーションの更新
   * @param formColValidation フォーム項目バリデーション
   * @param session DB Session
   */
  def save(formColValidation: FormColValidation)(implicit session: DBSession = autoSession): Int = {
    withSQL{
      update(FormColValidation).set(
        column.form_col_id -> formColValidation.form_col_id,
        column.form_id -> formColValidation.form_id,
        column.max_value -> formColValidation.max_value,
        column.min_value -> formColValidation.min_value,
        column.max_length -> formColValidation.max_length,
        column.min_Length -> formColValidation.min_Length,
        column.input_type -> formColValidation.input_type,
        column.modified_user -> formColValidation.modified_user,
        column.modified -> formColValidation.modified
      ).where.eq(column.id, formColValidation.id)
    }.update().apply()
  }
}
