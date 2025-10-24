<template>
  <h4>転送タスク設定<BButton class="ms-2" size="sm" @click="selectTransferConfig()">追加</BButton></h4>
  <b-table striped hover :items="form.form_transfer_tasks" :fields="fields">
    <template #cell(actions)="row">
      <BButton size="sm" @click="edit(row.item, row.index, $event.target)" class="mr-1">
        編集
      </BButton>
      <BButton class="ms-2" size="sm" @click="delete(row.item)">
        削除
      </BButton>
    </template>
  </b-table>
  <b-modal v-model="editModalMailVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditMail ref="editModalMailRef"/>
  </b-modal>
  <b-modal v-model="editModalSalesforceVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditSalesforce ref="editModalSalesforceRef"/>
  </b-modal>
  <BModal v-model="configSelectModalVisible" @ok="addNewTask" size="lg" title="転送設定の選択">
    <TransferTaskEditSelectConfig ref="configSelectModalRef" />
  </BModal>
</template>
<script setup>
import {onMounted, ref, inject, getCurrentInstance, reactive} from "vue";
import { BButton, BModal } from 'bootstrap-vue-3'
import TransferTaskEditMail from "@/components/form/TransferTaskEditMail.vue";
import TransferTaskEditSalesforce from "@/components/form/TransferTaskEditSalesforce.vue";
import TransferTaskEditSelectConfig from "@/components/form/TransferTaskEditSelectConfig.vue";
defineProps(['formId'])

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const fields = ref([
  { key: 'task_index', sortable: true, label: '順序'},
  { key: 'name', sortable: true, label: '転送タスク名'},
  { key: 'transfer_config_name', sortable: true, label: '転送設定名'},
  { key: 'actions', label: '操作' },
])

const salesforceTransferTaskDefault = (taskCount, configName) => {
  return {
    id: null,
    name: configName + '-' + (taskCount + 1),
    form_id: form.id,
    transfer_config_id: null,
    transfer_config_name: null,
    task_index: taskCount + 1,
    form_transfer_task_conditions: [],
    salesforce: {
      form_transfer_task_id: null,
      object_name: null,
      fields: []
    },
    mail: null,
  }
}

const mailTransferTaskDefault = (taskCount, configName) => {
  return {
    id: null,
    name: configName + '-' + (taskCount + 1),
    form_id: form.id,
    transfer_config_id: null,
    transfer_config_name: null,
    task_index: taskCount + 1,
    form_transfer_task_conditions: [],
    salesforce: null,
    mail: {
      bcc_address_id: null,
      body: '',
      cc_address: '',
      form_transfer_task_id: null,
      from_address_id: null,
      id: null,
      replyto_address_id: null,
      subject: '',
      to_address: ''
    }
  }
}
const form = reactive({})
const editModalMailVisible = ref(false)
const editModalSalesforceVisible = ref(false)
const configSelectModalVisible = ref(false)
const configSelectModalRef = ref(null)
const editModalSalesforceRef = ref(null)
const editModalMailRef = ref(null)

onMounted(() => {

})

const edit = (item, index, target) => {
  if (item.mail) {
    editModalMailRef.value.load(item)
    editModalMailVisible.value = true
  }
  if (item.salesforce) {
    editModalSalesforceRef.value.load(item, form.form_cols)
    editModalSalesforceVisible.value = true
  }
}

const load = (data) => {
  form.id = data.id
  form.form_transfer_tasks = data.form_transfer_tasks
  form.form_cols = data.form_cols
}
const selectTransferConfig = () => {
  configSelectModalVisible.value = true
}

const addNewTask = () => {
  configSelectModalVisible.value = false
  const config = configSelectModalRef.value.getConfig()
  const taskCount = form.form_transfer_tasks.length
  let newItem = {}

  if (config.type_code === 'salesforce') {
    newItem = salesforceTransferTaskDefault(taskCount, config.name)
  } else if (config.type_code === 'mail') {
    newItem = mailTransferTaskDefault(taskCount, config.name)
  }

  form.form_transfer_tasks.push(newItem)
  edit(newItem)
}

defineExpose({
  load,
})

</script>
