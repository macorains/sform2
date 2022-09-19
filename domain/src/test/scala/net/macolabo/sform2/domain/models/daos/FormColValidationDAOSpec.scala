package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol, FormColValidation}
import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.{ZoneId, ZonedDateTime}

class FormColValidationDAOSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {

  behavior of "FormColValidation"

  override def fixture(implicit session: DBSession): Unit = {
    withSQL {
      val c = Form.column
      insertInto(Form).namedValues(
        c.id -> 2,
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
        c.id -> 2,
        c.form_id -> 2,
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
      val c = FormColValidation.column
      insertInto(FormColValidation).namedValues(
        c.id -> null,
        c.form_col_id -> 2,
        c.form_id -> 2,
        c.max_value -> 1,
        c.min_value -> 1,
        c.max_length -> 1,
        c.min_length -> 1,
        c.input_type -> 1,
        c.required -> 1,
        c.user_group -> "hoge",
        c.created_user -> "foo",
        c.modified_user -> "foo",
        c.created -> "2020-08-01 12:00:00",
        c.modified -> "2020-08-01 12:00:00"
      )
    }.update().apply()
  }

  it should "select a formCol" in { implicit session =>
    val mockFormColValidationDAO = new FormColValidationDAOImpl()
    val formColValidation = mockFormColValidationDAO.get("hoge", 2, 2)

    assert(formColValidation.nonEmpty)
    val formColValidationData = formColValidation.get
    assert(formColValidationData.form_col_id.equals(2))
    assert(formColValidationData.max_value.get.equals(1))
    assert(formColValidationData.min_value.get.equals(1))
    assert(formColValidationData.max_length.get.equals(1))
    assert(formColValidationData.min_length.get.equals(1))
    assert(formColValidationData.input_type.equals(1))
    assert(formColValidationData.required)
    assert(formColValidationData.user_group.equals("hoge"))
    assert(formColValidationData.created_user.equals("foo"))
    assert(formColValidationData.modified_user.equals("foo"))
    assert(formColValidationData.created.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
    assert(formColValidationData.modified.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
  }
}

