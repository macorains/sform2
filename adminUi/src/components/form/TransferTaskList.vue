<template>
  <h4>転送タスク設定<BButton class="ms-2" size="sm" @click="selectTransferConfig()">追加</BButton></h4>
  <table class="table table-striped table-hover">
    <thead>
      <tr>
        <th>順序</th>
        <th>転送タスク名</th>
        <th>転送設定名</th>
        <th>操作</th>
      </tr>
    </thead>
    <draggable :list="form.form_transfer_tasks" tag="tbody" item-key="task_index" @end="onDragEnd">
      <template #item="{ element }">
        <tr>
          <td>{{ element.task_index }}</td>
          <td>{{ element.name }}</td>
          <td>{{ element.transfer_config_name }}</td>
          <td>
            <BButton size="sm" @click="edit(element)" class="mr-1">編集</BButton>
            <BButton class="ms-2" size="sm" @click="deleteTask(element)">削除</BButton>
          </td>
        </tr>
      </template>
    </draggable>
  </table>
  <b-modal v-model="editModalMailVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditMail ref="editModalMailRef"/>
  </b-modal>
  <b-modal v-model="editModalSalesforceVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditSalesforce ref="editModalSalesforceRef"/>
  </b-modal>
  <BModal v-model="configSelectModalVisible" @ok="addNewTask" @show="onConfigModalShow" :ok-disabled="configOkDisabled" size="lg" title="転送設定の選択">
    <TransferTaskEditSelectConfig ref="configSelectModalRef" @selection-change="v => configOkDisabled = (v === null)" />
  </BModal>
</template>
<script setup>
import {onMounted, ref, inject, getCurrentInstance, reactive} from "vue";
import { BButton, BModal } from 'bootstrap-vue-3'
import draggable from 'vuedraggable'
import TransferTaskEditMail from "@/components/form/TransferTaskEditMail.vue";
import TransferTaskEditSalesforce from "@/components/form/TransferTaskEditSalesforce.vue";
import TransferTaskEditSelectConfig from "@/components/form/TransferTaskEditSelectConfig.vue";
defineProps(['formId'])
const emit = defineEmits(['update-transfer-tasks'])

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const form = reactive({})
const editModalMailVisible = ref(false)
const editModalSalesforceVisible = ref(false)
const configSelectModalVisible = ref(false)
const configSelectModalRef = ref(null)
const editModalSalesforceRef = ref(null)
const editModalMailRef = ref(null)
const configOkDisabled = ref(true)

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

const mailTransferTaskDefault = (taskCount, configName, configId) => {
  return {
    id: null,
    name: configName + '-' + (taskCount + 1),
    form_id: form.id,
    transfer_config_id: configId,
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

onMounted(() => {

})

const onDragEnd = () => {
  form.form_transfer_tasks.forEach((task, i) => {
    task.task_index = i + 1
  })
  emit('update-transfer-tasks', form.form_transfer_tasks)
}

const edit = (item) => {
  if (item.mail) {
    editModalMailRef.value.load(item, form.form_cols)
    editModalMailVisible.value = true
  }
  if (item.salesforce) {
    editModalSalesforceRef.value.load(item, form.form_cols)
    editModalSalesforceVisible.value = true
  }
}

const deleteTask = (item) => {
  const index = form.form_transfer_tasks.findIndex(t => t.task_index === item.task_index)
  if (index !== -1) {
    form.form_transfer_tasks.splice(index, 1)
    form.form_transfer_tasks.forEach((task, i) => {
      task.task_index = i + 1
    })
    emit('update-transfer-tasks', form.form_transfer_tasks)
  }
}

const load = (data) => {
  form.id = data.id
  form.form_transfer_tasks = data.form_transfer_tasks
  form.form_cols = data.form_cols
  emit('update-transfer-tasks', form.form_transfer_tasks)
}
const selectTransferConfig = () => {
  configSelectModalVisible.value = true
}

const onConfigModalShow = () => {
  configOkDisabled.value = true
  configSelectModalRef.value?.reset()
}

const addNewTask = () => {
  configSelectModalVisible.value = false
  const config = configSelectModalRef.value.getConfig()
  const taskCount = form.form_transfer_tasks.length
  let newItem = {}

  if (config.type_code === 'salesforce') {
    newItem = salesforceTransferTaskDefault(taskCount, config.name)
  } else if (config.type_code === 'mail') {
    newItem = mailTransferTaskDefault(taskCount, config.name, config.id)
  }

  form.form_transfer_tasks.push(newItem)
  emit('update-transfer-tasks', form.form_transfer_tasks)
  edit(newItem)
}

defineExpose({
  load,
})

</script>
