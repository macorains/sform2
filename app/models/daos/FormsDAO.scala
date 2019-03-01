package models.daos

import java.io.StringWriter
import java.util.{Date, UUID}

import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{OutputKeys, Transformer, TransformerFactory}

import scala.collection.{Map, Seq}
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory
import org.w3c.dom.{Document, Element}
import play.api.libs.json._
import scalikejdbc._
import models.{RsResultSet, User}
import models.entity.Form
import models.json.FormJson
import utils.forms.FormParts

class FormsDAO extends FormParts with FormJson{

  /**
   * フォームデータ取得
   * @param identity 認証情報
   * @param hashed_id フォームのhashed_id
   * @return フォームデータ
   */
  def getData(identity: User, hashed_id: String): RsResultSet = {
    println(hashed_id)
    val userGroup = identity.group.getOrElse("")
    val f = Form.syntax("f")
    DB localTx { implicit s =>
      val formData =
        withSQL(
          select(f.id, f.hashed_id, f.form_data, f.user_group)
            .from(Form as f)
            .where
            .eq(f.hashed_id, hashed_id)
            .and
            .eq(f.user_group, userGroup)
        ).map(rs => Form(rs)).single.apply()

      val jsString = formData match {
        case Some(form) =>
          val formDefResult: JsResult[FormDef] = Json.parse(form.form_data).validate[FormDef]
          formDefResult match {
            case s: JsSuccess[FormDef] =>
              val fd: FormDef = s.get.replaceId(form.id, form.hashed_id)
              Json.toJson(fd)
            case _ =>
              // ToDo 2018/11/24 返り値をどうするか？
              Json.toJson("{}")
          }
        case _ =>
          // ToDo 2018/11/24 返り値をどうするか？
          Json.toJson("{}")
      }
      RsResultSet("OK", "OK", Json.toJson(jsString))
    }
  }

  /**
   * フォーム一覧
   * @return RsResultSet
   */
  def getList(identity: User): RsResultSet = {
    val userGroup = identity.group.getOrElse("")
    val f = Form.syntax("f")
    DB localTx { implicit s =>
      val formDataList =
        withSQL(
          select(f.id, f.hashed_id, f.form_data, f.user_group)
            .from(Form as f)
            .where
            .eq(f.user_group, userGroup)
        ).map(rs => Form(rs)).list.apply()

      val jsString = formDataList.zipWithIndex.map {
        case (a, b) =>
          val formDefResult: JsResult[FormDef] = Json.parse(a.form_data).validate[FormDef]
          formDefResult match {
            case s: JsSuccess[FormDef] =>
              val f: FormDef = s.get.replaceId(a.id, a.hashed_id)
              "\"" + b + "\":" + Json.toJson(f)
            case _ =>
              "\"" + b + "\":"
          }
      }.mkString("{", ",", "}")
      RsResultSet("OK", "OK", Json.toJson(jsString))
    }
  }

  /**
   * TransferJobManagerの処理対象フォームIDリスト取得
   * @return 処理対象のフォームIDリスト
   */
  def getListForTransferJobManager: List[String] = {
    val f = Form.syntax("f")
    val formlist = DB localTx { implicit s =>
      withSQL(
        select(f.id, f.hashed_id, f.form_data, f.user_group)
          .from(Form as f)
      ).map(rs => Form(rs)).list.apply()
    }
    formlist.map(form => {
      val formdef = Json.parse(form.form_data).validate[FormDef]
      formdef match {
        case s: JsSuccess[FormDef] =>
          s.get.status
        case _ => ""
      }
    }).filter(formid => formid.nonEmpty)
  }

  /**
   * フォームHTML取得
   * @param dt 入力データ
   * @return RsResultSet
   */
  def getHtml(dt: JsValue, host: String): RsResultSet = {
    println(dt.toString())
    val formId = (dt \ "formid").asOpt[String]
    val receiverPath = (dt \ "receiverPath").asOpt[String]
    formId match {
      case Some(id) =>
        val f = Form.syntax("f")
        DB localTx { implicit s =>
          val formData =
            withSQL {
              select(f.id, f.hashed_id, f.form_data, f.user_group)
                .from(Form as f)
                .where
                .eq(f.hashed_id, formId)
            }.map(rs => Form(rs)).single.apply()
          formData match {
            case Some(s: Form) =>
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(id, s, cFormMode.LOAD, None, host, receiverPath.getOrElse(""))))
            case _ =>
              RsResultSet("NG", "NG", Json.toJson("Could not get Formdata."))
          }
        }
      case None => RsResultSet("NG", "NG", Json.toJson("Invalid Parameter."))
    }
  }

  /**
   * フォーム定義データをHTMLに変換
   * @param fd フォーム定義データ
   * @return HTML文字列
   */
  def convertFormDefToHtml(hashed_id: String, fd: Form, mode: Int, postdata: Option[JsValue], host: String, receiverPath: String): String = {
    val formDefResult: JsResult[FormDef] = Json.parse(fd.form_data).validate[FormDef]
    formDefResult match {
      case s: JsSuccess[FormDef] =>
        val formDefColValue = s.get.formCols.value
        val validateResult: Map[String, String] = validateCols(formDefColValue, postdata).filter(p => p._2.length > 0)

        val htmlStr: Seq[String] =
          if (mode == cFormMode.REGIST) {
            savePostData(hashed_id, postdata.getOrElse(Json.toJson("")))
          } else {
            formDefColValue.map({
              case (k, v) =>
                val formDefColResult: JsResult[FormDefCol] = v.validate[FormDefCol]

                (k.toInt, formDefColResult match {
                  case f: JsSuccess[FormDefCol] =>
                    mode match {
                      case m: Int if m == cFormMode.LOAD => getColHtml(f.get, Map.empty[String, String])
                      case c: Int if c == cFormMode.CONFIRM =>
                        if (validateResult.nonEmpty) {
                          getColHtml(f.get, validateResult)
                        } else {
                          getConfirmColHtml(f.get, postdata.getOrElse(Json.toJson("")))
                        }
                    }
                  case e: JsError =>
                    println("Error1")
                    e.toString
                })
            }).toSeq.sortBy(_._1).map(_._2)
          }
        val resultStr = if (validateResult.nonEmpty) "NG" else "OK"
        header(mode, s.get) + htmlStr.mkString("", "", "") + hashed_id_hidden(hashed_id) + validate_result_hidden(resultStr) + buttons(mode, validateResult.nonEmpty) + script1(host, receiverPath)
      case e: JsError =>
        println("Error1")
        println(e.toString)
        ""
    }

  }

  /**
   * データ作成
   * @param dt 入力データ
   * @return RsResultSet
   */
  def insert(dt: JsValue, identity: User): RsResultSet = {
    println("insert")
    val formDefData = (dt \ "formDef").as[JsValue]
    formDefData.validate[FormDef] match {
      case s: JsSuccess[FormDef] =>
        s.get.id match {
          case i: Option[String] =>
            i match {
              case Some(j) if j.length > 0 => RsResultSet("OK", "OK", updateForm(formDefData, j, identity))
              case _ => RsResultSet("OK", "OK", insertForm(formDefData, identity))
            }
          case _ => RsResultSet("OK", "OK", insertForm(formDefData, identity))
        }
      case e: JsError =>
        RsResultSet("NG", "JSON Error.", Json.toJson(e.toString))
    }
  }

  /**
   * データ更新
   * @param dt 入力データ
   * @return RsResultSet
   */
  def update(dt: JsValue): RsResultSet = {
    RsResultSet("OK", "OK", Json.parse("""{}"""))
  }

  /**
   * データ削除
   * @param dt 入力データ
   * @return RsResultSet
   */
  def delete(dt: JsValue): RsResultSet = {
    val id = (dt \ "id").as[String]
    val f = Form.syntax("f")
    DB localTx { implicit s =>
      withSQL {
        deleteFrom(Form)
          .where
          .eq(Form.column.id, id)
      }.update.apply()
    }

    RsResultSet("NG", "NG", Json.parse("""{}"""))
  }
  def delete(hashed_form_id: String): RsResultSet = {
    val f = Form.syntax("f")
    DB localTx { implicit s =>
      withSQL {
        deleteFrom(Form)
          .where
          .eq(Form.column.hashed_id, hashed_form_id)
      }.update.apply()
    }
    RsResultSet("OK", "OK", Json.parse("""{}"""))
  }

  /**
   * フォームバリデーション
   * @param dt Ajaxからの送信データ
   * @return
   */
  def validate(dt: JsValue, host: String): RsResultSet = {
    val receiverPath = (dt \ "receiverPath").asOpt[String].getOrElse("")
    println("validate!")
    println(dt.toString())
    val f = Form.syntax("f")
    val formSaveRequest: JsResult[FormSaveRequest] = dt.validate[FormSaveRequest]
    formSaveRequest match {
      case s: JsSuccess[FormSaveRequest] =>
        DB localTx { implicit l =>
          val formData = withSQL {
            select(f.id, f.hashed_id, f.form_data, f.user_group)
              .from(Form as f)
              .where
              .eq(f.hashed_id, s.get.formid)
          }.map(rs => Form(rs)).single.apply()
          formData match {
            case Some(d) =>
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(s.get.formid, d, cFormMode.CONFIRM, Option(s.get.postdata), host, receiverPath)))
            case _ => RsResultSet("NG", "NG", Json.toJson("error"))
          }
        }
      case _ =>
        RsResultSet("NG", "NG", Json.toJson("error"))
    }
  }

  /**
   * フォーム送信データ保存
   * @param dt Ajaxからの送信データ
   * @return
   */
  def savePost(dt: JsValue, host: String): RsResultSet = {
    println("save!")
    println(dt.toString())
    val f = Form.syntax("f")
    val formSaveRequest: JsResult[FormSaveRequest] = dt.validate[FormSaveRequest]
    formSaveRequest match {
      case s: JsSuccess[FormSaveRequest] =>
        DB localTx { implicit l =>
          val formData = withSQL {
            select(f.id, f.hashed_id, f.form_data, f.user_group)
              .from(Form as f)
              .where
              .eq(f.hashed_id, s.get.formid)
          }.map(rs => Form(rs)).single.apply()
          formData match {
            case Some(d) =>
              println("save!!")
              RsResultSet("OK", "OK", Json.toJson(convertFormDefToHtml(s.get.formid, d, cFormMode.REGIST, Option(s.get.postdata), host, "")))
            case _ =>
              println("NG!!")
              RsResultSet("NG", "NG", Json.toJson("error"))
          }
        }
      case _ =>
        println("NG!!!!!")
        RsResultSet("NG", "NG", Json.toJson("error"))
    }
  }

  /**
   * フォーム項目情報取得
   * @param hashed_id フォームハッシュID
   * @return フォーム項目情報
   */
  def getFormCols(hashed_id: String): JsValue = {
    val f = Form.syntax("f")
    DB localTx { implicit s =>
      val formData =
        withSQL {
          select(f.id, f.hashed_id, f.form_data, f.user_group)
            .from(Form as f)
            .where
            .eq(f.hashed_id, hashed_id)
        }.map(rs => Form(rs)).single.apply()
      formData match {
        case Some(s: Form) =>
          val dt = Json.parse(s.form_data)
          (dt \ "formCols").asOpt[JsValue].getOrElse(Json.toJson(""))
        case _ => Json.toJson("")
      }
    }

  }

  /**
   * 各項目についてバリデーション実行
   * @param fd フォーム定義
   * @param pd 受信データ
   * @return バリデーション結果
   */
  private def validateCols(fd: Map[String, JsValue], pd: Option[JsValue]): Map[String, String] = {
    pd match {
      case Some(p: JsValue) =>
        fd map {
          case (k, v) =>
            v.validate[FormDefCol] match {
              case s: JsSuccess[FormDefCol] =>
                (s.get.colId, checkValidateRule(s.get, p))
              case e: JsError => ("", "")
            }
        }
      case _ =>
        Map.empty[String, String]
    }
  }

  /**
   * 各フォーム項目毎のバリデーション結果を返す
   * @param formDefCol フォーム項目定義
   * @param postdata 受信データ
   * @return バリデーション結果
   */
  private def checkValidateRule(formDefCol: FormDefCol, postdata: JsValue): String = {

    formDefCol.validations.validate[FormDefColValidation] match {
      case f: JsSuccess[FormDefColValidation] =>
        postdata \ formDefCol.colId match {
          case v: JsLookupResult =>

            f.get.inputType match {
              case cFormValidationType.NUMBER =>
                if (v.as[String].matches("""[0-9]*""")) "" else cValidationErrorMessage.NOT_NUMBER_ERROR
              case cFormValidationType.ALPHANUM =>
                if (v.as[String].matches("""[0-9a-zA-Z]*""")) "" else cValidationErrorMessage.NOT_ALPHA_NUMBER_ERROR
              case cFormValidationType.KATAKANA =>
                if (v.as[String].matches("[\\u30A1-\\u30FA]*")) "" else cValidationErrorMessage.NOT_KATAKANA_ERROR
              case cFormValidationType.HIRAGANA =>
                if (v.as[String].matches("[\\u3041-\\u3096]*")) "" else cValidationErrorMessage.NOT_HIRAGANA_ERROR
              case cFormValidationType.EMAIL =>
                if (v.as[String].matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) "" else cValidationErrorMessage.EMAIL_ADDRESS_FORMAT_ERROR
              case cFormValidationType.POSTCODE =>
                if (v.as[String].matches("[0-9]{3}-{0,1}[0-9]{4}")) "" else cValidationErrorMessage.NOT_POSTCODE_ERROR
              case _ => ""
            }
          case _ =>
            println("ppp")
            println(postdata \ formDefCol.name)
            ""
        }
      case e: JsError =>
        println("jserror")
        println(formDefCol.validations)
        println(e)
        ""
    }
  }

  /**
   * フォームデータ挿入
   * @param dt 入力データ
   * @return 処理結果のJsValue
   */
  private def insertForm(dt: JsValue, identity: User): JsValue = {
    println("insertForm")

    DB localTx { implicit s =>

      // 次のID取得
      val id: Option[Long] =
        sql"""SELECT auto_increment
             FROM information_schema.tables
             WHERE table_name = 'd_form'""".map(_.long(1)).single.apply()

      id match {
        case Some(l: Long) =>
          val rcdataResult: JsResult[FormDef] = dt.validate[FormDef]
          val hashed_id: String = UUID.randomUUID().toString
          val f: FormDef = rcdataResult.get.replaceId(l.intValue(), hashed_id)
          val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
          val newid: Long =
            sql"""INSERT INTO D_FORM(FORM_DATA,HASHED_ID,USER_GROUP,CREATED_USER,CREATED)
                 VALUES(${Json.toJson(f).toString},$hashed_id,${identity.group},${identity.userID.toString},$now)"""
              .updateAndReturnGeneratedKey.apply()
          Json.parse("""{"id": """" + newid.toString + """", "hashed_id":"""" + hashed_id + """"}""")
        case None =>
          Json.parse("""{"id": "failed"}""")
      }
    }
  }

  /**
   * フォームデータ更新
   * @param dt 入力データ
   * @param formId フォームID
   * @return 処理結果のJsValue
   */
  private def updateForm(dt: JsValue, formId: String, identity: User): JsValue = {
    DB localTx { implicit s =>
      sql"""UPDATE D_FORM
           SET FORM_DATA=${dt.toString},
           MODIFIED_USER=${identity.userID.toString},
           MODIFIED=now() WHERE ID=$formId""".update.apply
      Json.parse("""{"id": """" + formId.toString + """"}""")
    }
  }

  /**
   * フォーム項目定義からHTML生成
   * @param colDef 項目定義データ
   * @return
   */
  private def getColHtml(colDef: FormDefCol, validateResult: Map[String, String]): String = {
    val dbfactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val docbuilder: DocumentBuilder = dbfactory.newDocumentBuilder()
    val document: Document = docbuilder.newDocument()
    val coltype = colDef.coltype.getOrElse("")

    val root: Element = document.createElement("div")
    root.setAttribute("class", coltype match {
      case "6" => "sform-col-none"
      case "7" => "sform-col-disptext"
      case _ => "sform-col"
    })

    val colName: Element = document.createElement("div")
    colName.setAttribute("class", coltype match {
      case "6" | "7" => "sform-col-name-none"
      case _ => "sform-col-name"
    })
    val colForm: Element = document.createElement("div")
    colForm.setAttribute("class", "sform-col-form")

    colName.setTextContent(colDef.name)
    colForm.appendChild(getColFormHtml(colDef, document))

    val colError: Element = document.createElement("span")
    colError.setAttribute("class", "sform-col-error")
    colError.setTextContent(validateResult.getOrElse(colDef.colId, ""))
    colForm.appendChild(colError)

    root.appendChild(colName)
    root.appendChild(colForm)

    val sb: StringWriter = new StringWriter()
    val tfactory: TransformerFactory = TransformerFactory.newInstance()
    val transformer: Transformer = tfactory.newTransformer()

    // HTML形式で出力する（XMLのままだとXML宣言がつく）
    transformer.setOutputProperty(OutputKeys.METHOD, "html")
    // インデントオン、かつインデントのスペース数を決める
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2")
    transformer.transform(new DOMSource(root), new StreamResult(sb))
    sb.toString

  }

  /**
   * 項目種別からフォーム入力用タグを返す
   * @param colDef 項目定義データ
   * @param doc HTMLドキュメントオブジェクト
   * @return
   */
  private def getColFormHtml(colDef: FormDefCol, doc: Document): Element = {
    val typ: String = colDef.coltype match {
      case Some(s: String) => s
      case None => ""
    }

    val elem: Element = typ match {
      case "1" | "6" => doc.createElement("input")
      case "2" => doc.createElement("select")
      case "3" | "4" | "7" => doc.createElement("span")
      case "5" => doc.createElement("textarea")
      case _ => doc.createElement("span")
    }
    elem.setAttribute("class", typ match {
      case "1" | "2" | "5" | "6" => "sform-col-form-text"
      case "3" => "sform-col-form-checkbox"
      case "4" => "sform-col-form-radio"
      case "7" => "sform-col-form-span"
    })

    typ match {
      case "6" =>
        elem.setAttribute("type", "hidden")
      case "7" =>
        elem.setTextContent(colDef.default.getOrElse(""))
      case _ => None
    }

    elem.setAttribute("id", colDef.colId)

    colDef.selectList.value.map({
      case (k, v) =>
        v.validate[FormDefColSelectList] match {
          case s: JsSuccess[FormDefColSelectList] =>
            val element = typ match {
              case "2" => doc.createElement("option")
              case "3" | "4" => doc.createElement("input")
              case _ => doc.createElement("span")
            }
            element.setAttribute("type", typ match {
              case "3" => "checkbox"
              case "4" => "radio"
              case _ => ""
            })
            element.setAttribute("name", typ match {
              case "3" | "4" => "sel_" + colDef.colId
              case _ => ""
            })
            element.setTextContent(typ match {
              case "2" | "3" | "4" => s.get.displayText
              case _ => ""
            })
            element.setAttribute("value", s.get.value)
            element.setAttribute("id", colDef.colId + k)
            elem.appendChild(element)
          case e: JsError =>
            println(e.toString)
        }
    })
    elem
  }

  private def getConfirmColHtml(colDef: FormDefCol, postdata: JsValue): String = {
    val dbfactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val docbuilder: DocumentBuilder = dbfactory.newDocumentBuilder()
    val document: Document = docbuilder.newDocument()
    val colType = colDef.coltype.getOrElse("")
    val root: Element = document.createElement("div")

    colType match {
      case "6" | "7" =>
        root.setAttribute("class", "sform-col-none")
      case _ =>
        root.setAttribute("class", "sform-col")
        val colName: Element = document.createElement("div")
        colName.setAttribute("class", "sform-col-name")
        val colForm: Element = document.createElement("div")
        colForm.setAttribute("class", "sform-col-form")
        colName.setTextContent(colDef.name)

        val postdata_text = (postdata \ colDef.colId).getOrElse(Json.toJson("")).as[String]
        val displayText = colType match {
          case "2" | "3" | "4" =>
            // ラジオ・チェックボックス・コンボの場合はラベルを取ってくる
            val listDef = (colDef.selectList \ postdata_text).asOpt[JsValue]
            listDef match {
              case Some(s) => (s \ "displayText").asOpt[String].getOrElse("")
              case None => ""
            }
          case _ => postdata_text
        }
        colForm.setTextContent(displayText)

        root.appendChild(colName)
        root.appendChild(colForm)
    }

    val sb: StringWriter = new StringWriter()
    val tfactory: TransformerFactory = TransformerFactory.newInstance()
    val transformer: Transformer = tfactory.newTransformer()

    // HTML形式で出力する（XMLのままだとXML宣言がつく）
    transformer.setOutputProperty(OutputKeys.METHOD, "html")
    // インデントオン、かつインデントのスペース数を決める
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2")
    transformer.transform(new DOMSource(root), new StreamResult(sb))
    sb.toString
  }

  private def savePostData(hashed_id: String, dt: JsValue): Seq[String] = {
    val f = Form.syntax("f")
    DB localTx { implicit l =>
      val now: String = "%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date
      val newid: Long = sql"INSERT INTO D_POSTDATA(FORM_HASHED_ID,POSTDATA,CREATED,MODIFIED) VALUES($hashed_id,${Json.toJson(dt).toString},$now,$now)"
        .updateAndReturnGeneratedKey.apply()
      Seq("")
    }
  }

  def header(mode: Int, formDef: FormDef): String = {
    val text: String = mode match {
      case cFormMode.LOAD => formDef.inputHeader
      case cFormMode.CONFIRM => formDef.confirmHeader
      case cFormMode.REGIST => formDef.completeText
    }
    val subClass: String = mode match {
      case cFormMode.LOAD => "sform-header-input"
      case cFormMode.CONFIRM => "sform-header-confirm"
      case cFormMode.REGIST => "sform-header-complete"
    }
    s"""
       |<div class="sform-header-div $subClass">
       |$text
       |</div>
    """.stripMargin
  }

}
