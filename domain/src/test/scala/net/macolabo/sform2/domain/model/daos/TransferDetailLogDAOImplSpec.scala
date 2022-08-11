package net.macolabo.sform2.domain.model.daos

import net.macolabo.sform2.domain.model.helper.SformTestHelper
import net.macolabo.sform2.domain.models.daos.TransferDetailLogDAOImpl
import net.macolabo.sform2.domain.models.entity.transfer.TransferDetailLog
import org.scalatest.flatspec.FixtureAnyFlatSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{select, withSQL}

import java.time.ZonedDateTime

class TransferDetailLogDAOImplSpec extends FixtureAnyFlatSpec  with GuiceOneServerPerSuite with SformTestHelper with AutoRollback {

  behavior of "TransferLog"

  it should "save" in { implicit session =>
    val transferDetailLogDAO = new TransferDetailLogDAOImpl()
    val postdataId = 9999
    val transferTypeId = 1
    val status = 1
    val postdata = """{"hoge":"fuga"}"""
    val modifiedPostdata = """{"hoge":"fuga"}"""
    val resultCode = 0
    val message = """{"message":"aaa"}"""
    val created = ZonedDateTime.now()
    val modified = ZonedDateTime.now()

    val id = transferDetailLogDAO.save(postdataId, transferTypeId, status, postdata, modifiedPostdata, resultCode, message, created, modified)

    val f = TransferDetailLog.syntax("f")
    val newTransferDetailLog = withSQL (
      select(
        f.id,
        f.postdata_id,
        f.transfer_type_id,
        f.status,
        f.postdata,
        f.modified_postdata,
        f.result_code,
        f.message,
        f.user_group,
        f.created_user,
        f.created,
        f.modified_user,
        f.modified
      )
        .from(TransferDetailLog as f)
        .where
        .eq(f.id, id)
    ).map(rs => TransferDetailLog(rs)).single().apply().get

    assert(newTransferDetailLog.postdata_id.equals(postdataId))
    assert(newTransferDetailLog.transfer_type_id.equals(transferTypeId))
    assert(newTransferDetailLog.status.equals(status))
    assert(Json.parse(newTransferDetailLog.postdata).equals(Json.parse(postdata)))
    assert(Json.parse(newTransferDetailLog.modified_postdata).equals(Json.parse(modifiedPostdata)))
    assert(newTransferDetailLog.result_code.equals(resultCode))
    assert(Json.parse(newTransferDetailLog.message).equals(Json.parse(message)))
  }
}
