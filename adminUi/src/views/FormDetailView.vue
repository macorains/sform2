<script setup>
import {onMounted, getCurrentInstance, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import HeaderMenu from "@/components/HeaderMenu.vue";
import ColumnList from "@/components/form/ColumnList.vue";
import TransferTaskList from "@/components/form/TransferTaskList.vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()
const route = useRoute()

const form = ref({
  status: '',
  name: '',
  title: '',
  cancel_url: '',
  complete_url: '',
  input_header: '',
  confirm_header: '',
  complete_text: '',
  close_text: ''
})

// ライフサイクルフック
onMounted(() => {
  // TODO 詳細用APIを準備して叩くようにする
  $http.get('/form/' + route.params.form_id)
      .then(response => {
        console.log(response)
        form.value = response.data
      })
  console.log(route.params.form_id)
})
</script>

<template>
  <main>
    <HeaderMenu />
    <b-form-group
        id="formNameStatus"
        label-for="status"
        label="ステータス"
        label-cols="3"
    >
      <b-form-radio-group
          id="status"
          v-model.number="form.status"
          type="number"
          name="status"
      >
        <b-form-radio value="0">
          無効
        </b-form-radio>
        <b-form-radio value="1">
          有効
        </b-form-radio>
        <b-form-radio value="2">
          休止
        </b-form-radio>
      </b-form-radio-group>
    </b-form-group>
    <b-form-group
        id="formNameGroup"
        label-for="formName"
        label="フォーム名"
        label-cols="3"
    >
      <b-form-input
          id="formName"
          v-model="form.name"
          type="text"
      />
    </b-form-group>
    <b-form-group
        id="formTitleGroup"
        label-for="formTitle"
        label="画面に表示するフォームのタイトル"
        label-cols="3"
    >
      <b-form-input
          id="formTitle"
          v-model="form.title"
          type="text"
      />
    </b-form-group>
    <b-form-group
        id="urlAfterCancelGroup"
        label-for="urlAfterCancel"
        label="フォーム入力キャンセル時に開くページのURL"
        label-cols="3"
    >
      <b-form-input
          id="urlAfterCancel"
          v-model="form.cancel_url"
          type="text"
      />
    </b-form-group>
    <b-form-group
        id="urlAfterCompleteGroup"
        label-for="urlAfterComplete"
        label="フォーム入力完了後に開くページのURL"
        label-cols="3"
    >
      <b-form-input
          id="urlAfterComplete"
          v-model="form.complete_url"
          type="text"
      />
    </b-form-group>
    <b-form-group
        id="formInputHeaderGroup"
        label-for="formInputHeader"
        label="フォーム入力画面上部の文言"
        label-cols="3"
    >
      <b-form-textarea
          id="formInputHeader"
          v-model="form.input_header"
          :rows="3"
          :max-rows="10"
      />
    </b-form-group>
    <b-form-group
        id="formConfirmHeaderGroup"
        label-for="formConfirmHeader"
        label="フォーム入力後確認画面上部の文言"
        label-cols="3"
    >
      <b-form-textarea
          id="formConfirmHeader"
          v-model="form.confirm_header"
          :rows="3"
          :max-rows="10"
      />
    </b-form-group>
    <b-form-group
        id="formCompleteTextGroup"
        label-for="formCompleteText"
        label="フォーム確認後に表示する文言"
        label-cols="3"
    >
      <b-form-textarea
          id="formCompleteText"
          v-model="form.complete_text"
          :rows="3"
          :max-rows="10"
      />
    </b-form-group>
    <b-form-group
        id="formStopTextGroup"
        label-for="formStopText"
        label="フォーム休止時に表示する文言"
        label-cols="3"
    >
      <b-form-textarea
          id="formStopText"
          v-model="form.close_text"
          :rows="3"
          :max-rows="10"
      />
    </b-form-group>
    <ColumnList formId="1"/>
    <TransferTaskList />
  </main>
</template>
