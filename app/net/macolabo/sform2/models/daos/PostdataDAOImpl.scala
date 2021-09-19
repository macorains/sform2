package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.Postdata
import scalikejdbc._

class PostdataDAOImpl extends PostdataDAO {
  def getPostdataByFormHashedId(form_hashed_id: String, transfer_type_id: Int): List[Postdata] = {
    DB localTx { implicit l =>
      sql"""SELECT pd.POSTDATA_ID, pd.FORM_HASHED_ID, pd.POSTDATA
           FROM D_POSTDATA pd
           LEFT JOIN D_TRANSFER_DETAIL_LOG tdl
             ON pd.POSTDATA_ID = tdl.POSTDATA_ID AND tdl.TRANSFER_TYPE_ID=$transfer_type_id
           WHERE pd.FORM_HASHED_ID=$form_hashed_id  AND tdl.ID IS NULL"""
        .map(rs => Postdata(rs)).list().apply()
    }
  }

  def getPostdata(form_hashed_id: String): List[Postdata] = {
    DB localTx { implicit l =>
      sql"""SELECT pd.POSTDATA_ID, pd.FORM_HASHED_ID, pd.POSTDATA
           FROM D_POSTDATA pd
           WHERE pd.FORM_HASHED_ID=$form_hashed_id  """
        .map(rs => Postdata(rs)).list().apply()
    }
  }
}
