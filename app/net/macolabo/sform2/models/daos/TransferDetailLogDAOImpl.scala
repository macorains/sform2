package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.transfer.TransferDetailLog
import scalikejdbc._

import java.time.ZonedDateTime

class TransferDetailLogDAOImpl extends TransferDetailLogDAO {

  def save(postdata_id: Int, transfer_type_id: Int, status: Int, postdata: String, modified_postdata: String,
           result: Int, message: String, created: String, updated: String)(implicit session: DBSession): Long = {
    withSQL{
      val c = TransferDetailLog.column
      insertInto(TransferDetailLog).namedValues(
        c.postdata_id -> postdata_id,
        c.transfer_type_id -> transfer_type_id,
        c.status -> status,
        c.postdata -> postdata,
        c.modified_postdata -> modified_postdata,
        c.result -> result,
        c.message -> message,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }
}
