package net.macolabo.sform2.services.FormPostData

import com.google.inject.Inject
import net.macolabo.sform2.models.daos.{FormDAO, PostdataDAO}
import net.macolabo.sform2.models.entity.user.User
import net.macolabo.sform2.models.entity.form.{Form, FormCol}
import play.api.libs.json.{JsArray, JsNull, JsObject, JsString, Json}
import scalikejdbc.DB

class FormPostDataService @Inject()(
  postdataDAO: PostdataDAO,
  formDAO: FormDAO
                                   ){

  def getData(hashed_form_id: String, identity: User): String = {

    DB.localTx(implicit session => {

      val formColMap = identity.group.map(group => {
        val form = formDAO.getByHashedId(identity, hashed_form_id)
        val colIdMap = form.map(f => {
          f.form_cols.map(formCol => (formCol.col_id, formCol.name))
        })
        (form.map(f => f.id), colIdMap)
      })

      val data = formColMap.flatMap(fcm => {
        fcm._1.flatMap(f => {
          fcm._2 match {
            case Some(x) => {
              val dataList = JsArray(postdataDAO.getPostdata(hashed_form_id).map(pd => {
                JsObject(x.map(col => {
                  col._1 -> (pd.postdata \ col._1).getOrElse(JsNull)
                }))
              }))
              Some((x, dataList))
            }
            case None => Some((List.empty, JsArray(List.empty)))
          }
        })
      }).getOrElse((List.empty, JsArray(List.empty)))

      val result = JsObject(Seq(
        "header" -> JsObject(data._1.map(x => (x._1, JsString(x._2)))),
        "data" -> data._2
      ))
      Json.stringify(result)
    })
  }
}
