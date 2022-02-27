package net.macolabo.sform2.models.entity.form

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
                 id: BigInt,
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
               )

object Form extends SQLSyntaxSupport[Form] {
  override val tableName = "d_form"

  def apply(rs: WrappedResultSet): Form = {
    Form(
      rs.bigInt("id"),
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
}