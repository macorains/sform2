package models.daos

import play.api.libs.json.{JsValue, Json}
import scalikejdbc._

class PostdataDAO {

  case class Postdata(postdata_id: Int, form_hashed_id: String, postdata: JsValue)
  object Postdata extends SQLSyntaxSupport[Postdata] {
    override val tableName = "D_POSTDATA"
    def apply(rs: WrappedResultSet): Postdata = {
      Postdata(rs.int("postdata_id"), rs.string("form_hashed_id"), Json.parse(rs.string("postdata")))
    }
  }

  private def getPostdataByFormHashedId(form_hashed_id: String, transfer_type_id: Int) = {
    DB localTx { implicit l =>
      sql"""SELECT pd.POSTDATA_ID, pd.FORM_HASHED_ID, pd.POSTDATA
           FROM D_POSTDATA pd
           LEFT JOIN D_TRANSFER_DETAIL_LOG tdl
             ON pd.POSTDATA_ID = tdl.POSTDATA_ID AND tdl.TRANSFER_TYPE_ID=$transfer_type_id
           WHERE pd.FORM_HASHED_ID=$form_hashed_id  AND tdl.ID IS NULL"""
        .map(rs => Postdata(rs)).list.apply()
    }
  }

  def getPostdata(form_hashed_id: String): List[Postdata] = {
    DB localTx { implicit l =>
      sql"""SELECT pd.POSTDATA_ID, pd.FORM_HASHED_ID, pd.POSTDATA
           FROM D_POSTDATA pd
           WHERE pd.FORM_HASHED_ID=$form_hashed_id  """
        .map(rs => Postdata(rs)).list.apply()
    }
  }

}
