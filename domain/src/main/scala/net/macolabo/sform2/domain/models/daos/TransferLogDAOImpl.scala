package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.transfer.TransferLog
import scalikejdbc._

import java.time.ZonedDateTime

class TransferLogDAOImpl extends TransferLogDAO {

  def create(transfer_type_id: Int)(implicit session: DBSession): Long = {
    withSQL {
      val c = TransferLog.column
      insertInto(TransferLog).namedValues(
        c.transfer_id -> 0,
        c.transfer_type_id -> transfer_type_id,
        c.status -> 0,
        c.transfer_data -> "{}",
        c.result_data -> "{}",
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  def start(id: BigInt, transfer_id: Int, transfer_data: String)(implicit session: DBSession): Int = {
    withSQL {
      val c = TransferLog.column
      QueryDSL.update(TransferLog).set(
        c.transfer_id -> transfer_id,
        c.status -> 2,
        c.transfer_data -> transfer_data,
        c.modified -> ZonedDateTime.now()
      ).where
        .eq(c.id, id)
    }.update().apply()
  }

  //  def save(transfer_id: Int, transfer_type_id: Int, status: Int, transfer_data: String, result_data: String)(implicit session: DBSession) :Long= {
  //      sql"""
  //      INSERT INTO D_TRANSFER_LOG
  //      (TRANSFER_ID,TRANSFER_TYPE_ID,STATUS,TRANSFER_DATA,RESULT_DATA,CREATED,MODIFIED)
  //      VALUES (${transfer_id},${transfer_type_id},${status},${transfer_data},${result_data},NOW(),NOW())"""
  //          .updateAndReturnGeneratedKey().apply()
  //  }

  def update(id: BigInt, status: Int)(implicit session: DBSession): Int = {
    withSQL {
      val c = TransferLog.column
      QueryDSL.update(TransferLog).set(
        c.status -> status
      ).where
        .eq(c.id, id)
    }.update().apply()
  }
}
