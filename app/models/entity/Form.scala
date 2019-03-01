package models.entity

import scalikejdbc._

/**
  * フォームデータ
  *
  * @param id フォームID
  * @param hashed_id フォームハッシュID
  * @param form_data フォームデータ
  */
case class Form(id: Int, hashed_id: String, form_data: String, user_group: String)
object Form extends SQLSyntaxSupport[Form] {
  override val tableName = "D_FORM"
  def apply(rs: WrappedResultSet): Form = {
    Form(rs.int("id"), rs.string("hashed_id"), rs.string("form_data"), rs.string("user_group"))
  }
}
