package net.macolabo.sform2.models.entity.transfer

import scalikejdbc._

import java.time.ZonedDateTime

case class TransferLog(
                        id: BigInt,
                        transfer_id: Int,
                        transfer_type_id: Int,
                        status: Int,
                        transfer_data: String,
                        result_data: String,
                        created: ZonedDateTime,
                        modified: ZonedDateTime
                      )

object TransferLog extends SQLSyntaxSupport[TransferLog] {
  override val tableName = "d_transfer_log"
  def apply(rs: WrappedResultSet): TransferLog = {
    TransferLog(
      rs.bigInt("id"),
      rs.int("transfer_id"),
      rs.int("transfer_type_id"),
      rs.int("status"),
      rs.string("transfer_data"),
      rs.string("result_data"),
      rs.dateTime("created"),
      rs.dateTime("modified"))
  }
}
