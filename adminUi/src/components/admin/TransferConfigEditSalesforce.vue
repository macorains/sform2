<template>
  <BRow class="mb-1 mt-4">
    <BCol>
      <h5>Salesforce設定項目</h5>
    </BCol>
  </BRow>
  <BRow class="mb-0 mt-0 mp-0 mb-0">
    <BCol>
      <BFormGroup label="ユーザー名" label-for="transferConfigSalesforceUser" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforceUser"
            v-model="transferConfigDetailSalesforce.sf_user_name"
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
    <BCol>
      <BFormGroup label="パスワード" label-for="transferConfigSalesforcePassword" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforcePassword"
            v-model="transferConfigDetailSalesforce.sf_password"
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
  <BRow class="mb-0 mt-0 mp-0 mb-0">
    <BCol>
      <BFormGroup label="ドメイン" label-for="transferConfigSalesforceDomain" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforceDomain"
            v-model="transferConfigDetailSalesforce.sf_domain"
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
    <BCol>
      <BFormGroup label="APIバージョン" label-for="transferConfigSalesforceApiVersion" label-cols="4">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforceApiVersion"
            v-model="transferConfigDetailSalesforce.api_version"
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
  <BRow class="mb-0 mt-0 mp-0 mb-0">
    <BCol>
      <BFormGroup label="クライアントID" label-for="transferConfigSalesforceClientId" label-cols="2">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforceClientId"
            v-model="transferConfigDetailSalesforce.sf_client_id"
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
  <BRow class="mb-0 mt-0 mp-0 mb-0">
    <BCol>
      <BFormGroup label="クライアントシークレット" label-for="transferConfigSalesforceClientSecret" label-cols="2">
        <BFormInput
            class="form-control-sm"
            id="transferConfigSalesforceClientSecret"
            v-model="transferConfigDetailSalesforce.sf_client_secret"
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
  <BRow>
    <BCol cols="3">
      <BButton class="button" @click="salesforceTest">Salesforce接続テスト</BButton>
    </BCol>
    <BCol>{{ salesforceCheckResult.message }}</BCol>
  </BRow>
  <TransferConfigEditSalesforceObject ref="configEditSalesforceObjectRef"/>
</template>
<script setup>
import {BTable} from "bootstrap-vue-3"
import TransferConfigEditSalesforceObject from "@/components/admin/TransferConfigEditSalesforceObject.vue"
import {computed, getCurrentInstance, reactive, ref} from "vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const transferConfigDetailSalesforce = reactive({
  sf_user_name: '',
  sf_password: '',
  sf_client_id: '',
  sf_client_secret: '',
  sf_domain: '',
  api_version: '',
  objects: []
})

const salesforceCheckResult = ref({
  code: null,
  message: ''
})

const configEditSalesforceObjectRef = ref({ loadData: () => {} })

const transferConfigSalesforceUserState = computed(() => transferConfigDetailSalesforce.sf_user_name.trim() !== '')
const transferConfigSalesforcePasswordState = computed(() => transferConfigDetailSalesforce.sf_password.trim() !== '')
const transferConfigSalesforceClientIdState = computed(() => transferConfigDetailSalesforce.sf_client_id.trim() !== '')
const transferConfigSalesforceClientSecretState = computed(() => transferConfigDetailSalesforce.sf_client_secret.trim() !== '')
const transferConfigSalesforceDomainState = computed(() => transferConfigDetailSalesforce.sf_domain.trim() !== '')
const transferConfigSalesforceApiVersionState = computed(() => transferConfigDetailSalesforce.api_version.trim() !== '')

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

const loadData = (data) => {
    transferConfigDetailSalesforce.sf_user_name = data.sf_user_name
    transferConfigDetailSalesforce.sf_password = data.sf_password
    transferConfigDetailSalesforce.sf_client_id = data.sf_client_id
    transferConfigDetailSalesforce.sf_client_secret = data.sf_client_secret
    transferConfigDetailSalesforce.sf_domain = data.sf_domain
    transferConfigDetailSalesforce.api_version = data.api_version
    configEditSalesforceObjectRef.value.loadData(data.objects, data.transfer_config_id)
}

const getData = () => {
  transferConfigDetailSalesforce.objects = configEditSalesforceObjectRef.value.getData()
  console.log(transferConfigDetailSalesforce)
  return transferConfigDetailSalesforce
}

const clearData = (data) => {

}

const salesforceTest = () => {
  const requestData = {
    username: transferConfigDetailSalesforce.sf_user_name,
    password: transferConfigDetailSalesforce.sf_password,
    client_id: transferConfigDetailSalesforce.sf_client_id,
    client_secret: transferConfigDetailSalesforce.sf_client_secret,
    api_version: transferConfigDetailSalesforce.api_version,
    domain: transferConfigDetailSalesforce.sf_domain
  }

  $http.post('/transfer/salesforce/check', requestData)
      .then(response => {
        salesforceCheckResult.value.code = response.request.status
        salesforceCheckResult.value.message = response.request.statusText
      })
}

defineExpose({
  loadData,
  getData,
  clearData,
})
</script>
