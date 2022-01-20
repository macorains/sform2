package net.macolabo.sform2.models.daos

import scalikejdbc._

trait TransferLogDAO {
  def create(transfer_type_id: Int)(implicit session: DBSession): Long
  def start(id: BigInt, transfer_id: Int, transfer_data: String)(implicit session: DBSession): Int
  //  def save(transfer_id: Int, transfer_type_id: Int, status: Int, transfer_data: String, result_data: String)(implicit session: DBSession) :Long
  def update(id: BigInt, status: Int)(implicit session: DBSession): Int
}
