package net.macolabo.sform2.models.daos

import scalikejdbc.{ DB, WrappedResultSet, _ }

class TransferDetailLogDAO {

  def save(postdata_id: Int, transfer_type_id: Int, status: Int, postdata: String, modified_postdata: String,
    result: Int, message: String, created: String, updated: String): Int = {
    DB localTx { implicit l =>
      sql"""INSERT INTO D_TRANSFER_DETAIL_LOG
      (POSTDATA_ID,TRANSFER_TYPE_ID,STATUS,POSTDATA,MODIFIED_POSTDATA,RESULT,MESSAGE,CREATED,MODIFIED)
      VALUES ($postdata_id,$transfer_type_id,$status,$postdata,$modified_postdata,$result,$message,
      NOW(),NOW())"""
        .update().apply()
    }
  }

}
