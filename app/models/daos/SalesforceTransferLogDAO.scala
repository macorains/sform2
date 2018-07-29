package models.daos

import scalikejdbc._
import scalikejdbc.{ DB, WrappedResultSet }

class SalesforceTransferLogDAO {
  case class SalesforceTransferLog(id: Int, postdata_id: Int, postdata: String, modified_postdata: String,
    result: Int, message: String, created: String, updates: String)
  object SalesforceTransferLog extends SQLSyntaxSupport[SalesforceTransferLog] {
    override val tableName = "d_salesforce_transfer_log"
    def apply(rs: WrappedResultSet): SalesforceTransferLog = {
      SalesforceTransferLog(rs.int("id"), rs.int("postdata_id"), rs.string("postdata"), rs.string("modified_postdata"),
        rs.int("result"), rs.string("message"), rs.string("creared"), rs.string("updated"))
    }
  }

  def save(postdata_id: Int, postdata: String, modified_postdata: String, result: Int, message: String, created: String, updated: String) = {
    DB localTx { implicit l =>
      sql"""UPDATE D_SALESFORCE_TRANSFER_LOG SET
      (POSTDATA_ID,POSTDATA,MODIFIED_POSTDATA,RESULT,MESSAGE,CREATED,MODIFIED)
      VALUES (${postdata_id},${postdata},${modified_postdata},${result},${message},NOW(),NOW())"""
        .update.apply()
    }
  }

}
