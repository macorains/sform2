<script setup>
import {getCurrentInstance, onMounted, ref} from "vue";
import {BTable} from "bootstrap-vue-3";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const transferConfig = ref({
  mail: {},
  salesforce: {},
  detail: {
    mail: {}
  }
})

const typeOptions = ref([
  { value: 'salesforce', text: 'Salesforce' },
  { value: 'mail', text: 'Mail' },
])

const mailAddressFields = ref([
  { key: 'name', sortable: true, label: '名前'},
  { key: 'mail_address', sortable: true, label: 'メールアドレス'},
])
const setConfig = (id) => {
  $http.get('/transfer/config/' + id)
      .then(response => {
        transferConfig.value = response.data
        setTypeOptions(transferConfig.value.type_code)
        console.log(response.data)
      })
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
  setConfig
})

</script>
<template>
  <BContainer class="text-left">
    <BRow class="mb-2">
      <BCol cols="2">
        転送タイプ
      </BCol>
      <BCol cols="10">
        <BFormSelect v-model="transferConfig.type_code" :options="typeOptions"></BFormSelect>
      </BCol>
    </BRow>
    <BRow class="mb-2">
      <BCol cols="2">
        名前
      </BCol>
      <BCol cols="10">
        <BFormInput
            v-model="transferConfig.name"
            type="text"
        />
      </BCol>
    </BRow>
    <template v-if="transferConfig.type_code == 'salesforce'">
      <BRow class="mb-2 mt-4">
        <BCol>
          <h5>Salesforce設定項目</h5>
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          ユーザー名
        </BCol>
        <BCol>
          <BFormInput
              id="user"
              v-model="transferConfig.detail.salesforce.sf_user_name"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          パスワード
        </BCol>
        <BCol>
          <BFormInput
              id="password"
              v-model="transferConfig.detail.salesforce.sf_password"
              type="password"
            />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          クライアントID
        </BCol>
        <BCol>
          <BFormInput
              id="salesforce_client_id"
              v-model="transferConfig.detail.salesforce.sf_client_id"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          クライアントシークレット
        </BCol>
        <BCol>
          <BFormInput
              id="salesforce_client_secret"
              v-model="transferConfig.detail.salesforce.sf_client_secret"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          ドメイン
        </BCol>
        <BCol>
          <BFormInput
              id="salesforce_domain"
              v-model="transferConfig.detail.salesforce.sf_domain"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          APIバージョン
        </BCol>
        <BCol>
          <BFormInput
              id="salesforce_api_version"
              v-model="transferConfig.detail.salesforce.api_version"
              type="text"
          />
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
      <BTable striped hover :items="transferConfig.detail.mail.mail_address_list" :fields="mailAddressFields">
      </BTable>
    </template>
  </BContainer>
</template>
