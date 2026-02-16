<template>
  <main>
    <HeaderMenu />
    <BBreadcrumb>
      <BBreadcrumbItem href="/form">フォーム</BBreadcrumbItem>
      <BBreadcrumbItem active>{{ form.name }}</BBreadcrumbItem>
    </BBreadcrumb>
    <BFormGroup
        id="formStatusGroup"
        label-for="formStatus"
        label="ステータス"
        label-cols="3"
    >
      <BFormRadioGroup
          id="formStatus"
          v-model.number="form.status"
          type="number"
          name="status"
          :options="formStatusOptions"
      />
    </BFormGroup>
    <BFormGroup
        id="formNameGroup"
        label-for="formName"
        label="フォーム名"
        label-cols="3"
    >
      <BFormInput
          id="formName"
          v-model="form.name"
          type="text"
          :state="formDataNameState"
          required
          @input="validateFormDataNameState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formTitleGroup"
        label-for="formTitle"
        label="画面に表示するフォームのタイトル"
        label-cols="3"
    >
      <BFormInput
          id="formTitle"
          v-model="form.title"
          type="text"
          :state="formDataTitleState"
          required
          @input="validateFormDataTitleState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="urlAfterCancelGroup"
        label-for="urlAfterCancel"
        label="フォーム入力キャンセル時に開くページのURL"
        label-cols="3"
    >
      <BFormInput
          id="urlAfterCancel"
          v-model="form.cancel_url"
          type="text"
          :state="formDataCancelUrlState"
          required
          @input="validateFormDataCancelUrlState"
      />
      <BFormInvalidFeedback>{{ errorMessage.cancel_url }}</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="urlAfterCompleteGroup"
        label-for="urlAfterComplete"
        label="フォーム入力完了後に開くページのURL"
        label-cols="3"
    >
      <BFormInput
          id="urlAfterComplete"
          v-model="form.complete_url"
          type="text"
          :state="formDataCompleteUrlState"
          required
          @input="validateFormDataCompleteUrlState"
      />
      <BFormInvalidFeedback>{{ errorMessage.complete_url }}</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formInputHeaderGroup"
        label-for="formInputHeader"
        label="フォーム入力画面上部の文言"
        label-cols="3"
    >
      <BFormTextarea
          id="formInputHeader"
          v-model="form.input_header"
          :rows="3"
          :max-rows="10"
          :state="formDataInputHeaderState"
          required
          @input="validateFormDataInputHeaderState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formConfirmHeaderGroup"
        label-for="formConfirmHeader"
        label="フォーム入力後確認画面上部の文言"
        label-cols="3"
    >
      <BFormTextarea
          id="formConfirmHeader"
          v-model="form.confirm_header"
          :rows="3"
          :max-rows="10"
          :state="formDataConfirmHeaderState"
          required
          @input="validateFormDataConfirmHeaderState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formCompleteTextGroup"
        label-for="formCompleteText"
        label="フォーム確認後に表示する文言"
        label-cols="3"
    >
      <BFormTextarea
          id="formCompleteText"
          v-model="form.complete_text"
          :rows="3"
          :max-rows="10"
          :state="formDataCompleteTextState"
          required
          @input="validateFormDataCompleteTextState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <BFormGroup
        id="formStopTextGroup"
        label-for="formStopText"
        label="フォーム休止時に表示する文言"
        label-cols="3"
    >
      <BFormTextarea
          id="formStopText"
          v-model="form.close_text"
          :rows="3"
          :max-rows="10"
          :state="formDataCloseTextState"
          required
          @input="validateFormDataCloseTextState"
      />
      <BFormInvalidFeedback>入力してください。</BFormInvalidFeedback>
    </BFormGroup>
    <ColumnList ref="columnListRef" @update-column="updateColumn" v-if="form"/>
    <TransferTaskList ref="transferTaskListRef" @update-transfer-tasks="updateTransferTasks" />
    <BButton
        class="mt-3"
        block
        @click="cancel"
    >
      <i class="bi bi-ban me-1"></i>キャンセル
    </BButton>
    <BButton
        class="mt-3 ms-3"
        block
        @click="saveForm"
        :disabled="okButtonDisabled()"
    >
      <i class="bi bi-cloud-arrow-down me-1"></i>保存
    </BButton>

  </main>
</template>

<script setup>
import {onMounted, onBeforeMount, getCurrentInstance, ref, provide, computed, reactive} from "vue"
import {useRoute, useRouter} from "vue-router"
import { BButton, BFormGroup, BFormRadioGroup, BFormRadio, BFormInput, BFormTextarea } from 'bootstrap-vue-3'
import { useHttpRequest } from "@/composables/useHttpRequest.js"
import HeaderMenu from "@/components/HeaderMenu.vue"
import ColumnList from "@/components/form/ColumnList.vue"
import TransferTaskList from "@/components/form/TransferTaskList.vue"

const { requestGet, requestPost, loading } = useHttpRequest()
const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()
const route = useRoute()
const form = reactive({})
const formStatusOptions = [
  { text: '無効', value:0 },
  { text: '有効', value:1 },
  { text: '休止', value:2 }
]
const transferTaskListRef = ref(null)
const errorMessage = reactive({
})

// ライフサイクルフック
onBeforeMount(async () => {
  requestGet(
      '/form/' + route.params.form_id,
      response => {
        const data = response.data
        Object.assign(form, {
          id: data.id,
          hashed_id: data.hashed_id,
          form_index: data.form_index,
          status: data.status,
          name: data.name,
          title: data.title,
          cancel_url: data.cancel_url,
          complete_url: data.complete_url,
          input_header: data.input_header,
          confirm_header: data.confirm_header,
          complete_text: data.complete_text,
          close_text: data.close_text,
          form_transfer_tasks: data.form_transfer_tasks ?? [],
          form_cols: data.form_cols ?? []
        })

        columnListRef.value.load(response.data.form_cols)
        transferTaskListRef.value.load(response.data)
      }
  )
})

const columnListRef = ref(null)

const formDataNameState = computed(() => form.name?.trim() !== '')
const formDataTitleState = computed(() => form.title?.trim() !== '')
const formDataCancelUrlState = computed(() => checkCancelUrl(form))
const formDataCompleteUrlState = computed(() => checkCompleteUrl(form))
const formDataInputHeaderState = computed(() => form.input_header?.trim() !== '')
const formDataConfirmHeaderState = computed(() => form.confirm_header?.trim() !== '')
const formDataCompleteTextState = computed(() => form.complete_text?.trim() !== '')
const formDataCloseTextState = computed(() => form.close_text?.trim() !== '')
const validateFormDataNameState = () => {
  return formDataNameState.value
}
const validateFormDataTitleState = () => {
  return formDataTitleState.value
}

const validateFormDataCancelUrlState = () => {
  return formDataCancelUrlState.value
}

const validateFormDataCompleteUrlState = () => {
  return formDataCompleteUrlState.value
}

const validateFormDataInputHeaderState = () => {
  return formDataInputHeaderState.value
}

const validateFormDataConfirmHeaderState = () => {
  return formDataConfirmHeaderState.value
}

const validateFormDataCompleteTextState = () => {
  return formDataCompleteTextState.value
}

const validateFormDataCloseTextState = () => {
  return formDataCloseTextState.value
}

const okButtonDisabled = () => {
  return !(formDataNameState.value
      && formDataTitleState.value
      && formDataCancelUrlState.value
      && formDataCompleteUrlState.value
      && formDataInputHeaderState.value
      && formDataConfirmHeaderState.value
      && formDataCompleteTextState.value
      && formDataCloseTextState.value)
}

const checkCancelUrl = (data) => {
  const cancelUrl = data.cancel_url
  if(cancelUrl === '') {
    errorMessage.cancel_url = '入力してください。'
    return false
  } else if(!URL.canParse(cancelUrl)) {
    errorMessage.cancel_url = 'URL形式で入力してください。'
    return false
  } else {
    errorMessage.cancel_url = ''
    return true
  }
}

const checkCompleteUrl = (data) => {
  const completeUrl = data.complete_url
  if(completeUrl === '') {
    errorMessage.complete_url = '入力してください。'
    return false
  } else if(!URL.canParse(completeUrl)) {
    errorMessage.complete_url = 'URL形式で入力してください。'
    return false
  } else {
    errorMessage.complete_url = ''
    return true
  }
}

const saveForm = () =>  {
  requestPost('/form', form, response => {
    router.push('/form')
  })
}

const cancel = () => {
  router.push('/form')
}

const updateColumn = (columnList) => {
  if (columnList) {
    form.form_cols = columnList
  }
}

const updateTransferTasks = (transferTasks) => {
  if (transferTasks) {
    form.form_transfer_tasks = transferTasks
  }
}
</script>

