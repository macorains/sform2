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
  </div>
  <BModal v-model="editModalVisible" size="lg" title="ユーザー編集" @ok="save" @hidden="close" scrollable>
    <UserConfigEdit ref="userEditRef" @load="reload" />
  </BModal>
</template>
<script setup>

import {BButton, BModal, BSpinner, BTable} from "bootstrap-vue-3";
import {getCurrentInstance, inject, onMounted, ref} from "vue";
import { useHttpRequest } from "@/composables/useHttpRequest.js"
import UserConfigEdit from "@/components/admin/UserConfigEdit.vue";


const instance = getCurrentInstance()
const { requestGet, requestDelete, loading } = useHttpRequest()

const editModalVisible = ref(false)
const userEditRef = ref(null)

const fields = ref([
  { key: 'full_name', sortable: true, label: '名前'},
  { key: 'username', sortable: true, label: 'ユーザー名'},
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
</script>
