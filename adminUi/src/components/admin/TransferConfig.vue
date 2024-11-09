<script setup>
import {getCurrentInstance, onMounted, ref} from "vue";
import {BButton, BModal, BTable} from "bootstrap-vue-3";
import TransferConfigEdit from "@/components/admin/TransferConfigEdit.vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

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
  $http.get('/transfer/config/list')
      .then(response => {
        configList.value = response.data
      })
})

</script>
<template>
  <div class="container">
    <BTable striped hover :items="configList" :fields="fields">
      <template #cell(actions)="row">
        <BButton size="sm" @click="edit(row)" class="mr-1">
          編集
        </BButton>
        <BButton size="sm" @click="delete(row.config_index)">
          削除
        </BButton>
      </template>
    </BTable>
    <BRow>
      <BCol>
        <BButton
            class="mt-4"
            @click="add"
        >
            <span
                class="oi oi-plus"
                title="plus"
                aria-hidden="true"
            />
          転送設定の追加
        </BButton>
      </BCol>
    </BRow>
  </div>
  <BModal v-model="editModalVisible" size="xl" title="転送設定編集" @ok="save" @hidden="close">
    <TransferConfigEdit ref="configEditRef"  />
  </BModal>

  <!--
  <mailTransferConfig
      :is-visible="modalState.mail"
      :transfer-config-id="transferConfigId"
      @changeModalState="changeModalState"
  />
  <salesforceTransferConfig
      :is-visible="modalState.salesforce"
      :transfer-config-id="transferConfigId"
      @changeModalState="changeModalState"
  />
  -->

</template>
