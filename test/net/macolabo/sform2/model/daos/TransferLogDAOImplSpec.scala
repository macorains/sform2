package net.macolabo.sform2.model.daos

import play.api.libs.json._
import net.macolabo.sform2.helper.SformTestHelper
import net.macolabo.sform2.models.daos.TransferLogDAOImpl
import net.macolabo.sform2.models.entity.transfer.TransferLog
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback

import java.time.ZonedDateTime

class TransferLogDAOImplSpec extends FixtureAnyFlatSpec  with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {

  behavior of "TransferLog"

  it should "create" in { implicit session =>
    val transferLogDAO = new TransferLogDAOImpl()
    val transferTypeId = 1
    val transferLogId = transferLogDAO.create(transferTypeId)

    val f = TransferLog.syntax("f")
    val newTransferLog = withSQL (
      select(
        f.id,
        f.transfer_id,
        f.transfer_type_id,
        f.status,
        f.transfer_data,
        f.result_data,
        f.created,
        f.modified
      )
        .from(TransferLog as f)
        .where
        .eq(f.id, transferLogId)
    ).map(rs => TransferLog(rs)).single().apply()

    assert(newTransferLog.get.transfer_type_id.equals(transferTypeId))
  }

  it should "start" in { implicit session =>
    val transferLogDAO = new TransferLogDAOImpl()
    val f = TransferLog.syntax("f")

    val transferLog = withSQL (
      select(
        f.id,
        f.transfer_id,
        f.transfer_type_id,
        f.status,
        f.transfer_data,
        f.result_data,
        f.created,
        f.modified
      )
        .from(TransferLog as f)
    ).map(rs => TransferLog(rs)).list().apply().head

    val transferData = Json.toJson{"a" -> "b"}
    transferLogDAO.start(transferLog.id, 1, transferData.toString())

    val startedTransferLog = withSQL (
      select(
        f.id,
        f.transfer_id,
        f.transfer_type_id,
        f.status,
        f.transfer_data,
        f.result_data,
        f.created,
        f.modified
      )
        .from(TransferLog as f)
        .where
        .eq(f.id, transferLog.id)
    ).map(rs => TransferLog(rs)).single().apply().get
    assert(startedTransferLog.status.equals(2))
    assert(Json.parse(startedTransferLog.transfer_data).equals(transferData))
  }

//  it should "save" in {
//    val transferLogDAO = new TransferLogDAOImpl()
//
//  }

  it should "update" in { implicit session =>
    val transferLogDAO = new TransferLogDAOImpl()
    val f = TransferLog.syntax("f")

    val transferLog = withSQL (
      select(
        f.id,
        f.transfer_id,
        f.transfer_type_id,
        f.status,
        f.transfer_data,
        f.result_data,
        f.created,
        f.modified
      )
        .from(TransferLog as f)
    ).map(rs => TransferLog(rs)).list().apply().head

    val status = 3
    transferLogDAO.update(transferLog.id, status)

    val updatedTransferLog = withSQL (
      select(
        f.id,
        f.transfer_id,
        f.transfer_type_id,
        f.status,
        f.transfer_data,
        f.result_data,
        f.created,
        f.modified
      )
        .from(TransferLog as f)
        .where
        .eq(f.id, transferLog.id)
    ).map(rs => TransferLog(rs)).single().apply().get
    assert(updatedTransferLog.status.equals(3))
  }

  /*
    Fixture
  */
  override def fixture(implicit session: DBSession): Unit = {
    withSQL {
      val c = TransferLog.column
      insertInto(TransferLog).namedValues(
        c.id -> 1,
        c.transfer_id -> 1,
        c.transfer_type_id -> 1,
        c.status -> 1,
        c.transfer_data -> "{}",
        c.result_data -> "{}",
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.update().apply()
  }
}
