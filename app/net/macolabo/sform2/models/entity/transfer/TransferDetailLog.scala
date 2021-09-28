package net.macolabo.sform2.models.entity.transfer

import scalikejdbc._

case class TransferDetailLog(id: Int, postdata_id: Int, transfer_type_id: Int, status: Int, postdata: String,
                             modified_postdata: String, result: Int, message: String, created: String, updates: String)
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
      rs.int("result"),
      rs.string("message"),
      rs.string("creared"),
      rs.string("updated"))
  }
}
