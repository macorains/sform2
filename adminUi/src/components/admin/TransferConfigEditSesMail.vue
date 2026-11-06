<template>
  <BRow class="mb-1 mt-4">
    <BCol>
      <h5>Mail設定項目</h5>
    </BCol>
  </BRow>
  <BRow>
    <BCol class="d-flex gap-4">
      <BFormCheckbox
          id="use_cc"
          v-model="transferConfigDetailMail.use_cc"
          name="use_cc"
      >
        ccを使う
      </BFormCheckbox>
      <BFormCheckbox
          id="use_bcc"
          v-model="transferConfigDetailMail.use_bcc"
          name="use_bcc"
      >
        bccを使う
      </BFormCheckbox>
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
      <BFormInput
          v-if="editingIndex === data.index"
          v-model="editingItem.name"
          placeholder="名前を入力"
      />
      <span v-else>{{ data.item.name }}</span>
    </template>
    <template #cell(address)="data">
      <BFormInput
          v-if="editingIndex === data.index"
          v-model="editingItem.address"
          placeholder="メールアドレスを入力"
      />
      <span v-else>{{ data.item.address }}</span>
    </template>
    <template #cell(actions)="data">
      <template v-if="editingIndex === data.index">
        <BButton size="sm" variant="primary" class="me-1" @click="confirmEdit">更新</BButton>
        <BButton size="sm" variant="secondary" @click="cancelEdit">取消</BButton>
      </template>
      <template v-else>
        <BButton size="sm" variant="outline-primary" class="me-1" @click="startEdit(data.index)" :disabled="editingIndex !== null">編集</BButton>
        <BButton size="sm" variant="outline-danger" @click="deleteMailAddress(data.index)" :disabled="editingIndex !== null">削除</BButton>
      </template>
    </template>
  </BTable>

</template>
<script setup>
import {BTable} from "bootstrap-vue-3";
import {ref, watch} from "vue";

const props = defineProps({
  mailData: {
    type: Object,
    default: null
  }
})

const transferConfigDetailMailDefault = {
  id: null,
  use_cc: false,
  use_bcc: false,
  use_replyto: false,
  mail_address_list: []
}
const transferConfigDetailMail = ref({ ...transferConfigDetailMailDefault })

const editingIndex = ref(null)
const editingItem = ref({})

const mailAddressFields = [
  { key: 'address_index', label: '番号' },
  { key: 'name', label: '名前' },
  { key: 'address', label: 'メールアドレス' },
  { key: 'actions', label: '' },
]

const loadData = (data) => {
  if (data) {
    transferConfigDetailMail.value = {
      id: data.id,
      use_cc: data.use_cc,
      use_bcc: data.use_bcc,
      use_replyto: data.use_replyto,
      mail_address_list: data.mail_address_list
    }
    editingIndex.value = null
    editingItem.value = {}
  }
}

watch(
  () => props.mailData,
  (newData) => {
    if (newData) {
      loadData(newData)
    }
  },
  { immediate: true }
)

const getData = () => {
  return transferConfigDetailMail.value
}

const clearData = () => {
}

const startEdit = (index) => {
  editingIndex.value = index
  editingItem.value = { ...transferConfigDetailMail.value.mail_address_list[index] }
}

const confirmEdit = () => {
  transferConfigDetailMail.value.mail_address_list[editingIndex.value] = { ...editingItem.value }
  transferConfigDetailMail.value.mail_address_list = [...transferConfigDetailMail.value.mail_address_list]
  editingIndex.value = null
  editingItem.value = {}
}

const cancelEdit = () => {
  editingIndex.value = null
  editingItem.value = {}
}

const deleteMailAddress = (index) => {
  transferConfigDetailMail.value.mail_address_list.splice(index, 1)
  transferConfigDetailMail.value.mail_address_list = transferConfigDetailMail.value.mail_address_list
    .map((item, i) => ({ ...item, address_index: i + 1 }))
}

const addMailAddress = () => {
  const list = transferConfigDetailMail.value.mail_address_list
  if (!Array.isArray(list)) {
    transferConfigDetailMail.value.mail_address_list = []
  }
  transferConfigDetailMail.value.mail_address_list.push({
    transferconfig_mail_id: transferConfigDetailMail.value.id,
    name: '',
    address: '',
    address_index: transferConfigDetailMail.value.mail_address_list.length + 1
  })
  const newIndex = transferConfigDetailMail.value.mail_address_list.length - 1
  editingIndex.value = newIndex
  editingItem.value = { ...transferConfigDetailMail.value.mail_address_list[newIndex] }
}

defineExpose({
  loadData,
  getData,
  clearData,
})

</script>
