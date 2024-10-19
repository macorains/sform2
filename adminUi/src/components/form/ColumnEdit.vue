<script setup>
import ColumnSelectList from "@/components/form/ColumnSelectList.vue";
import {inject, onMounted, provide, ref} from "vue";

const props = defineProps({ formColId: { type: Number } })

const isSelectable = () => {
  return true // TODO 正しい形に直す
}
const form = inject('form')
const formCol = ref({ validations: {}})
const optionFormColType = ref( [
  { value: 1, text: 'テキスト', select_list: false },
  { value: 2, text: 'コンボボックス（単一選択）', select_list: true },
  { value: 3, text: 'チェックボックス（複数選択）', select_list: true },
  { value: 4, text: 'ラジオボタン（単一選択）', select_list: true },
  { value: 5, text: 'テキストエリア', select_list: false },
  { value: 6, text: '隠しテキスト', select_list: false },
  { value: 7, text: '表示テキスト（非入力項目）', select_list: false }
])
const optionFormColValidation = ref([
      { value: 0, text: '無制限' },
      { value: 1, text: '数値のみ' },
      { value: 2, text: '英数字のみ' },
      { value: 3, text: 'ひらがなのみ' },
      { value: 4, text: 'カタカナのみ' },
      { value: 5, text: 'メールアドレス' },
      { value: 6, text: '郵便番号' }
])

const setFormCol = (item) => {
  formCol.value = item
}

provide('formCol', formCol)

onMounted(() => {
  // data.value = props.form.form_cols
})

defineExpose({
  setFormCol
})

</script>
<template>
  <BContainer class="text-left form-col-edit">
    <BRow class="mb-3">
      <BCol cols="4">
        カラム名
      </BCol>
      <BCol>
        <BFormInput
            id="formColName"
            v-model="formCol.name"
            type="text"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        カラムID
      </BCol>
      <BCol>
        <BFormInput
            id="formColId"
            v-model="formCol.col_id"
            type="text"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        型
      </BCol>
      <BCol>
        <BFormSelect
            v-model="formCol.col_type"
            :options="optionFormColType"
            class="mb-3"
        />
      </BCol>
    </BRow>
    <BRow
        v-if="isSelectable()"
        class="mb-3"
    >
      <BCol cols="4">
        選択肢
      </BCol>
      <BCol>
        <ColumnSelectList />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        初期値
      </BCol>
      <BCol>
        <BFormInput
            id="formColDefault"
            v-model="formCol.default_value"
            type="text"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        バリデーション種別
      </BCol>
      <BCol>
        <BFormSelect
            v-model="formCol.validations.input_type"
            :options="optionFormColValidation"
            class="mb-3"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        数値範囲
      </BCol>
      <BCol cols="2">
        <BFormInput
            id="formColValidationMinValue"
            v-model="formCol.validations.min_value"
            type="text"
        />
      </BCol>
      <BCol cols="1">
        ～
      </BCol>
      <BCol cols="2">
        <BFormInput
            id="formColValidationMaxValue"
            v-model="formCol.validations.max_value"
            type="text"
        />
      </BCol>
      <BCol cols="5" />
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        文字列の長さ
      </BCol>
      <BCol cols="2">
        <BFormInput
            id="formColValidationMinLength"
            v-model="formCol.validations.min_length"
            type="text"
        />
      </BCol>
      <BCol cols="1">
        ～
      </BCol>
      <BCol cols="2">
        <BFormInput
            id="formColValidationMaxLength"
            v-model="formCol.validations.max_length"
            type="text"
        />
      </BCol>
      <BCol cols="5" />
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        必須項目
      </BCol>
      <BCol>
        <BFormCheckbox
            id="formColRequired"
            v-model="formCol.validations.required"
            value="true"
            unchecked-value="false"
        />
      </BCol>
    </BRow>
  </BContainer>
</template>
