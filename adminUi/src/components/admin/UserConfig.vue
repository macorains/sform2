<template>
  <div v-if="isLoading" class="loading-overlay">
    <BSpinner label="Processing..." variant="light" />
  </div>
  <div class="container">
    <BTable striped hover :items="userList" :fields="fields" @row-clicked="userEdit">
    </BTable>
  </div>
  <BModal v-model="editModalVisible" size="lg" title="ユーザー編集" @ok="save" @hidden="close" scrollable>
    <UserConfigEdit ref="userEditRef" @load="reload" />
  </BModal>
</template>
<script setup>

import {BModal, BSpinner, BTable} from "bootstrap-vue-3";
import {getCurrentInstance, onMounted, ref} from "vue";
import UserConfigEdit from "@/components/admin/UserConfigEdit.vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const editModalVisible = ref(false)
const userEditRef = ref(null)

const fields = ref([
  { key: 'full_name', sortable: true, label: '名前'},
  { key: 'username', sortable: true, label: 'ユーザー名'},
  { key: 'role', sortable: true, label: '役割'},
])

const userList = ref([])
const isLoading = ref(false)

onMounted(() => {
  load()
})

const load = () => {
  isLoading.value = true
  $http.get('/user')
      .then(response => {
        userList.value = response.data.dataset.map(d => convert(d))
        isLoading.value = false
      })
}
const convert = (source) => {
  source.full_name = source.full_name || (source.last_name + ' ' + source.first_name)
  return source
}

const userEdit = (data) => {
  console.log(data)
  userEditRef.value.loadUser(data)
  editModalVisible.value = true
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
