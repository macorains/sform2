package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.helper.SformTestHelper
import net.macolabo.sform2.domain.models.daos.PostdataDAOImpl
import net.macolabo.sform2.domain.models.entity.Postdata
import net.macolabo.sform2.domain.models.entity.transfer.TransferDetailLog
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{DBSession, insertInto, withSQL}

import java.time.ZonedDateTime

class PostdataDAOImplSpec extends FixtureAnyFlatSpec  with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {
  behavior of "Postdata"

  it should "getPostdataByFormHashedId" in { implicit session =>
    val postdataDAO = new PostdataDAOImpl()
    val postdata = postdataDAO.getPostdataByFormHashedId("aaa", 1)

    assert(postdata.nonEmpty)
  }

  it should "getPostdata" in { implicit session =>
    val postdataDAO = new PostdataDAOImpl()
    val postdata = postdataDAO.getPostdata("aaa")

    assert(postdata.nonEmpty)
  }

  /*
  Fixture
  */
  override def fixture(implicit session: DBSession): Unit = {
    val postdata_id = withSQL {
      val c = Postdata.column
      insertInto(Postdata).namedValues(
        c.form_hashed_id -> "aaa",
        c.postdata -> """{"a":"xxx"}""",
        c.user_group -> "hoge",
        c.created_user -> "fuga",
        c.created -> ZonedDateTime.now(),
        c.modified_user -> "fuga",
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey("postdata_id").apply()

    withSQL {
      val c = TransferDetailLog.column
      insertInto(TransferDetailLog).namedValues(
        c.postdata_id -> postdata_id,
        c.transfer_type_id -> 1,
        c.status -> 0,
        c.postdata -> """{"a":"xxx"}""",
        c.modified_postdata -> """{"a":"xxx"}""",
        c.result_code -> 1,
        c.message -> """{"msg":"hoge"}""",
        c.user_group -> "hoge",
        c.created_user -> "fuga",
        c.created -> ZonedDateTime.now(),
        c.modified_user -> "fuga",
        c.modified -> ZonedDateTime.now()
      )
    }.update().apply()
  }
}
