<template>
  <BRow class="mb-1 mt-4">
    <BCol>
      <h5>Mail設定項目</h5>
    </BCol>
  </BRow>
  <BRow>
    <BCol>
      <BFormCheckbox
          id="use_cc"
          v-model="transferConfigDetailMail.use_cc"
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
          v-model="transferConfigDetailMail.use_bcc"
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
          v-model="transferConfigDetailMail.use_replyto"
          name="use_replyto"
      >
        replyToを使う
      </BFormCheckbox>
    </BCol>
  </BRow>
  <BRow class="mb-1 mt-4">
    <BCol>
      <b>cc, bcc, reply-toで使用するメールアドレス</b><BButton class="ms-auto d-block small" @click="addMailAddress">メールアドレス追加</BButton>
    </BCol>
  </BRow>
  <BTable striped hover :items="transferConfigDetailMail.mail_address_list" :fields="mailAddressFields">
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
<script setup>
import {BTable} from "bootstrap-vue-3";
import {ref} from "vue";

const transferConfigDetailMailDefault = {
  id: null,
  use_cc: false,
  use_bcc: false,
  use_replyto: false,
  mail_address_list: []
}
const transferConfigDetailMail = ref(transferConfigDetailMailDefault)

const loadData = (data) => {
  if (data) {
    transferConfigDetailMail.value = {
      id: data.id,
      use_cc: data.use_cc,
      use_bcc: data.use_bcc,
      use_replyto: data.use_replyto,
      mail_address_list: data.mail_address_list
    }
  }
}


const getData = () => {
  return transferConfigDetailMail.value
}

const clearData = (data) => {

}

const addMailAddress = () => {
  if (!Array.isArray(transferConfigDetailMail.mail_address_list)) {
    transferConfigDetailMail.mail_address_list = [];
  }
  transferConfigDetailMail.mail_address_list.push({ transferconfig_mail_id: transferConfigDetailMail.id, name:'', address:'', address_index: transferConfigDetailMail.mail_address_list.length + 1 })
}

const updateMailAddress = (index, key, value) => {
  transferConfigDetailMail.mail_address_list[index][key] = value;
  // Vueのリアクティブシステムに変更を通知
  transferConfigDetailMail.mail_address_list = [...transferConfigDetailMail.mail_address_list]
}

defineExpose({
  loadData,
  getData,
  clearData,
})

</script>
