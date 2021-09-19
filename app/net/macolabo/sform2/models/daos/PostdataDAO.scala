package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.Postdata
import scalikejdbc._

trait PostdataDAO {

  /** HashedFormIdによるpostdata一覧取得 */
  def getPostdataByFormHashedId(form_hashed_id: String, transfer_type_id: Int): List[Postdata]

  /** postdata一覧取得 */
  def getPostdata(form_hashed_id: String): List[Postdata]

}
