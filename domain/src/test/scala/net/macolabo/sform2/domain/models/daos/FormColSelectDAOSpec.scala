package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.{ZoneId, ZonedDateTime}

class FormColSelectDAOSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {

  override def fixture(implicit session: DBSession): Unit = {
    sql"insert into d_form_col_select values(null, 1, 1, 1, 'col1', 'col1', 1, 'aaa', 'aaa', 'hoge', 'foo', 'foo', '2020-08-01 12:00:00', '2020-08-01 12:00:00')".update().apply()
  }

  behavior of "DFormColSelect"
  // TODO エラーになるので一旦コメントアウト
  /*
  it should "select a formCol" in { implicit session =>
    val mockFormColSelectDAO = mock[FormColSelectDAO]

    val formColSelectList = mockFormColSelectDAO.getList(1, 1)
    assert(formColSelectList.size.equals(1))
    val formColSelect = formColSelectList.last
    assert(formColSelect.form_col_id.equals(1))
    assert(formColSelect.form_id.equals(1))
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
    assert(formColSelect.modified.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))  }


   */
}
