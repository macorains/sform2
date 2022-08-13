package net.macolabo.sform2.domain.models.daos

import scalikejdbc._

import java.time.ZonedDateTime

trait TransferDetailLogDAO {

  def save(
            postdataId: Int,
            transferTypeId: Int,
            status: Int,
            postdata: String,
            modifiedPostdata: String,
            resultCode: Int,
            message: String,
            created: ZonedDateTime,
            modified: ZonedDateTime
          )(implicit session: DBSession): Long

}
