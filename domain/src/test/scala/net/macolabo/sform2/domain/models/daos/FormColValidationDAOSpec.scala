package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import org.mockito.MockitoSugar.mock
import org.scalatest.flatspec.FixtureAnyFlatSpec
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.{ZoneId, ZonedDateTime}

class FormColValidationDAOSpec extends FixtureAnyFlatSpec with AutoRollback with SformTestHelper {
  override def fixture(implicit session: DBSession): Unit = {
    sql"insert into d_form_col_validation values(null, 1, 1, 1, 1, 1, 1, 1, 1, 'hoge', 'foo', 'foo', '2020-08-01 12:00:00', '2020-08-01 12:00:00')".update().apply()
  }

  behavior of "DFormColValidation"

  it should "select a formCol" in { implicit session =>
    val mockFormColValidationDAO = mock[FormColValidationDAO]

    val formColValidation = mockFormColValidationDAO.get("hoge", 1, 1)
    assert(!formColValidation.getOrElse(None).equals(None))
    val formColValidationData = formColValidation.get
    assert(formColValidationData.form_col_id.equals(1))
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

