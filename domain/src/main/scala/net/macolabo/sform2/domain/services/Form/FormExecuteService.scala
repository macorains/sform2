package net.macolabo.sform2.domain.services.Form

import org.apache.pekko.actor.ActorRef
import com.google.inject.Inject
import net.macolabo.sform2.domain.models.daos.{FormColDAO, FormColSelectDAO, FormColValidationDAO, FormDAO, PostdataDAO}
import net.macolabo.sform2.domain.models.entity.form.{Form, FormCol, FormColSelect}
import net.macolabo.sform2.domain.services.Form.load.{FormLoadRequest, FormLoadRequestJson}
import net.macolabo.sform2.domain.services.Form.post.{FormPostRequest, FormPostRequestJson}
import net.macolabo.sform2.domain.services.Form.validate.FormValidateResultResponse
import net.macolabo.sform2.domain.services.Transfer.TransferReceiver.NewTaskRequest
import net.macolabo.sform2.domain.utils.forms.{FormColType_Checkbox, FormColType_Combo, FormColType_DisplayText, FormColType_Hidden, FormColType_Radio, FormColType_Text, FormColType_TextArea}
import play.api.cache.SyncCacheApi
import play.api.libs.json.JsValue
import scalikejdbc.DB

import javax.inject.Named

class FormExecuteService @Inject()(
  cache:  SyncCacheApi,
  formDAO: FormDAO,
  formColDAO: FormColDAO,
  formColSelectDAO: FormColSelectDAO,
  formColValidationDAO: FormColValidationDAO,
  postdataDAO: PostdataDAO,
  @Named("actor_transfer_receiver") transferReceiver: ActorRef
)
extends FormLoadRequestJson with FormPostRequestJson
{
  /**
   * load：以下のようなHTMLを生成して返す
   * <div class="sform-form-wrapper">
   *   <div class="sform-header">
   *     ヘッダー文字列
   *   </div>
   *   <div class="sform-col">
   *
   *     <!-- 通常の入力要素を出力の場合-->
   *     <div class="sform-col-name">項目名</div>
   *     <div class="sform-col-input">
   *       <input type="text" name="hoge">
   *       <div class="sform-col-input-error">エラーメッセージ</div>
   *     </div>
   *
   *     <!-- 表示テキストを出力の場合 -->
   *     <div class="sform-col-display-text">表示文字列</div>
   *
   *     <!-- hidden項目を出力の場合 -->
   *     <div class="sform-col-hidden">
   *       <input type="hidden" value="hogehoge">
   *     </div>
   *
   *   </div>
   *   <div class="sform-footer">
   *     ボタンとか
   *   </div>
   * </div>
   *
   * @param load_request リクエスト
   * @param host ホスト
   */
  def load(load_request: JsValue, host: String): Option[String] = {
    DB.localTx(implicit session => {
      load_request.validate[FormLoadRequest].map(request => {
        formDAO.get(request.hashed_form_id).map(formdata => {
          // form.getForm(request.hashed_form_id).map(formdata => {
          createInputForm(formdata, request.cache_id, Map.empty[String, String])
        })
      }).getOrElse(None)
    })
  }

  /**
   * バリデート
   * @param post_request リクエスト
   * @param host ホスト名
   */
  def validate(post_request: JsValue, host: String): Option[FormValidateResultResponse] = {
    DB.localTx(implicit session => {
      post_request.validate[FormPostRequest].map(request => {
        formDAO.get(request.hashed_form_id).map(formdata => {
          val validateResult = formColDAO.getList(formdata.id).flatMap(formCol => validateCols(formCol, request.postdata)).filter(c => c._2.nonEmpty).toMap
          val cache_id = setValidateCache(request, validateResult)
          FormValidateResultResponse(cache_id, validateResult)
        })
      }).getOrElse(None)
    })
  }

  /**
   * 確認画面HTML取得
   */
  def confirm(post_request: JsValue, host: String): Option[String] = {
    // TODO Validate経由ではない呼び出しにも対応させる
    DB.localTx(implicit session => {
      post_request.validate[FormPostRequest].map(request => {
        formDAO.get(request.hashed_form_id).map(formdata => {
          createConfirmForm(formdata, request.postdata)
        })
      }).getOrElse(None)
    })
  }

  /**
   * フォームデータ送信＆完了画面HTML取得
   */
  def complete(post_request: JsValue, host: String): Option[String] = {
    DB.localTx(implicit session => {
      post_request.validate[FormPostRequest].map(request => {
        formDAO.get(request.hashed_form_id).map(formdata => {
          request.cache_id.map(cid => {
            cache.get[FormPostRequest](cid).map(formPostRequest => {
              formPostRequest.postdata.map(postdata => {
                // データ保存
                val postdataId = postdataDAO.save(request.hashed_form_id, postdata.toString())
                // Transfer実行
                // TODO 修正必要
                // transferReceiver ! NewTaskRequest(postdata, postdataId, request.hashed_form_id)
                // 完了画面の出力
                createCompleteForm(formdata)
              })
            })
          })
        })
      }).getOrElse(None).flatten.flatten.flatten
    })
  }

  def setValidateCache(request: FormPostRequest, validateResult: Map[String,String]): Option[String] = {
    if(validateResult.isEmpty) {
      val id = java.util.UUID.randomUUID.toString
      cache.set(id, request)
      Some(id)
    } else None
  }

  /**
   * 各フォーム項目のバリデート実行
   * @param formColData フォーム項目データ
   * @param postdata 送信データ
   * @return フォーム項目IDとエラーメッセージのMap
   */
  def validateCols(formColData: FormCol, postdata: Option[JsValue]) :Map[String,String] = {
    DB.localTx(implicit session => {
      val errorMessage = postdata.flatMap(post => {
        val postValue = post \ formColData.col_id
        formColValidationDAO.get(formColData.user_group, formColData.form_id, formColData.id).map(validation => {
          val postValueString = postValue.validate[String].getOrElse("")
          val postvalueStringLength = postValueString.length
          val postvalueInt = optToInt(postValueString)
          List(
            if (validation.required && (!postValue.isDefined || postValue.isEmpty || postValueString.isBlank)) s"${formColData.name}は入力必須項目です" else "",
            lengthCheck(formColData.name, validation.min_length, validation.max_length, postvalueStringLength),
            rangeCheck(formColData.name, validation.min_value, validation.max_value, postvalueInt)
         ).filter(msg => msg.nonEmpty).mkString("<br>")
        })
      }).getOrElse("")
      Map(formColData.col_id -> errorMessage)
    })
  }

  /**
   * 数値範囲チェック
   */
  def rangeCheck(colName: String, minValue: Option[Int], maxValue: Option[Int], postValue: Option[Int]): String = {
    postValue.map(pv => {
      (minValue, maxValue) match {
        case (Some(x), Some(y)) => if(pv > y || pv < x) s"${colName}は${x}以上${y}以下でなければなりません" else ""
        case (Some(x), None) => if(pv < x) s"${colName}は${x}以上でなければなりません" else ""
        case (None, Some(y)) => if(pv > y) s"${colName}は${y}以下でなければなりません" else ""
        case _ => ""
      }
    }).getOrElse(if(minValue.nonEmpty || maxValue.nonEmpty) s"${colName}は数値でなければなりません" else "")

  }

  /**
   * 文字長範囲チェック
   */
  def lengthCheck(colName: String, minLength: Option[Int], maxLength: Option[Int], postValueLength: Int): String = {
    (minLength, maxLength) match {
      case (Some(x), Some(y)) => if(postValueLength > y || postValueLength < x) s"${colName}は${x}文字以上${y}文字以下でなければなりません" else ""
      case (Some(x), None) => if(postValueLength < x) s"${colName}は${x}文字以上でなければなりません" else ""
      case (None, Some(y)) => if(postValueLength > y) s"${colName}は${y}文字以下でなければなりません" else ""
      case _ => ""
    }
  }

  /**
   * 入力フォーム作成
   */
  def createInputForm(formdata: Form, cache_id: Option[String], validate_result: Map[String,String]): String = {
    val form_post_request = cache_id.flatMap(cid => cache.get[FormPostRequest](cid))
    val form_cols = createInputFormCol(formdata.id, form_post_request, validate_result)
    s"""
       |<div class="sform-form-wrapper">
       |  <div class="sform-header">
       |    ${formdata.input_header.getOrElse("")}
       |  </div>
       |  <div class="sform-body">$form_cols
       |  </div>
       |  <div class="sform_button_div">
       |    <button type="button" id="sform_button_cancel">キャンセル</button>
       |    <button type="button" id="sform_button_confirm">次へ</button>
       |  </div>
       |</div>""".stripMargin
  }

  /**
   * 入力フォーム行作成
   */
  def createInputFormCol(form_id: BigInt, form_post_request: Option[FormPostRequest], validate_result: Map[String,String]): String = {
    DB.localTx(implicit session => {
      formColDAO.getList(form_id).map(formCol => {
        val posted_value = form_post_request.flatMap(request => request.postdata.flatMap(postdata => getPostedValue(formCol.col_id, postdata))).getOrElse("")
        formCol.col_type match {
          case t if t == FormColType_Text.col_type =>
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formCol.name}</div>
               |        <div class="sform-col-input">
               |          <input type="text" name="${formCol.col_id}" value="$posted_value">
               |          <div class="sform-col-input-error">
               |            ${validate_result.getOrElse(formCol.col_id, "")}
               |          </div>
               |       </div>
               |    </div>""".stripMargin
          case t if t == FormColType_Combo.col_type =>
            val options = createFormColCombo(form_id, formCol, posted_value)
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formCol.name}</div>
               |        <div class="sform-col-input">
               |          <select name="${formCol.col_id}">
               |            $options
               |          </select>
               |          <div class="sform-col-input-error">
               |            ${validate_result.getOrElse(formCol.col_id, "")}
               |          </div>
               |       </div>
               |    </div>""".stripMargin
          case t if t == FormColType_Checkbox.col_type =>
            val options = createFormColCheckbox(form_id, formCol, posted_value)
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formCol.name}</div>
               |        <div class="sform-col-input">
               |          $options
               |          <div class="sform-col-input-error">
               |            ${validate_result.getOrElse(formCol.col_id, "")}
               |          </div>
               |       </div>
               |    </div>""".stripMargin
          case t if t == FormColType_Radio.col_type =>
            val options = createFormColRadio(form_id, formCol, posted_value)
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formCol.name}</div>
               |        <div class="sform-col-input">
               |            $options
               |          <div class="sform-col-input-error">
               |            ${validate_result.getOrElse(formCol.col_id, "")}
               |          </div>
               |       </div>
               |    </div>""".stripMargin
          case t if t == FormColType_TextArea.col_type =>
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formCol.name}</div>
               |        <div class="sform-col-input">
               |          <textarea name="hoge">$posted_value</textarea>
               |          <div class="sform-col-input-error">
               |            ${validate_result.getOrElse(formCol.col_id, "")}
               |          </div>
               |       </div>
               |    </div>""".stripMargin
          case t if t == FormColType_Hidden.col_type =>
            s"""
               |    <div class="sform-col-hidden">
               |      <input type="hidden" name="hoge" value="${formCol.default_value}"
               |    </div>""".stripMargin
          case t if t == FormColType_DisplayText.col_type =>
            s"""
               |    <div class="sform-col-display-text">
               |      ${formCol.default_value}
               |    </div>""".stripMargin
        }
      }).mkString
    })
  }

  def createConfirmForm(formData: Form, postData: Option[JsValue]): String= {
    DB.localTx(implicit session => {
      val formCols = formColDAO.getList(formData.id).map(formcol => {
        createConfirmCol(formcol, postData)
      }).mkString
      s"""
         |<div class="sform-form-wrapper">
         |  <div class="sform-header">
         |    ${formData.confirm_header.getOrElse("")}

         |

         |  <div class="sform-body">$formCols


           </div>
         |  <div class="sf
         on_div">
         |    <button type="button" id="sform_button_b
         /button>
         |    <button type="button" id="sform_button_sub
         /button>
         |    <input type="hidden"
         rm_tmp">

           </div>
         |</div>""".
        stripMargin
      })
  }

  def createConfirmCol(formColData: FormCol, postdata: Option[JsValue]): String = {
    DB.localTx(implicit session => {
      postdata.map(post => {
        val postValue = getPostedValue(formColData.col_id, post).getOrElse("")
        val formColSelectData = formColSelectDAO.getList(formColData.form_id, formColData.id)
        formColData.col_type match {
          case t if t == FormColType_Text.col_type =>
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formColData.name}</div>
               |      <div class="sform-col-form"><span id="${formColData.col_id}">$postValue</span></div>
               |    </div>""".stripMargin
          case t if t == FormColType_Combo.col_type || t == FormColType_Checkbox.col_type || t == FormColType_Radio.col_type =>
            val values = postValue.split(",").map(v => formColSelectData.filter(d => d.select_value.equals(v)).map(d => d.select_name)).mkString(",")
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formColData.name}</div>
               |      <div class="sform-col-form"><span id="${formColData.col_id}">$values</span></div>
               |    </div>""".stripMargin
          case t if t == FormColType_TextArea.col_type =>
            s"""
               |    <div class="sform-col">
               |      <div class="sform-col-name">${formColData.name}</div>
               |      <div class="sform-col-form"><span id="${formColData.col_id}">$postValue</span></div>
               |    </div>""".stripMargin
          case t if t == FormColType_Hidden.col_type =>
            s"""
               |    <div class="sform-col-none">
               |      <div class="sform-col-name-none"></div>
               |      <div class="sform-col-form"></div>
               |    </div>""".stripMargin
          case t if t == FormColType_DisplayText.col_type =>
            s"""
               |    <div class="sform-col-disptext">
               |      <div class="sform-col-name-none"></div>
               |      <div class="sform-col-form">${formColData.default_value}</div>
               |    </div>""".stripMargin
        }
      }).getOrElse("")
    })
  }

  def getPostedValue(col_id: String, postdata: JsValue): Option[String] =  (postdata \ col_id).asOpt[String]

  def getSelected(form_col_select: FormColSelect, posted_value:String): Boolean = {
    val defaultValue = (posted_value,form_col_select.is_default) match {
      case v if v._1.isEmpty && v._2 => form_col_select.select_value
      case v if v._1.nonEmpty => v._1
      case _ => ""
    }
    defaultValue.nonEmpty && defaultValue.equals(form_col_select.select_value)
  }

  def createFormColCombo(form_id: BigInt, formCol: FormCol, posted_value: String): String = {
    DB.localTx(implicit session => {
      formColSelectDAO.getList(form_id, formCol.id).map(formColSelect => {
        val selected = if (getSelected(formColSelect, posted_value)) "selected" else ""
        s"""
           |<option value="${formColSelect.select_value}" name="${formCol.col_id}" $selected>${formColSelect.select_name}</option>
           |""".stripMargin
      }).mkString
    })
  }

  def createFormColCheckbox(form_id: BigInt, formCol:FormCol, posted_value: String): String = {
    DB.localTx(implicit session => {
      formColSelectDAO.getList(form_id, formCol.id).map(formColSelect => {
        val selected = if(getSelected(formColSelect, posted_value)) "checked" else ""
        s"""
           |<input type="checkbox" name="${formCol.col_id}" value="${formColSelect.select_value}" $selected>${formColSelect.select_name}
           |""".stripMargin
      }).mkString
    })
  }

  def createFormColRadio(form_id: BigInt, formCol:FormCol, posted_value: String): String = {
    DB.localTx(implicit session => {
      formColSelectDAO.getList(form_id, formCol.id).map(formColSelect => {
        val selected = if(getSelected(formColSelect, posted_value)) "checked" else ""
        s"""
           |<input type="radio" name="${formCol.col_id}" value="${formColSelect.select_value}" $selected>${formColSelect.select_name}
           |""".stripMargin
      }).mkString
    })
  }


  def createCompleteForm(formdata: Form): String = {
    s"""
       |<div class="sform-form-wrapper">
       |  <div class="sform-header">
       |    ${formdata.complete_text.getOrElse("")}
       |  </div>
       |  <div class="sform-body">
       |    <button type="button" onClick="location.href='${formdata.complete_url}'">完了</button>
       |  </div>
       |  <div class="sform_button_div">
       |    <button type="button" id="sform_button_finish">完了</button>
       |  </div>
       |</div>""".stripMargin
  }

  def createErrorForm(): String = {
    s"""
       |<div class="sform-form-wrapper">
       |  <div class="sform-header">
       |    登録時にエラーが発生しました。繰り返しエラーが発生する場合は管理者にお問い合わせください。
       |  </div>
       |  <div class="sform-body">
       |  </div>
       |</div>""".stripMargin
  }

  def optToInt(s:String): Option[Int] = {
    try{
      Some(s.toInt)
    } catch {
      case e: NumberFormatException => None
    }
  }
}
