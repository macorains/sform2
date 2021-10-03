package net.macolabo.sform2.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferDetailLog(
                              id: Int,
                              postdata_id: Int,
                              transfer_type_id: Int,
                              status: Int,
                              postdata: String,
                              modified_postdata: String,
                              result_code: Int,
                              message: String,
                              user_group: String,
                              created_user: String,
                              created: ZonedDateTime,
                              modified_user: String,
                              modified: ZonedDateTime
                            )

object TransferDetailLog extends SQLSyntaxSupport[TransferDetailLog] {
  override val tableName = "D_TRANSFER_DETAIL_LOG"
  def apply(rs: WrappedResultSet): TransferDetailLog = {
    TransferDetailLog(
      rs.int("id"),
      rs.int("postdata_id"),
      rs.int("transfer_type_id"),
      rs.int("status"),
      rs.string("postdata"),
      rs.string("modified_postdata"),
      rs.int("result_code"),
      rs.string("message"),
      rs.string("user_group"),
      rs.string("created_user"),
      rs.dateTime("created"),
      rs.string("modified_user"),
      rs.dateTime("modified")
    )
  }
}
