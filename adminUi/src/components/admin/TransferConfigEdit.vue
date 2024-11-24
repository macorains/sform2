<template>
  <BContainer class="text-left">
    <BForm>
      <BRow class="mb-2">
        <BCol>
          <BFormGroup label="転送タイプ" label-for="transferConfigTypeCode" label-cols="2">
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
      </BRow>
      <BRow class="mb-2">
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
      <BRow class="mb-2">
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
      <template v-if="transferConfig.type_code == 'salesforce'">
        <BRow class="mb-2 mt-4">
          <BCol>
            <h5>Salesforce設定項目</h5>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="ユーザー名" label-for="transferConfigSalesforceUser" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforceUser"
                  v-model="transferConfig.detail.salesforce.sf_user_name"
                  type="text"
                  :state="transferConfigSalesforceUserState"
                  required
                  @input="validateTransferConfigSalesforceUser"
              />
              <BFormInvalidFeedback>
                Salesforceユーザー名を入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="パスワード" label-for="transferConfigSalesforcePassword" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforcePassword"
                  v-model="transferConfig.detail.salesforce.sf_password"
                  type="password"
                  :state="transferConfigSalesforcePasswordState"
                  required
                  @input="validateTransferConfigSalesforcePassword"
              />
              <BFormInvalidFeedback>
                Salesforceパスワードを入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="クライアントID" label-for="transferConfigSalesforceClientId" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforceClientId"
                  v-model="transferConfig.detail.salesforce.sf_client_id"
                  type="text"
                  :state="transferConfigSalesforceClientIdState"
                  required
                  @input="validateTransferConfigSalesforceClientId"
              />
              <BFormInvalidFeedback>
                SalesforceクライアントIDを入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="クライアントシークレット" label-for="transferConfigSalesforceClientSecret" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforceClientSecret"
                  v-model="transferConfig.detail.salesforce.sf_client_secret"
                  type="text"
                  :state="transferConfigSalesforceClientSecretState"
                  required
                  @input="validateTransferConfigSalesforceClientSecret"
              />
              <BFormInvalidFeedback>
                Salesforceクライアントシークレットを入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="ドメイン" label-for="transferConfigSalesforceDomain" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforceDomain"
                  v-model="transferConfig.detail.salesforce.sf_domain"
                  type="text"
                  :state="transferConfigSalesforceDomainState"
                  required
                  @input="validateTransferConfigSalesforceDomain"
              />
              <BFormInvalidFeedback>
                Salesforceドメインを入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
        <BRow class="mb-2">
          <BCol>
            <BFormGroup label="APIバージョン" label-for="transferConfigSalesforceApiVersion" label-cols="2">
              <BFormInput
                  id="transferConfigSalesforceApiVersion"
                  v-model="transferConfig.detail.salesforce.api_version"
                  type="text"
                  :state="transferConfigSalesforceApiVersionState"
                  required
                  @input="validateTransferConfigSalesforceApiVersion"
              />
              <BFormInvalidFeedback>
                Salesforce APIバージョンを入力してください
              </BFormInvalidFeedback>
            </BFormGroup>
          </BCol>
        </BRow>
      </template>
      <template v-if="transferConfig.type_code == 'mail'">
        <BRow class="mb-2 mt-4">
          <BCol>
            <h5>Mail設定項目</h5>
          </BCol>
        </BRow>
        <BRow>
          <BCol>
            <BFormCheckbox
                id="use_cc"
                v-model="transferConfig.detail.mail.use_cc"
                name="use_cc"
            >
              ccを使う
            </BFormCheckbox>
          </BCol>
        </BRow>
        <BRow>
          <BCol>
            <BFormCheckbox
                id="use_bcc"
                v-model="transferConfig.detail.mail.use_bcc"
                name="use_bcc"
            >
              bccを使う
            </BFormCheckbox>
          </BCol>
        </BRow>
        <BRow>
          <BCol>
            <BFormCheckbox
                id="use_replyto"
                v-model="transferConfig.detail.mail.use_replyto"
                name="use_replyto"
            >
              replyToを使う
            </BFormCheckbox>
          </BCol>
        </BRow>
        <BRow class="mb-2 mt-4">
          <BCol>
            <b>cc, bcc, reply-toで使用するメールアドレス</b><BButton class="ms-auto d-block small" @click="addMailAddress">メールアドレス追加</BButton>
          </BCol>
        </BRow>
        <BTable striped hover :items="transferConfig.detail.mail.mail_address_list" :fields="mailAddressFields">
          <template #cell(name)="data">
            <b-form-input
                v-model="data.item.name"
                placeholder="名前を入力"
                @input="updateMailAddress(data.index, 'name', data.item.name)"
            ></b-form-input>
          </template>
          <template #cell(address)="data">
            <b-form-input
                v-model="data.item.address"
                placeholder="メールアドレスを入力"
                @input="updateMailAddress(data.index, 'address', data.item.address)"
            ></b-form-input>
          </template>
        </BTable>
      </template>
    </BForm>
  </BContainer>
</template>

<script setup>
import {getCurrentInstance, onMounted, ref, computed, reactive } from "vue";
import {BTable} from "bootstrap-vue-3";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

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
      .then(response => {
        transferConfig.id = response.data.id
        transferConfig.name = response.data.name
        transferConfig.config_index = response.data.config_index
        transferConfig.type_code = response.data.type_code
        transferConfig.detail = response.data.detail
        setTypeOptions(transferConfig.type_code)
      })
}

const saveConfig = () => {
  const saveData = JSON.parse(JSON.stringify(transferConfig))
  if (transferConfig.type_code === 'mail') {
    // saveData.detail.salesforce = null
    delete saveData.detail.salesforce
  }
  if (transferConfig.type_code === 'salesforce') {
    // saveData.detail.mail = null
    delete saveData.detail.mail
  }
  console.log(saveData)
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
const transferConfigSalesforceUserState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.sf_user_name.trim() !== '')
const transferConfigSalesforcePasswordState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.sf_password.trim() !== '')
const transferConfigSalesforceClientIdState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.sf_client_id.trim() !== '')
const transferConfigSalesforceClientSecretState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.sf_client_secret.trim() !== '')
const transferConfigSalesforceDomainState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.sf_domain.trim() !== '')
const transferConfigSalesforceApiVersionState = computed(() => transferConfig.type_code === 'salesforce' && transferConfig.detail.salesforce.api_version.trim() !== '')

const validateTransferConfigTypeCodeState = () => {
  return transferConfigTypeCodeState.value
}
const validateTransferConfigName = () => {
  return transferConfigNameState.value
}
const validateTransferConfigStatusState = () => {
  return transferConfigStatusState.value
}
const validateTransferConfigSalesforceUser = () => {
  return transferConfigSalesforceUserState.value
}
const validateTransferConfigSalesforcePassword = () => {
  return transferConfigSalesforcePasswordState.value
}
const validateTransferConfigSalesforceClientId = () => {
  return transferConfigSalesforceClientIdState.value
}
const validateTransferConfigSalesforceClientSecret = () => {
  return transferConfigSalesforceClientSecretState.value
}
const validateTransferConfigSalesforceDomain = () => {
  return transferConfigSalesforceDomainState.value
}
const validateTransferConfigSalesforceApiVersion = () => {
  return transferConfigSalesforceApiVersionState.value
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
