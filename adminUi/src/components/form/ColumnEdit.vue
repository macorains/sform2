<template>
  <BContainer class="text-left form-col-edit">
    <BFormGroup
        id="formColNameGroup"
        label-for="formColName"
        label="カラム名"
        label-cols="4"
    >
      <BFormInput
          id="formColName"
          v-model="formCol.name"
          type="text"
          :state="errorState.name?.status"
          required
          @input="validate"
      />
      <BFormInvalidFeedback>{{ errorState.name?.message }}</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formColIdGroup"
        label-for="formColId"
        label="カラムID"
        label-cols="4"
    >
      <BFormInput
          id="formColId"
          v-model="formCol.col_id"
          type="text"
          :state="formColColIdState"
          required
          @input="validateFormColColIdState"
      />
      <BFormInvalidFeedback>{{ errorMessage.col_id }}</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formColColTypeGroup"
        label-for="formColColType"
        label="型"
        label-cols="4"
    >
      <BFormSelect
          id="formColColType"
          v-model="formCol.col_type"
          :options="optionFormColType"
          class="mb-3"
          :state="formColColTypeState"
          required
          @input="validateFormColColTypeState"
          @change="validateFormColColTypeState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>

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

    <BFormGroup
        id="formColDefaultGroup"
        label-for="formColDefault"
        :label="colDefaultLabel()"
        label-cols="4"
    >
      <BFormInput
          id="formColDefault"
          v-model="formCol.default_value"
          type="text"
          :state="formColDefaultValueState"
          @input="validateFormColDefaultValueState"
      />
      <BFormInvalidFeedback>{{ errorMessage.default_value }}</BFormInvalidFeedback>
    </BFormGroup>

    <BFormGroup
        v-if="[1,5].includes(formCol.col_type)"
        id="formColInputTypeGroup"
        label-for="formColInputType"
        label="入力形式"
        label-cols="4"
    >
      <BFormSelect
          id="formColInputType"
          v-model="formCol.validations.input_type"
          :options="optionFormColValidation"
          class="mb-3"
          @change="fuga()"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>

    <BRow class="mb-2" v-if="formCol.validations.input_type==1">
      <BCol cols="4">
        数値範囲
      </BCol>
      <BCol cols="3">
        <BFormInput
            id="formColValidationMinValue"
            v-model="formCol.validations.min_value"
            type="number"
            :state="formColValidateValueState"
            required
            @input="validateFormColValidateValueState"
        />
      </BCol>
      <BCol cols="1">
        ～
      </BCol>
      <BCol cols="3">
        <BFormInput
            id="formColValidationMaxValue"
            v-model="formCol.validations.max_value"
            type="number"
            :state="formColValidateValueState"
            required
            @input="validateFormColValidateValueState"
        />
      </BCol>
      <BCol cols="3" />
    </BRow>
    <BRow align-h="end" class="mt-0 mb-3">
      <BCol align-self="end" cols="8">
        <div v-if="errorMessage.validations.value" class="invalid-feedback d-block">{{ errorMessage.validations.value }}</div>
      </BCol>
    </BRow>
    <BRow class="mb-0" v-if="[1,5].includes(formCol.col_type) && ![1,6].includes(formCol.validations.input_type)">
      <BCol cols="4">
        文字列の長さ
      </BCol>
      <BCol cols="3">
        <BFormInput
            id="formColValidationMinLength"
            v-model="formCol.validations.min_length"
            type="number"
            :state="formColValidateLengthState"
            required
            @input="validateFormColValidateLengthState"
        />
      </BCol>
      <BCol cols="1">
        ～
      </BCol>
      <BCol cols="3">
        <BFormInput
            id="formColValidationMaxLength"
            v-model="formCol.validations.max_length"
            type="number"
            :state="formColValidateLengthState"
            required
            @input="validateFormColValidateLengthState"
        />
      </BCol>
      <BCol cols="3" />
    </BRow>
    <BRow v-if="[1,5].includes(formCol.col_type)" align-h="end" class="mt-0 mb-3">
      <BCol align-self="end" cols="8">
        <div v-if="errorMessage.validations.length" class="invalid-feedback d-block">{{ errorMessage.validations.length }}</div>
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="4">
        必須項目
      </BCol>
      <BCol>
        <BFormCheckbox
            id="formColRequired"
            v-model="formCol.validations.required"
            unchecked-value="false"
        />
      </BCol>
    </BRow>
  </BContainer>
</template>

<script setup>
import validator from 'validator'
import { isNil } from 'es-toolkit'
import ColumnSelectList from "@/components/form/ColumnSelectList.vue"
import {computed, defineEmits, inject, onMounted, provide, reactive, ref, triggerRef} from "vue"
import {BFormGroup} from "bootstrap-vue-3"
import isPostalCode from "validator/es/lib/isPostalCode.js";

const isSelectable = () => {
  return [2,3,4].includes(formCol.col_type) // TODO 正しい形に直す
}
const formCol = reactive({ validations: {}})
const errorMessage = reactive({validations: {}})
const errorState = reactive({})
const emit = defineEmits(['checkColumnIdExists', 'checkColumnNameExists']);
const props = defineProps({
  columnIdCheckResult: Boolean,
  columnNameCheckResult: Boolean
})

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

const formColNameState = computed(() => errorState.name?.status)
const formColColIdState = computed(() => checkFormColId(formCol))
const formColColTypeState = computed(() => checkFormColType(formCol))
const formColDefaultValueState = computed(() => checkFormColDefaultValue(formCol))
const formColInputTypeState = computed(() => formCol.validations.input_type?.trim() !== '')
const formColValidateValueState = computed(() => checkFormColValidateValue(formCol))
const formColValidateLengthState = computed(() => checkFormColValidateLength(formCol))
const formColValidateRequiredState = computed(() => checkFormColValidateRequired(formCol))

const okButtonDisabled = () => {
  return !(formColNameState.value
      && formColColIdState.value
      && formColColTypeState.value
      && formColDefaultValueState.value
      && formColInputTypeState.value
      && formColValidateValueState.value
      && formColValidateLengthState.value
      && formColValidateRequiredState.value
  )
}

const validateFormColNameState = () => {
  return formColNameState.value
}
const validateFormColColIdState = () => {
  return formColColIdState.value
}
const validateFormColColTypeState = () => {
  return formColColTypeState.value
}

const validateFormColDefaultValueState = () => {
  return formColDefaultValueState.value
}
const validateFormColInputTypeState = () => {
  return formColInputTypeState.value
}
const validateFormColValidateValueState = () => {
  return formColValidateValueState.value
}
const validateFormColValidateLengthState = () => {
  return formColValidateLengthState.value
}

const validateFormColValidateRequiredState = () => {
  return formColValidateRequiredState.value
}

const checkFormColId = (formCol) => {
  if(formCol.col_id === '') {
    errorMessage.col_id = 'カラムIDを入力してください'
    return false
  }
  emit('checkColumnIdExists', formCol)
  if(props.columnIdCheckResult) {
    errorMessage.col_id = '他の項目で使用されています'
    return false
  } else {
    errorMessage.col_id = ''
    return true
  }
}

const colDefaultLabel = () => {
  if(formCol.col_type === 6) {
    return '送信文字列'
  }
  if(formCol.col_type === 7) {
    return '表示文字列'
  }
  return '初期値'
}

const validate = () => {
  // カラム名
  if(isEmpty(formCol.name)) {
    errorState.name = { status: false, message: 'カラム名を入力してください'}
  } else {
    emit('checkColumnNameExists', formCol)
    if (props.columnNameCheckResult) {
      errorState.name = {status: false, message: '他の項目で使用されています'}
    } else {
      errorState.name = {status: true, message: ''}
    }
  }
}

const checkFormColName = () => {
  if(formCol.name === '') {
    errorMessage.name = 'カラム名を入力してください'
    return false
  }
  emit('checkColumnNameExists', formCol)
  if(props.columnNameCheckResult) {
    errorMessage.name = '他の項目で使用されています'
    return false
  } else {
    errorMessage.name = ''
    return true
  }
}

const checkFormColType = (formCol) => {
  if(![1,5].includes(formCol.col_type)){
    formCol.validations.min_value = ''
    formCol.validations.max_value = ''
    formCol.validations.min_length = ''
    formCol.validations.max_length = ''
    formCol.validations.input_type = ''
  }
  if([1,5].includes(formCol.col_type) && formCol.validations.input_type === ''){
    formCol.validations.input_type = '0'
  }
  return typeof formCol.col_type === 'number' && Number.isFinite(formCol.col_type)
}

const checkFormColDefaultValue = (formCol)=> {
  if(formCol.validations.input_type === 1) {
    if(/^[0-9]+$/.test(formCol.default_value)) {
      return true
    }
    errorMessage.default_value = '数値を入力してください'
    return false
  }
  if(formCol.validations.input_type === 2) {
    if(/^[A-Za-z0-9]+$/.test(formCol.default_value)) {
      return true
    }
    errorMessage.default_value = '英数字を入力してください'
    return false
  }
  if(formCol.validations.input_type === 3) {
    if(/^[\u3040-\u309F]+$/.test(formCol.default_value)) {
      return true
    }
    errorMessage.default_value = 'ひらがなを入力してください'
    return false
  }
  if(formCol.validations.input_type === 4) {
    if (isEmpty(formCol.default_value) || validator.isEmail(formCol.default_value)) {
      if (/^[\u30A0-\u30FF]+$/.test(formCol.default_value)) {
        return true
      }
      errorMessage.default_value = 'カタカナを入力してください'
      return false
    }
  }
  if(formCol.validations.input_type === 5) {
    if(isEmpty(formCol.default_value) || validator.isEmail(formCol.default_value)) {
      return true
    }
    errorMessage.default_value = 'メールアドレスを入力してください'
    return false
  }
  if(formCol.validations.input_type === 6) {
    if(isEmpty(formCol.default_value) || validator.isPostalCode(formCol.default_value, 'JP')){
      return true
    }
    errorMessage.default_value = '郵便番号を入力してください'
    return false
  }
  return true
}

const checkFormColValidateValue = (formCol) => {
  if(!formCol.validations.min_value || !formCol.validations.max_value){
    errorMessage.validations.value = ''
    return true
  }
  if(Number(formCol.validations.min_value) >= Number(formCol.validations.max_value)) {
    errorMessage.validations.value = '最大値は最小値より大きくしてください'
    return false
  }
  errorMessage.validations.value = ''
  return true
}
const checkFormColValidateLength = (formCol) => {
  if(formCol.validations.required === true && (!formCol.validations.min_length || formCol.validations.min_length <= 0)) {
    errorMessage.validations.length = '最小値は1以上にしてください'
    return false
  }
  if(formCol.validations.required === "false" && Number(formCol.validations.min_length) < 0) {
    errorMessage.validations.length = '最小値は0以上にしてください'
    return false
  }
  if(formCol.validations.required === "false" && !isEmpty(formCol.validations.max_length) && Number(formCol.validations.max_length) < 1) {
    errorMessage.validations.length = '最大値は1以上にしてください'
    return false
  }
  if(!formCol.validations.min_length || !formCol.validations.max_length){
    errorMessage.validations.length = ''
    return true
  }
  if(Number(formCol.validations.min_length) >= Number(formCol.validations.max_length)) {
    errorMessage.validations.length = '最大値は最小値より大きくしてください'
    return false
  }
  errorMessage.validations.length = ''
  return true
}

const checkFormColValidateRequired = (formCol) => {
  if(formCol.validations.required && (!formCol.validations.min_length || formCol.validations.min_length <= 0)) {
    errorMessage.validations.length = '最小値は1以上にしてください'
    return true
  }
  errorMessage.validations.length = ''
  return true
}

const setFormCol = (item) => {
  formCol.id = item.id
  formCol.name = item.name
  formCol.col_id = item.col_id
  formCol.col_type = item.col_type
  formCol.col_index = item.col_index
  formCol.col_type = item.col_type
  formCol.default_value = item.default_value
  formCol.form_id = item.form_id
  formCol.select_list = item.select_list
  formCol.validations = item.validations
}


const getFormCol = () => {
  return formCol
}

const isEmpty = (str) => {
  return isNil(str) || str === ''
}

const fuga = () => {
  console.log('***** fuga *****')
}

provide('formCol', formCol)

onMounted(() => {
  // data.value = props.form.form_cols
})

defineExpose({
  setFormCol,
  getFormCol,
  okButtonDisabled
})
</script>
