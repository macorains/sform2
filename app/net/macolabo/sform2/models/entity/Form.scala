package net.macolabo.sform2.models.entity

import java.time.ZonedDateTime
import scalikejdbc._

/**
 * フォームデータ
 *
 * @param id フォームID
 * @param hashed_id フォームハッシュID
 * @param form_index フォーム番号
 * @param name フォーム名
 * @param title タイトル
 * @param status ステータス
 * @param cancel_url キャンセル時遷移先URL
 * @param complete_url 完了時遷移先URL
 * @param input_header 入力画面ヘッダ文
 * @param confirm_header 確認画面ヘッダ文
 * @param complete_text 完了時文言
 * @param close_text フォームクローズ時文言
 * @param form_data フォームデータ
 * @param user_group ユーザーグループ
 * @param created_user 作成ユーザー
 * @param modified_user 更新ユーザー
 * @param created 作成日
 * @param modified 更新日
 */
case class Form(
                 id: Int,
                 hashed_id: String,
                 form_index: Int,
                 name: String,
                 title: String,
                 status: Int,
                 cancel_url: String,
                 complete_url: String,
                 input_header: Option[String],
                 confirm_header: Option[String],
                 complete_text: Option[String],
                 close_text: Option[String],
                 form_data: String,
                 user_group: String,
                 created_user: String,
                 modified_user: String,
                 created: ZonedDateTime,
                 modified: ZonedDateTime
               ){
  import Form._
  def insert: Int = create(this)
  def update: Int = save(this)
}

object Form extends SQLSyntaxSupport[Form] {
  override val tableName = "D_FORM"
  def apply(rs: WrappedResultSet): Form = {
    Form(
      rs.int("id"),
      rs.string("hashed_id"),
      rs.int("form_index"),
      rs.string("name"),
      rs.string("title"),
      rs.int("status"),
      rs.string("cancel_url"),
      rs.string("complete_url"),
      rs.stringOpt("input_header"),
      rs.stringOpt("confirm_header"),
      rs.stringOpt("complete_text"),
      rs.stringOpt("close_text"),
      rs.string("form_data"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.string("modified_user"),
      rs.dateTime("created"),
      rs.dateTime("modified")
    )
  }

  /**
   * フォームデータ取得
   * @param userGroup ユーザーグループ
   * @param hashedFormId フォームのhashed_id
   * @return フォームデータ
   */
  def get(userGroup: String, hashedFormId: String)(implicit session: DBSession = autoSession) :Option[Form] = {
    val f = Form.syntax("f")
      withSQL(
        select(
          f.id,
          f.hashed_id,
          f.form_index,
          f.name,
          f.title,
          f.status,
          f.cancel_url,
          f.complete_url,
          f.input_header,
          f.confirm_header,
          f.complete_text,
          f.close_text,
          f.form_data,
          f.user_group,
          f.created_user,
          f.modified_user,
          f.created,
          f.modified)
          .from(Form as f)
          .where
          .eq(f.hashed_id, hashedFormId)
          .and
          .eq(f.user_group, userGroup)
      ).map(rs => Form(rs)).single().apply()
  }

  /**
   * フォーム一覧
   * @return フォームデータのリスト
   */
  def getList(userGroup: String)(implicit session: DBSession = autoSession): List[Form] = {
    val f = Form.syntax("f")
      withSQL(
        select(
          f.id,
          f.hashed_id,
          f.form_index,
          f.name,
          f.title,
          f.status,
          f.cancel_url,
          f.complete_url,
          f.input_header,
          f.confirm_header,
          f.complete_text,
          f.close_text,
          f.form_data,
          f.user_group,
          f.created_user,
          f.modified_user,
          f.created,
          f.modified)
          .from(Form as f)
          .where
          .eq(f.user_group, userGroup)
      ).map(rs => Form(rs)).list().apply()
  }

  /**
   * データ作成
   * @param form フォームデータ
   * @return 作成したフォームのID
   */
  def create(form: Form)(implicit session: DBSession = autoSession): Int = {
      withSQL {
        val c = Form.column
        insert.into(Form).namedValues(
          c.hashed_id -> form.hashed_id,
          c.form_index -> form.form_index,
          c.name -> form.name,
          c.title -> form.title,
          c.status -> form.status,
          c.cancel_url -> form.cancel_url,
          c.complete_url -> form.complete_url,
          c.input_header -> form.input_header,
          c.confirm_header -> form.confirm_header,
          c.complete_text -> form.complete_text,
          c.close_text -> form.close_text,
          c.form_data -> form.form_data,
          c.user_group -> form.user_group,
          c.created_user -> form.created_user,
          c.modified_user -> form.modified_user,
          c.created -> form.created,
          c.modified -> form.modified
        )
      }.updateAndReturnGeneratedKey().apply().toInt
  }

  /**
   * データ更新
   * @param form フォームデータ
   * @param session DBセッション
   * @return
   */
  def save(form: Form)(implicit session: DBSession = autoSession): Int = {
      withSQL{
        update(Form).set(
          column.form_index -> form.form_index,
          column.name -> form.name,
          column.title -> form.title,
          column.status -> form.status,
          column.cancel_url -> form.cancel_url,
          column.complete_url -> form.complete_url,
          column.input_header -> form.input_header,
          column.confirm_header -> form.confirm_header,
          column.complete_text -> form.complete_text,
          column.close_text -> form.close_text,
          column.form_data -> form.form_data,
          column.modified_user -> form.modified_user,
          column.modified -> form.modified
        ).where.eq(column.id, form.id)
      }.update().apply()
  }

  /**
   * データ削除
   * @param userGroup ユーザーグループ
   * @param formId フォームID
   * @param session DB Session
   * @return
   */
  def erase(userGroup:String, formId: Int)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Form).where.eq(Form.column.id, formId).and.eq(Form.column.user_group, userGroup)
    }.update().apply()
  }
}
