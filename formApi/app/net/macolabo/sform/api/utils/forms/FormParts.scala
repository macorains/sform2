package net.macolabo.sform.api.utils.forms


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
    val REQUIRED = "入力してください"
    val NUMBER_TOO_SMALL = "入力値が小さすぎます"
    val NUMBER_TOO_BIG = "入力値が大きすぎます"
    val STRING_TOO_SHORT = "文字数が不足しています"
    val STRING_TOO_LONG = "文字数が多すぎます"
  }

  def script1(host: String, receiverPath: String): String = {
    //val source = Source.fromPath("forminput.js")
    //val lines = source.getLines
    //lines.mkString("")
    //val resource: Option[URL] = app.resource("forminput.js")
    //FileUtils.readFileToString(new File("forminput.js"), "UTF-8")
    if(receiverPath.isEmpty) {
      ""
    } else {
      s"""
         |<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
         |<script src="http://$host/forminputjs/$receiverPath"></script>
    """.stripMargin
    }
  }

  def buttons(mode: Int, validateResult: Boolean): String = {
    mode match {
      case cFormMode.LOAD =>
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_cancel">キャンセル</button>
          |<button type="button" id="sform_button_confirm">次へ</button>
          |</div>
        """.stripMargin
      case cFormMode.CONFIRM if !validateResult =>
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_cancel">キャンセル</button>
          |<button type="button" id="sform_button_confirm">次へ</button>
          |</div>
        """.stripMargin
      case cFormMode.CONFIRM if validateResult =>
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_back">戻る</button>
          |<button type="button" id="sform_button_submit">送信</button>
          |<input type="hidden" id="sform_tmp">
          |</div>
        """.stripMargin
      case cFormMode.REGIST =>
        """
          |<div class="sform_button_div">
          |<button type="button" id="sform_button_finish">完了</button>
          |</div>
       """.stripMargin
    }
  }

  def hidden_tag(id: String, value:String): String = {
    s"""<input type="hidden" id="$id" name="$id" value="$value">"""
  }

  def validate_result_hidden(result: String): String = {
    s"""
       |<input type="hidden" id="validate_result" name="validate_result" value="$result">
    """.stripMargin
  }

}
