package models.daos

import scalikejdbc._
import scalikejdbc.{ DB, WrappedResultSet }

class TransferLogDAO {

  case class TransferLog(id: Int, transfer_id: Int, transfer_type_id: Int, transfer_data: String,
    result_data: String, created: Option[String], updates: Option[String])
  object TransferLog extends SQLSyntaxSupport[TransferLog] {
    override val tableName = "D_TRANSFER_LOG"
    def apply(rs: WrappedResultSet): TransferLog = {
      TransferLog(rs.int("id"), rs.int("transfer_id"), rs.int("transfer_type_id"), rs.string("transfer_data"),
        rs.string("result_data"), rs.stringOpt("creared"), rs.stringOpt("updated"))
    }
  }

  def create(transfer_type_id: Int): Long = {
    DB localTx { implicit l =>
      sql"""
      INSERT INTO D_TRANSFER_LOG
      (TRANSFER_ID,TRANSFER_TYPE_ID,STATUS,TRANSFER_DATA,RESULT_DATA,CREATED,MODIFIED)
      VALUES (0,${transfer_type_id},0,'{}','{}',NOW(),NOW())"""
        .updateAndReturnGeneratedKey().apply()
    }
  }

  def start(id: Long, transfer_id: Int, transfer_data: String) = {
    DB localTx { implicit l =>
      sql"""
      UPDATE D_TRANSFER_LOG
      SET TRANSFER_ID = ${transfer_id}, STATUS = 2, TRANSFER_DATA = ${transfer_data}, MODIFIED = NOW()
      WHERE ID = ${id}"""
        .update.apply()
    }
  }

  //  def save(transfer_id: Int, transfer_type_id: Int, status: Int, transfer_data: String, result_data: String) :Long= {
  //    DB localTx { implicit l =>
  //      sql"""
  //      INSERT INTO D_TRANSFER_LOG
  //      (TRANSFER_ID,TRANSFER_TYPE_ID,STATUS,TRANSFER_DATA,RESULT_DATA,CREATED,MODIFIED)
  //      VALUES (${transfer_id},${transfer_type_id},${status},${transfer_data},${result_data},NOW(),NOW())"""
  //          .updateAndReturnGeneratedKey().apply()
  //    }
  //  }

  def update(id: Long, status: Int) = {
    DB localTx { implicit l =>
      sql"""
      UPDATE D_TRANSFER_LOG
      SET STATUS = ${status}
      WHERE ID = ${id}
        """
        .update.apply()
    }
  }

}
