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
          :state="errorState.col_id?.status"
          required
          @input="validate"
      />
      <BFormInvalidFeedback>{{ errorState.col_id?.message }}</BFormInvalidFeedback>
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
          required
          @input="validate"
          @change="validate"
      />
    </BFormGroup>

    <BRow
        v-show="isSelectable()"
        class="mb-3"
    >
      <BCol cols="4">
        選択肢
      </BCol>
      <BCol>
        <ColumnSelectList v-model:selectList="formCol.select_list" @validate="validate" ref="columnSelectListRef" />
        <div v-if="!errorState.is_default?.status" class="invalid-feedback d-block">{{ errorState.is_default?.message }}</div>
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
          :state="errorState.default_value?.status"
          @input="validate"
      />
      <BFormInvalidFeedback>{{ errorState.default_value?.message }}</BFormInvalidFeedback>
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
          @change="validate"
      />
      <BFormInvalidFeedback>{{ errorState.input_type?.message }}</BFormInvalidFeedback>
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
            :state="errorState.validations.value?.status"
            required
            @input="validate"
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
            :state="errorState.validations?.value?.status"
            required
            @input="validate"
        />
      </BCol>
      <BCol cols="3" />
    </BRow>
    <BRow align-h="end" class="mt-0 mb-3">
      <BCol align-self="end" cols="8">
        <div v-if="!errorState.validations?.value?.status" class="invalid-feedback d-block">{{ errorState.validations?.value?.message }}</div>
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
            :state="errorState.validations?.length?.status"
            required
            @input="validate"
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
            :state="errorState.validations?.length?.status"
            required
            @input="validate"
        />
      </BCol>
      <BCol cols="3" />
    </BRow>
    <BRow v-if="[1,5].includes(formCol.col_type)" align-h="end" class="mt-0 mb-3">
      <BCol align-self="end" cols="8">
        <div v-if="!errorState.validations?.length?.status" class="invalid-feedback d-block">{{ errorState.validations?.length?.message }}</div>
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
            @change="validate"
        />
      </BCol>
    </BRow>
  </BContainer>
</template>

<script setup>
import validator from 'validator'
import { isNil } from 'es-toolkit'
import ColumnSelectList from "@/components/form/ColumnSelectList.vue"
import {defineEmits, inject, onMounted, provide, reactive, ref, toRef} from "vue"
import {BFormGroup} from "bootstrap-vue-3"

const isSelectable = () => {
  return [2,3,4].includes(formCol.col_type) // TODO 正しい形に直す
}
const formCol = reactive({ select_list:[], validations: {}})
const errorState = reactive({ validations: {}})
const emit = defineEmits(['checkColumnIdExists', 'checkColumnNameExists', 'updateButtonState']);
const columnSelectListRef = ref(null)
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
    errorState.name = { status: false, message: 'カラム名を入力してください' }
  } else {
    emit('checkColumnNameExists', formCol)
    if (props.columnNameCheckResult) {
      errorState.name = {status: false, message: '他の項目で使用されています' }
    } else {
      errorState.name = {status: true, message: ''}
    }
  }
  // カラムID
  if(formCol.col_id === '') {
    errorState.col_id = { status: false, message: 'カラムIDを入力してください' }
  } else {
    emit('checkColumnIdExists', formCol)
    if (props.columnIdCheckResult) {
      errorState.col_id = {status: false, message: '他の項目で使用されています'}
    } else {
      errorState.col_id = {status: true, message: ''}
    }
  }
  // 初期値
  if(formCol.validations.input_type === 1) {
    if(isEmpty(formCol.default_value) || /^[0-9]+$/.test(formCol.default_value)) {
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: '数値を入力してください'}
    }
  }
  if(formCol.validations.input_type === 2) {
    if(isEmpty(formCol.default_value) || /^[A-Za-z0-9]+$/.test(formCol.default_value)) {
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: '英数字を入力してください'}
    }
  }
  if(formCol.validations.input_type === 3) {
    if(isEmpty(formCol.default_value) || /^[\u3040-\u309F]+$/.test(formCol.default_value)) {
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: 'ひらがなを入力してください'}
    }
  }
  if(formCol.validations.input_type === 4) {
    if (isEmpty(formCol.default_value) || /^[\u30A0-\u30FF]+$/.test(formCol.default_value)) {
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: 'カタカナを入力してください'}
    }
  }
  if(formCol.validations.input_type === 5) {
    if(isEmpty(formCol.default_value) || validator.isEmail(formCol.default_value)) {
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: 'メールアドレスを入力してください'}
    }
  }
  if(formCol.validations.input_type === 6) {
    if(isEmpty(formCol.default_value) || validator.isPostalCode(formCol.default_value, 'JP')){
      errorState.default_value = {status: true, message: ''}
    } else {
      errorState.default_value = {status: false, message: '郵便番号を入力してください'}
    }
  }
  // col_type
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
  const col_type_status = typeof formCol.col_type === 'number' && Number.isFinite(formCol.col_type)
  if(!col_type_status) {
    errorState.col_type = {status: false, message: 'カラムタイプを選択してください'}
  } else {
    errorState.col_type = {status: true, message: ''}
  }

  if([2,3,4].includes(formCol.col_type)) {
    if(formCol.select_list.length === 0) {
      columnSelectListRef.value.addColSelectList()
    }
    if([2,4].includes(formCol.col_type)) {
      const isDefaultCount = formCol.select_list.filter(item => item.is_default === true).length
      if(isDefaultCount > 1) {
        errorState.is_default = {status: false, message: 'デフォルト値に設定できるのは1つだけです'}
      } else {
        errorState.is_default = {status: true, message:''}
      }
    } else {
      errorState.is_default = {status: true, message:''}
    }
  } else {
    formCol.select_list = []
  }

  // 数値範囲
  if(formCol.col_type === 1) {
    if (!formCol.validations.min_value || !formCol.validations.max_value) {
      errorState.validations.value = {status: true, message: ''}
    } else if (Number(formCol.validations.min_value) >= Number(formCol.validations.max_value)) {
      errorState.validations.value = {status: false, message: '最大値は最小値より大きくしてください'}
    } else {
      errorState.validations.value = {status: true, message: ''}
    }
  } else {
    errorState.validations.value = {status: true, message: ''}
  }

  // 文字列長
  if([1,5].includes(formCol.col_type)) {
    if (formCol.validations.required === true && (!formCol.validations.min_length || formCol.validations.min_length <= 0)) {
      errorState.validations.length = {status: false, message: '最小値は1以上にしてください'}
    } else if (formCol.validations.required === "false" && Number(formCol.validations.min_length) < 0) {
      errorState.validations.length = {status: false, message: '最小値は0以上にしてください'}
    } else if (formCol.validations.required === "false" && !isEmpty(formCol.validations.max_length) && Number(formCol.validations.max_length) < 1) {
      errorState.validations.length = {status: false, message: '最大値は1以上にしてください'}
    } else if (!formCol.validations.min_length || !formCol.validations.max_length) {
      errorState.validations.length = {status: true, message: ''}
    } else if (Number(formCol.validations.min_length) >= Number(formCol.validations.max_length)) {
      errorState.validations.length = {status: false, message: '最大値は最小値より大きくしてください'}
    } else {
      errorState.validations.length = {status: true, message: ''}
    }
  } else {
    errorState.validations.length = {status: true, message: ''}
  }
  emit('updateButtonState', okButtonDisabled())
}

const okButtonDisabled = () => {
  const errorCount = Object.values(errorState).filter(item => item.status === false).length + Object.values(errorState.validations).filter(item => item.status === false).length
  return errorCount > 0
}

const setFormCol = (item) => {
  console.log(item)
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
  validate()
}


const getFormCol = () => {
  return formCol
}

const isEmpty = (str) => {
  return isNil(str) || str === ''
}


defineExpose({
  setFormCol,
  getFormCol,
})
</script>
