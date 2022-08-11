package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.Postdata
import scalikejdbc._

trait PostdataDAO {

  /** HashedFormIdによるpostdata一覧取得 */
  def getPostdataByFormHashedId(form_hashed_id: String, transfer_type_id: Int)(implicit session: DBSession): List[Postdata]

  /** postdata一覧取得 */
  def getPostdata(form_hashed_id: String)(implicit session: DBSession): List[Postdata]

}
