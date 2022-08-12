package net.macolabo.sform.api.models.form

import java.time.{ZoneId, ZonedDateTime}

import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import helper.SformApiTestHelper
import net.macolabo.sform.api.models.daos.FormDAO
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec

class FormSpec extends FixtureAnyFlatSpec with AutoRollback with SformApiTestHelper{
  override def fixture(implicit session: DBSession): Unit =
  {
    sql"insert into d_form values(null, 'test', 1, 'test1', 'test1', 1, 'http://hogehoge.com', 'http://foobar.com', 'input1', 'confirm1', 'complete1', 'close1', '{}', 'hoge', 'foo', 'foo', '2020-08-01 12:00:00', '2020-08-01 12:00:00')".update().apply()
  }

  behavior of "DForm"

  it should "select a form" in { implicit session =>
    val mockFormDAO = mock[FormDAO]

    val form = mockFormDAO.get( "test")
    assert(!form.getOrElse(None).equals(None))
    val formdata = form.get
    assert(formdata.id.isInstanceOf[BigInt])
    assert(formdata.hashed_id.equals("test"))
    assert(formdata.form_index.equals(1))
    assert(formdata.name.equals("test1"))
    assert(formdata.title.equals("test1"))
    assert(formdata.status.equals(1))
    assert(formdata.cancel_url.equals("http://hogehoge.com"))
    assert(formdata.complete_url.equals("http://foobar.com"))
    assert(formdata.input_header.get.equals("input1"))
    assert(formdata.confirm_header.get.equals("confirm1"))
    assert(formdata.complete_text.get.equals("complete1"))
    assert(formdata.close_text.get.equals("close1"))
    assert(formdata.form_data.equals("{}"))
    assert(formdata.user_group.equals("hoge"))
    assert(formdata.created_user.equals("foo"))
    assert(formdata.modified_user.equals("foo"))
    assert(formdata.created.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
    assert(formdata.modified.withZoneSameInstant(ZoneId.of("UTC")).equals(ZonedDateTime.of(2020, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"))))
  }
}
