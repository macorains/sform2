<template>
  <main>
    <HeaderMenu />
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
      />
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
      />
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
      />
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
      />
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
      />
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
      />
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
      />
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
      />
    </BFormGroup>
    <ColumnList ref="columnListRef" @update-column="updateColumn" v-if="form"/>
    <TransferTaskList ref="transferTaskListRef" />
    <BButton
        class="mt-3"
        block
        @click="saveForm"
    >
      保存
    </BButton>

  </main>
</template>

<script setup>
import {onMounted, onBeforeMount, getCurrentInstance, ref, provide} from "vue"
import {useRoute, useRouter} from "vue-router"
import { BButton, BFormGroup, BFormRadioGroup, BFormRadio, BFormInput, BFormTextarea } from 'bootstrap-vue-3'
import HeaderMenu from "@/components/HeaderMenu.vue"
import ColumnList from "@/components/form/ColumnList.vue"
import TransferTaskList from "@/components/form/TransferTaskList.vue"

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()
const route = useRoute()
const form = ref({})
const formStatusOptions = [
  { text: '無効', value:0 },
  { text: '有効', value:1 },
  { text: '休止', value:2 }
]
const transferTaskListRef = ref(null)

// ライフサイクルフック
onMounted(async () => {
  $http.get('/form/' + route.params.form_id)
      .then(response => {
        console.log(response)
        form.value = response.data
        columnListRef.value.load(response.data.form_cols)
        transferTaskListRef.value.load(response.data)
      })
})

const columnListRef = ref(null)

const saveForm = () =>  {
  console.log('*** saveForm ***')
  console.log(form)
}

const updateColumn = (columnList) => {
  if (columnList) {
    form.form_cols = columnList
  }
}
</script>

