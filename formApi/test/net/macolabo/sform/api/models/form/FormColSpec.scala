package net.macolabo.sform.api.models.form

import java.time.{ZoneId, ZonedDateTime}

import helper.SformApiTestHelper
import net.macolabo.sform.api.models.daos.FormColDAO
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

class FormColSpec extends FixtureAnyFlatSpec with AutoRollback with SformApiTestHelper {
  override def fixture(implicit session: DBSession): Unit = {
    sql"insert into d_form_col values(null, 1, 'col1', 'col1', 1, 1, 'a', 'hoge', 'foo', 'foo', '2020-08-01 12:00:00', '2020-08-01 12:00:00')".update().apply()
  }

  behavior of "DFormCol"

  it should "select a formCol" in { implicit session =>
    val mockFormColDAO = mock[FormColDAO]

    val formColList = mockFormColDAO.getList(1)
    assert(formColList.size.equals(1))
    val formCol = formColList.last
    println(formCol)
    assert(formCol.form_id.equals(1))
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