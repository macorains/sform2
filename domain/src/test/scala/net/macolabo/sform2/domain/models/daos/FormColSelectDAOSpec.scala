package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol, FormColSelect}
import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.{ZoneId, ZonedDateTime}

class FormColSelectDAOSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {

  behavior of "FormColSelect"

  override def fixture(implicit session: DBSession): Unit = {
    withSQL {
      val c = Form.column
      insertInto(Form).namedValues(
        c.id -> 1,
        c.hashed_id -> "test",
        c.form_index -> 1,
        c.name -> "test1",
        c.title -> "test1",
        c.status -> 1,
        c.cancel_url -> "http://hogehoge.com",
        c.complete_url -> "http://foobar.com",
        c.input_header -> "input1",
        c.confirm_header -> "confirm1",
        c.complete_text -> "complete1",
        c.close_text -> "close1",
        c.form_data -> "{}",
        c.user_group -> "hoge",
        c.created_user -> "foo",
        c.modified_user -> "foo",
        c.created -> "2020-08-01 12:00:00",
        c.modified -> "2020-08-01 12:00:00"
      )
    }.update().apply()

    withSQL {
      val c = FormCol.column
      insertInto(FormCol).namedValues(
        c.id -> 1,
        c.form_id -> 1,
        c.name -> "col1",
        c.col_id -> "col1",
        c.col_index -> 1,
        c.col_type -> 1,
        c.default_value -> "a",
        c.user_group -> "hoge",
        c.created_user -> "foo",
        c.modified_user -> "foo",
        c.created -> "2020-08-01 12:00:00",
        c.modified -> "2020-08-01 12:00:00"
      )
    }.update().apply()

    withSQL {
      val c = FormColSelect.column
      insertInto(FormColSelect).namedValues(
        c.id -> null,
        c.form_col_id -> 1,
        c.form_id -> 1,
        c.select_index -> 1,
        c.select_name -> "col1",
        c.select_value -> "col1",
        c.is_default -> "1",
        c.edit_style -> "aaa",
        c.view_style -> "aaa",
        c.user_group -> "hoge",
        c.created_user -> "foo",
        c.modified_user -> "foo",
        c.created -> "2020-08-01 12:00:00",
        c.modified -> "2020-08-01 12:00:00"
      )
    }.update().apply()
  }

  it should "select a formCol" in { implicit session =>
    val mockFormColSelectDAO = new FormColSelectDAOImpl()

    val formColSelectList = mockFormColSelectDAO.getList(1, 1)
    assert(formColSelectList.size.equals(1))
    val formColSelect = formColSelectList.last
    assert(formColSelect.form_col_id == 1)
    assert(formColSelect.form_id == 1)
    assert(formColSelect.select_index.equals(1))
    assert(formColSelect.select_name.equals("col1"))
    assert(formColSelect.select_value.equals("col1"))
    assert(formColSelect.is_default)
    assert(formColSelect.edit_style.get.equals("aaa"))
    assert(formColSelect.view_style.get.equals("aaa"))
    assert(formColSelect.user_group.equals("hoge"))
    assert(formColSelect.created_user.equals("foo"))
    assert(formColSelect.modified_user.equals("foo"))
    assert(formColSelect.created.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
    assert(formColSelect.modified.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
  }
}
