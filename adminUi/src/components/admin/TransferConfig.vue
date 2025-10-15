<template>
  <div v-if="isLoading" class="loading-overlay">
    <BSpinner label="Processing..." variant="light" />
  </div>
  <div class="container">
    <BTable striped hover :items="configList" :fields="fields">
      <template #cell(actions)="row">
        <BButton variant="primary" size="sm" @click="edit(row)" class="me-1">
          <i class="bi bi-pencil"></i>
          編集
        </BButton>
        <BButton variant="danger" size="sm" @click="delete(row.config_index)">
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
          転送設定の追加
        </BButton>
      </BCol>
    </BRow>
  </div>
  <BModal v-model="editModalVisible" size="xl" title="転送設定編集" @ok="save" @hidden="close" scrollable>
    <TransferConfigEdit ref="configEditRef"  />
  </BModal>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { BButton, BModal, BSpinner, BTable } from "bootstrap-vue-3";
import TransferConfigEdit from "@/components/admin/TransferConfigEdit.vue";
import { useHttpRequest } from "@/composables/useHttpRequest.js"

const { requestGet, loading } = useHttpRequest()

const fields = ref([
  { key: 'config_index', sortable: true, label: 'No'},
  { key: 'name', sortable: true, label: '名前'},
  { key: 'status', sortable: true, label: 'ステータス'},
  { key: 'type_code', sortable: true, label: '転送タイプ'},
  { key: 'actions', label: '操作' },
])
const configList = ref([])
const editModalVisible = ref(false)
const configEditRef = ref(null)
const isLoading = ref(false)


const edit = (row) => {
  configEditRef.value.loadConfig(row.item.id)
  editModalVisible.value = true
}

const add = () => {
  editModalVisible.value = true
}

const save = () => {
  configEditRef.value.saveConfig()
}

const close = () => {
  configEditRef.value.clearModal()
}

onMounted(() => {
  isLoading.value = true
  requestGet(
      '/transfer/config/list',
      response => {
        configList.value = response.data
        isLoading.value = false
      }
  )
})

</script>

