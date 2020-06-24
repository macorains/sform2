package net.macolabo.sform2.utils.forms

import java.net.InetAddress

import controllers.routes

//import scala.io.Source

trait FormParts {

  object cFormMode {
    // フォーム実行処理モード
    val LOAD: Int = 1
    val CONFIRM: Int = 2
    val REGIST: Int = 3
  }

  /**
   * フォームバリデーション種別
   */
  object cFormValidationType {
    val NONE = "0"
    val NUMBER = "1"
    val ALPHANUM = "2"
    val HIRAGANA = "3"
    val KATAKANA = "4"
    val EMAIL = "5"
    val POSTCODE = "6"
  }

  object cValidationErrorMessage {
    val NOT_NUMBER_ERROR = "半角数字で入力してください"
    val NOT_ALPHA_NUMBER_ERROR = "半角英数字で入力してください"
    val EMAIL_ADDRESS_FORMAT_ERROR = "メールアドレス形式が不正です"
    val NOT_HIRAGANA_ERROR = "全角ひらがなで入力してください"
    val NOT_KATAKANA_ERROR = "全角カタカナで入力してください"
    val NOT_POSTCODE_ERROR = "半角数字およびハイフン以外は入力できません"
  }

  def script1(host: String, receiverPath: String): String = {
    //val source = Source.fromPath("forminput.js")
    //val lines = source.getLines
    //lines.mkString("")
    //val resource: Option[URL] = app.resource("forminput.js")
    //FileUtils.readFileToString(new File("forminput.js"), "UTF-8")

    s"""
      |<!-- {$host} -->
      |<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
      |<!-- <script src="http://localhost:9001/assets/javascripts/forminput.js"></script> -->
      |<script src="http://$host/assets/javascripts/forminput.js$receiverPath"></script>
    """.stripMargin
  }

  def buttons(mode: Int, validateResult: Boolean): String = {
    mode match {
      case cFormMode.LOAD => {
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_cancel">キャンセル</button>
          |<button type="button" id="sform_button_confirm">次へ</button>
          |</div>
        """.stripMargin
      }
      case cFormMode.CONFIRM if validateResult => {
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_cancel">キャンセル</button>
          |<button type="button" id="sform_button_confirm">次へ</button>
          |</div>
        """.stripMargin
      }
      case cFormMode.CONFIRM if !validateResult => {
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_back">戻る</button>
          |<button type="button" id="sform_button_submit">送信</button>
          |<input type="hidden" id="sform_tmp">
          |</div>
        """.stripMargin
      }
      case cFormMode.REGIST => {
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_finish">完了</button>
          |</div>
       """.stripMargin
      }
    }
  }

  def hashed_id_hidden(hashed_id: String): String = {
    s"""
      |<input type="hidden" id="hashed_id" name="hashed_id" value="${hashed_id}">
    """.stripMargin
  }

  def validate_result_hidden(result: String): String = {
    s"""
       |<input type="hidden" id="validate_result" name="validate_result" value="${result}">
    """.stripMargin
  }

}
