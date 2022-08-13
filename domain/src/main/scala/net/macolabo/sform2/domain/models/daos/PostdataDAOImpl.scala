package net.macolabo.sform2.domain.models.daos

import net.macolabo.sform2.domain.models.entity.Postdata
import net.macolabo.sform2.domain.models.entity.transfer.TransferDetailLog
import scalikejdbc._
import java.util.Date

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

  /**
   * フォーム送信データ保存
   * @param form_id フォームID
   * @param postdata Ajaxからの送信データをJSON文字列にしたもの
   * @return
   */
  def save(form_id: String, postdata: String)(implicit session: DBSession): Long = {
    val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
    withSQL {
      val p = Postdata.column
      insertInto(Postdata).namedValues(
        p.form_hashed_id -> form_id,
        p.postdata -> postdata,
        p.created -> now,
        p.modified -> now
      )
    }.updateAndReturnGeneratedKey().apply()
  }

}
