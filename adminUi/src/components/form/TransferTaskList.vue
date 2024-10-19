<script setup>
import {onMounted, ref, inject} from "vue";
import { BButton } from 'bootstrap-vue-3'
import TransferTaskEditMail from "@/components/form/TransferTaskEditMail.vue";
import TransferTaskEditSalesforce from "@/components/form/TransferTaskEditSalesforce.vue";
defineProps(['formId'])

const fields = ref([
  { key: 'index', sortable: true, label: '順序'},
  { key: 'name', sortable: true, label: '転送タスク名'},
  { key: 'config_name', sortable: true, label: '転送設定名'},
  { key: 'actions', label: '操作' },
])
const form = inject('form')
const editModalMailVisible = ref(false)
const editModalSalesforceVisible = ref(false)

onMounted(() => {
  //
})
const edit = (item, index, target) => {
  console.log(item)
  if (item.mail) {
    editModalMailVisible.value = true
  }
  if (item.salesforce) {
    editModalSalesforceVisible.value = true
  }
}

</script>
<template>
  <h4>転送タスク設定</h4>
  <b-table striped hover :items="form.form_transfer_tasks" :fields="fields">
    <template #cell(actions)="row">
      <BButton size="sm" @click="edit(row.item, row.index, $event.target)" class="mr-1">
        編集
      </BButton>
      <BButton size="sm" @click="delete(row.item)">
        削除
      </BButton>
    </template>
  </b-table>
  <b-modal v-model="editModalMailVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditMail/>
  </b-modal>
  <b-modal v-model="editModalSalesforceVisible" size="xl" title="転送タスク編集">
    <TransferTaskEditSalesforce/>
  </b-modal>
</template>
