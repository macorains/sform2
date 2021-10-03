package net.macolabo.sform2.models.daos

import net.macolabo.sform2.models.entity.Postdata
import net.macolabo.sform2.models.entity.transfer.TransferDetailLog
import scalikejdbc._

class PostdataDAOImpl extends PostdataDAO {
  def getPostdataByFormHashedId(form_hashed_id: String, transfer_type_id: Int)(implicit session: DBSession): List[Postdata] = {
    withSQL {
      val pd = Postdata.syntax("pd")
      val tdl = TransferDetailLog.syntax("tdl")
      select(
        pd.postdata_id,
        pd.form_hashed_id,
        pd.postdata,
        pd.user_group,
        pd.created_user,
        pd.created,
        pd.modified_user,
        pd.modified
      )
        .from(Postdata as pd)
        .leftJoin(TransferDetailLog as tdl)
        .on(pd.postdata_id, tdl.postdata_id)
        .where
        .eq(pd.form_hashed_id, form_hashed_id)
        .and
        .eq(tdl.transfer_type_id, transfer_type_id)
    }.map(rs => Postdata(rs)).list().apply()
  }

  def getPostdata(form_hashed_id: String)(implicit session: DBSession): List[Postdata] = {
    withSQL {
      val pd = Postdata.syntax("pd")
      select(
        pd.postdata_id,
        pd.form_hashed_id,
        pd.postdata,
        pd.user_group,
        pd.created_user,
        pd.created,
        pd.modified_user,
        pd.modified
      )
        .from(Postdata as pd)
        .where
        .eq(pd.form_hashed_id, form_hashed_id)
    }.map(rs => Postdata(rs)).list().apply()
  }
}
