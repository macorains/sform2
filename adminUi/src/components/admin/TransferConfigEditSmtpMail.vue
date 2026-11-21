<template>
  <BRow class="mb-1 mt-4">
    <BCol>
      <h5>SMTP設定項目</h5>
    </BCol>
  </BRow>
  <BRow class="mb-0 mt-0">
    <BCol>
      <BFormGroup label="SMTPサーバ名" label-for="smtpHost" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="smtpHost"
            v-model="smtpMailConfig.smtp_host"
            type="text"
            :state="smtpHostState"
            required
            @input="validateSmtpHost"
        />
        <BFormInvalidFeedback>
          SMTPサーバ名を入力してください
        </BFormInvalidFeedback>
      </BFormGroup>
    </BCol>
    <BCol>
      <BFormGroup label="ポート番号" label-for="smtpPort" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="smtpPort"
            v-model.number="smtpMailConfig.smtp_port"
            type="number"
            :state="smtpPortState"
            required
            @input="validateSmtpPort"
        />
        <BFormInvalidFeedback>
          ポート番号を入力してください
        </BFormInvalidFeedback>
      </BFormGroup>
    </BCol>
  </BRow>
  <BRow class="mb-0 mt-0">
    <BCol>
      <BFormGroup label="ユーザー名" label-for="smtpUser" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="smtpUser"
            v-model="smtpMailConfig.smtp_user"
            type="text"
            :state="smtpUserState"
            required
            @input="validateSmtpUser"
        />
        <BFormInvalidFeedback>
          ユーザー名を入力してください
        </BFormInvalidFeedback>
      </BFormGroup>
    </BCol>
    <BCol>
      <BFormGroup label="パスワード" label-for="smtpPassword" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="smtpPassword"
            v-model="smtpMailConfig.smtp_password"
            type="password"
            :state="smtpPasswordState"
            required
            @input="validateSmtpPassword"
        />
        <BFormInvalidFeedback>
          パスワードを入力してください
        </BFormInvalidFeedback>
      </BFormGroup>
    </BCol>
  </BRow>
  <BRow class="mb-0 mt-0">
    <BCol>
      <BFormGroup label="送信元メールアドレス" label-for="fromAddress" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="fromAddress"
            v-model="smtpMailConfig.from_address"
            type="email"
            :state="fromAddressState"
            required
            @input="validateFromAddress"
        />
        <BFormInvalidFeedback>
          送信元メールアドレスを入力してください
        </BFormInvalidFeedback>
      </BFormGroup>
    </BCol>
  </BRow>

  <BRow class="mb-1 mt-4">
    <BCol>
      <h5>テストメール送信</h5>
    </BCol>
  </BRow>
  <BRow class="mb-0 mt-0">
    <BCol>
      <BFormGroup label="送信先メールアドレス" label-for="testToAddress" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="testToAddress"
            v-model="testToAddress"
            type="email"
        />
      </BFormGroup>
    </BCol>
  </BRow>
  <BRow>
    <BCol cols="3">
      <BButton class="button" @click="sendTestMail" :disabled="isSending">
        {{ isSending ? '送信中...' : 'テストメール送信' }}
      </BButton>
    </BCol>
    <BCol>{{ testMailResult }}</BCol>
  </BRow>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { getCurrentInstance } from 'vue'

const props = defineProps({
  smtpMailData: {
    type: Object,
    default: null
  }
})

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const smtpMailConfig = reactive({
  id: null,
  transfer_config_id: null,
  smtp_host: '',
  smtp_port: 587,
  smtp_user: '',
  from_address: '',
  smtp_password: '',
})

const testToAddress = ref('')
const testMailResult = ref('')
const isSending = ref(false)

const smtpHostState     = computed(() => smtpMailConfig.smtp_host.trim() !== '')
const smtpPortState     = computed(() => smtpMailConfig.smtp_port > 0)
const smtpUserState     = computed(() => smtpMailConfig.smtp_user.trim() !== '')
const smtpPasswordState = computed(() => smtpMailConfig.smtp_password.trim() !== '')
const fromAddressState  = computed(() => smtpMailConfig.from_address.trim() !== '')

const validateSmtpHost     = () => smtpHostState.value
const validateSmtpPort     = () => smtpPortState.value
const validateSmtpUser     = () => smtpUserState.value
const validateSmtpPassword = () => smtpPasswordState.value
const validateFromAddress  = () => fromAddressState.value

const loadData = (data) => {
  if (!data) return
  smtpMailConfig.id                = data.id
  smtpMailConfig.transfer_config_id = data.transfer_config_id
  smtpMailConfig.smtp_host         = data.smtp_host
  smtpMailConfig.smtp_port         = data.smtp_port
  smtpMailConfig.smtp_user         = data.smtp_user
  smtpMailConfig.from_address      = data.from_address
  smtpMailConfig.smtp_password     = data.smtp_password
}

watch(
  () => props.smtpMailData,
  (newData) => {
    if (newData) loadData(newData)
  },
  { immediate: true }
)

const getData = () => {
  return { ...smtpMailConfig }
}

const clearData = () => {
  smtpMailConfig.id                = null
  smtpMailConfig.transfer_config_id = null
  smtpMailConfig.smtp_host         = ''
  smtpMailConfig.smtp_port         = 587
  smtpMailConfig.smtp_user         = ''
  smtpMailConfig.from_address      = ''
  smtpMailConfig.smtp_password     = ''
  testToAddress.value  = ''
  testMailResult.value = ''
}

const sendTestMail = () => {
  isSending.value      = true
  testMailResult.value = ''
  const requestData = {
    smtp_host:     smtpMailConfig.smtp_host,
    smtp_port:     smtpMailConfig.smtp_port,
    smtp_user:     smtpMailConfig.smtp_user,
    from_address:  smtpMailConfig.from_address,
    smtp_password: smtpMailConfig.smtp_password,
    to_address:    testToAddress.value,
  }
  $http.post('/transfer/smtp/test', requestData)
    .then(response => {
      testMailResult.value = '送信成功: ' + response.data
    })
    .catch(error => {
      if (error.response) {
        testMailResult.value = '送信失敗: ' + (error.response.data || error.response.statusText)
      } else {
        testMailResult.value = '送信失敗: ネットワークエラー'
      }
    })
    .finally(() => {
      isSending.value = false
    })
}

defineExpose({
  loadData,
  getData,
  clearData,
})
</script>
