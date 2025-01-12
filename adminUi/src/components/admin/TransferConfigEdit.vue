<template>
  <BContainer class="text-left">
    <BForm>
      <BRow>
        <BCol>
          <BFormGroup label="転送設定名" label-for="transferConfigName" label-cols="2">
            <BFormInput
                id="transferConfigName"
                v-model="transferConfig.name"
                type="text"
                :state="transferConfigNameState"
                required
                @input="validateTransferConfigName"
            />
            <BFormInvalidFeedback>
              転送設定名を入力してください
            </BFormInvalidFeedback>
          </BFormGroup>
        </BCol>
      </BRow>
      <BRow>
        <BCol>
          <BFormGroup label="転送タイプ" label-for="transferConfigTypeCode" label-cols="4">
            <BFormSelect
                id="transferConfigTypeCode"
                v-model="transferConfig.type_code"
                :options="typeOptions"
                :state="transferConfigTypeCodeState"
                required
                @input="validateTransferConfigTypeCodeState"
            />
            <BFormInvalidFeedback>
              転送タイプを選択してください
            </BFormInvalidFeedback>
          </BFormGroup>
        </BCol>
        <BCol>
          <BFormGroup label="ステータス" label-for="transferConfigStatus" label-cols="2">
            <BFormSelect
                id="transferConfigStatus"
                v-model="transferConfig.status"
                :options="statusOptions"
                :state="transferConfigStatusState"
                required
                @input="validateTransferConfigStatusState"
            />
            <BFormInvalidFeedback>
              ステータスを選択してください
            </BFormInvalidFeedback>
          </BFormGroup>
        </BCol>
      </BRow>
      <div>
        <TransferConfigEditSalesforce ref="configEditSalesforceRef" v-if="transferConfig.type_code === 'salesforce'" />
        <TransferConfigEditMail ref="configEditMailRef" v-if="transferConfig.type_code === 'mail'" />
      </div>
    </BForm>
  </BContainer>
</template>

<script setup>
import {getCurrentInstance, onMounted, ref, computed, reactive, nextTick} from "vue"
import {BTable} from "bootstrap-vue-3"
import TransferConfigEditMail from "@/components/admin/TransferConfigEditMail.vue";
import TransferConfigEditSalesforce from "@/components/admin/TransferConfigEditSalesforce.vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const configEditMailRef = ref({ loadData: () => {} })
const configEditSalesforceRef = ref({ loadData: () => {} })

const transferConfig = reactive({
  id: null,
  type_code: '',
  name: '',
  status: 1,
  detail: {
    mail: {
      use_cc: false,
      use_replyto: false,
      use_bcc: false,
      mail_address_list: []
    },
    salesforce: {
      sf_user_name: '',
      sf_password: '',
      sf_client_id: '',
      sf_client_secret: '',
      sf_domain: '',
      api_version: ''
    }
  }
})

const typeOptions = ref([
  { value: 'salesforce', text: 'Salesforce' },
  { value: 'mail', text: 'Mail' },
])

const statusOptions = ref([
  { value: 0, text: '無効' },
  { value: 1, text: '有効' },
])

const mailAddressFields = ref([
  { key: 'name', sortable: true, label: '名前'},
  { key: 'address', sortable: true, label: 'メールアドレス'},
])

const loadConfig = (id) => {
  $http.get('/transfer/config/' + id)
      .then(async (response) => {
        transferConfig.id = response.data.id
        transferConfig.name = response.data.name
        transferConfig.config_index = response.data.config_index
        transferConfig.type_code = response.data.type_code
        transferConfig.detail = response.data.detail
        setTypeOptions(transferConfig.type_code)

        await nextTick()
        if (transferConfig.type_code === 'mail' && configEditMailRef.value) {
          configEditMailRef.value.loadData(transferConfig.detail.mail);
        } else if (transferConfig.type_code === 'salesforce' && configEditSalesforceRef.value) {
          configEditSalesforceRef.value.loadData(transferConfig.detail.salesforce);
        }
      })
}

const saveConfig = () => {
  console.log(configEditSalesforceRef)
  if (transferConfig.type_code === 'mail') {
    transferConfig.detail.mail = configEditMailRef.value.getData()
  }
  if (transferConfig.type_code === 'salesforce') {
    transferConfig.detail.salesforce = configEditSalesforceRef.value.getData()
  }

  const saveData = JSON.parse(JSON.stringify(transferConfig))
  if (transferConfig.type_code === 'mail') {
    delete saveData.detail.salesforce
  }
  if (transferConfig.type_code === 'salesforce') {
    delete saveData.detail.mail
  }
  console.log(saveData)
  // データの確認のため一旦コメントアウト後で戻す

  $http.post('/transfer/config', saveData)
      .then(response => {
        alert('ok')
      })
}

const addMailAddress = () => {
  if (!Array.isArray(transferConfig.detail.mail.mail_address_list)) {
    transferConfig.detail.mail.mail_address_list = [];
  }
  transferConfig.detail.mail.mail_address_list.push({ transferconfig_mail_id: transferConfig.detail.mail.id, name:'', address:'', address_index: transferConfig.detail.mail.mail_address_list.length + 1 })
}

const updateMailAddress = (index, key, value) => {
  transferConfig.detail.mail.mail_address_list[index][key] = value;
  // Vueのリアクティブシステムに変更を通知
  transferConfig.detail.mail.mail_address_list = [...transferConfig.detail.mail.mail_address_list];
};

const clearModal = () => {
  transferConfig.id = null
  transferConfig.type_code = ''
  transferConfig.name = ''
  transferConfig.status = 1
  transferConfig.type_code = ''
  transferConfig.detail = {
    mail: {
      use_cc: false,
      use_replyto: false,
      use_bcc: false,
      mail_address_list: []
    },
    salesforce: {
      sf_user_name: '',
      sf_password: '',
      sf_client_id: '',
      sf_client_secret: '',
      sf_domain: '',
      api_version: ''
    }
  }
  typeOptions.value = [
    { value: 'salesforce', text: 'Salesforce' },
    { value: 'mail', text: 'Mail' },
  ]
}

const transferConfigTypeCodeState = computed(() => transferConfig.type_code.trim() !== '')
const transferConfigNameState = computed(() => transferConfig.name.trim() !== '')
const transferConfigStatusState = computed(() => transferConfig.status !== null)

const validateTransferConfigTypeCodeState = () => {
  return transferConfigTypeCodeState.value
}
const validateTransferConfigName = () => {
  return transferConfigNameState.value
}
const validateTransferConfigStatusState = () => {
  return transferConfigStatusState.value
}

const setTypeOptions = (type) => {
  if (type === 'salesforce') {
    typeOptions.value = [{ value: 'salesforce', text: 'Salesforce' }]
  }
  if (type === 'mail') {
    typeOptions.value = [{ value: 'mail', text: 'Mail' }]
  }
}

defineExpose({
  loadConfig,
  saveConfig,
  clearModal,
})

</script>
