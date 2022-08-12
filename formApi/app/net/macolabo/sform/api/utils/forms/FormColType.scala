package net.macolabo.sform.api.utils.forms

sealed abstract class FormColType(val col_type: Int)

case object FormColType_Text extends FormColType(1)
case object FormColType_Combo extends FormColType(2)
case object FormColType_Checkbox extends FormColType(3)
case object FormColType_Radio extends FormColType(4)
case object FormColType_TextArea extends FormColType(5)
case object FormColType_Hidden extends FormColType(6)
case object FormColType_DisplayText extends FormColType(7)