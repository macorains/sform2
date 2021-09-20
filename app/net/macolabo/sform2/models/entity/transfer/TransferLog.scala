package net.macolabo.sform2.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferLog(
                        id: Int,
                        transfer_id: Int,
                        transfer_type_id: Int,
                        status: Int,
                        transfer_data: String,
                        result_data: String,
                        created: ZonedDateTime,
                        modified: ZonedDateTime
                      )

object TransferLog extends SQLSyntaxSupport[TransferLog] {
  override val tableName = "D_TRANSFER_LOG"
  def apply(rs: WrappedResultSet): TransferLog = {
    TransferLog(
      rs.int("id"),
      rs.int("transfer_id"),
      rs.int("transfer_type_id"),
      rs.int("status"),
      rs.string("transfer_data"),
      rs.string("result_data"),
      rs.dateTime("creared"),
      rs.dateTime("modified"))
  }
}
