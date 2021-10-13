package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.transfer.TransferDetailLog
import scalikejdbc._

import java.time.ZonedDateTime

class TransferDetailLogDAOImpl extends TransferDetailLogDAO {

  def save(postdataId: Int,
           transferTypeId: Int,
           status: Int,
           postdata: String,
           modifiedPostdata: String,
           resultCode: Int,
           message: String,
           created: ZonedDateTime,
           modified: ZonedDateTime
          )(implicit session: DBSession): Long = {
    withSQL{
      val c = TransferDetailLog.column
      insertInto(TransferDetailLog).namedValues(
        c.postdata_id -> postdataId,
        c.transfer_type_id -> transferTypeId,
        c.status -> status,
        c.postdata -> postdata,
        c.modified_postdata -> modifiedPostdata,
        c.result_code -> resultCode,
        c.message -> message,
        c.created -> ZonedDateTime.now(),
        c.modified -> ZonedDateTime.now()
      )
    }.updateAndReturnGeneratedKey().apply()
  }
}
