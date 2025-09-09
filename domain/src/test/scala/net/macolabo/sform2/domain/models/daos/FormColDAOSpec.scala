package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol}
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import net.macolabo.sform2.domain.models.helper.SformTestHelper

import java.time.{ZoneId, ZonedDateTime}

class FormColDAOSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {

  behavior of "FormCol"

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
  }

  it should "select a formCol" in { implicit session =>
    val mockFormColDAO = new FormColDAOImpl()

    val formColList = mockFormColDAO.getList(1)
    assert(formColList.nonEmpty)
    assert(formColList.size.equals(1))
    val formCol = formColList.last
    println(formCol)
    assert(formCol.form_id == 1)
    assert(formCol.name.equals("col1"))
    assert(formCol.col_id.equals("col1"))
    assert(formCol.col_index.equals(1))
    assert(formCol.col_type.equals(1))
    assert(formCol.default_value.get.equals("a"))
    assert(formCol.user_group.equals("hoge"))
    assert(formCol.created_user.equals("foo"))
    assert(formCol.modified_user.equals("foo"))
    assert(formCol.created.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
    assert(formCol.modified.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
  }
}
