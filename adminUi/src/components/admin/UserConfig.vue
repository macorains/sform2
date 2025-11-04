<template>
  <div v-if="loading" class="loading-overlay">
    <BSpinner label="Processing..." variant="light" />
  </div>
  <div class="container">
    <BTable striped hover :items="userList" :fields="fields">
      <template #cell(actions)="row">
        <BButton variant="primary" size="sm" @click="userEdit(row.item)" class="me-1">
          <i class="bi bi-pencil"></i>
          編集
        </BButton>
        <BButton variant="danger" size="sm" @click="userDelete(row.item)">
          <i class="bi bi-trash"></i>
          削除
        </BButton>
      </template>
    </BTable>
    <BRow>
      <BCol class="text-end">
        <BButton
            class="mt-4 right-aligned"
            @click="add"
        >
          <i class="bi bi-file-earmark-plus"></i>
          ユーザーの追加
        </BButton>
      </BCol>
    </BRow>
  </div>
  <BModal v-model="editModalVisible" size="lg" title="ユーザー編集" @ok="save" @hidden="close" :ok-disabled="isOkDisabled" scrollable>
    <UserConfigEdit ref="userEditRef" @load="reload" @checkExists="checkExists" :email-check-result="emailCheckResult" />
  </BModal>
</template>
<script setup>

import {BButton, BModal, BSpinner, BTable} from "bootstrap-vue-3";
import {computed, getCurrentInstance, inject, onMounted, ref} from "vue";
import { useHttpRequest } from "@/composables/useHttpRequest.js"
import UserConfigEdit from "@/components/admin/UserConfigEdit.vue";


const instance = getCurrentInstance()
const { requestGet, requestDelete, loading } = useHttpRequest()

const editModalVisible = ref(false)
const userEditRef = ref(null)
const emailCheckResult = ref(false)

const fields = ref([
  { key: 'full_name', sortable: true, label: '名前'},
  { key: 'username', sortable: true, label: 'メールアドレス'},
  { key: 'role', sortable: true, label: '役割'},
  { key: 'actions', label: '操作' },
])

const userList = ref([])
const showConfirm = inject("showConfirm")

onMounted(() => {
  load()
})

const load = () => {
  requestGet(
      '/user',
      response => {
        userList.value = response.data.user_list.map(d => convert(d))
      }
  )
}

const checkExists = (data) => {
  emailCheckResult.value = userList.value.some((user) => user.email === data.email && user.id !== data.user_id)
}

const convert = (source) => {
  source.full_name = source.full_name || (source.last_name + ' ' + source.first_name)
  return source
}

const userEdit = (data) => {
  userEditRef.value.loadUser(data)
  editModalVisible.value = true
}

const userDelete = (data) => {
  showConfirm('ユーザー: ' + data.username + 'を削除します。よろしいですか？', (result) =>{
    if(result) {
      requestDelete(
          '/user/' + data.id,
          _ => {
            load()
          }
      )
    } else {
      // なにもしない
    }
  })
}

const isOkDisabled = computed(() => !userEditRef.value?.okButtonEnabled())

const save = () => {
  userEditRef.value.saveUser()
}

const close = () => {
  userEditRef.value.clearModal()
  load()
}

const reload = () => {
  load()
}

const add = () => {
  const data = {
    first_name: '',
    last_name: '',
    full_name: '',
    email: '',
    role: 'operator',
    avatar_url: ''
  }
  userEditRef.value.loadUser(data)
  editModalVisible.value = true
}
</script>
