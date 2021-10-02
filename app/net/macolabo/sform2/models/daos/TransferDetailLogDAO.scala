package net.macolabo.sform2.models.daos

import scalikejdbc._

trait TransferDetailLogDAO {

  def save(postdata_id: Int, transfer_type_id: Int, status: Int, postdata: String, modified_postdata: String,
    result: Int, message: String, created: String, updated: String)(implicit session: DBSession): Long

}
