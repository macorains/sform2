<template>
  <div class="table-container">
    <BTable
        sticky-header="60vh"
        :items="fieldList"
        :fields="fields"
        head-variant="light"
    >
      <template #cell(column_id)="data">
        <BFormSelect
            v-model="data.item.column_id"
            :options="columnList"
            size="sm"
        />
      </template>
    </BTable>
  </div>
</template>
<style>

.table-container {
  max-height: 60vh;
  overflow-y: auto;
  border: 1px solid #ddd;
}

.table > thead {
  position: sticky !important;
  top: 0 !important;
  z-index: 1020 !important;
  background-color: white !important; /* 必須 */
}
</style>
<script setup>
import {onMounted, ref, inject, toRaw} from "vue"
import {useHttpRequest} from "@/composables/useHttpRequest.js";
import { BTable, BFormSelect } from 'bootstrap-vue-3'

const { requestGet, loading } = useHttpRequest()

const fields = ref([
  { key: 'label', label: 'Salesforce項目' },
  { key: 'field_type', label: '型' },
  { key: 'column_id', label: 'フォーム項目' },
])
const stickyHeader = ref(true)

const columnList = ref([])
const fieldList = ref([])

const load = (transferConfigId, salesforce, formCols) => {
  columnList.value = [{ text: '(なし)', value: null }]
  toRaw(formCols).forEach(fc => {
    columnList.value.push({ text: fc.name, value: fc.id })
  })
  getSalesforceFieldList(transferConfigId, salesforce)
}

const onObjectChange = (transferConfigId, objectName) => {
  getSalesforceFieldList(transferConfigId, objectName)
}

const getSalesforceFieldList = (transferConfigId, salesforce) => {
  const url = `/transfer/salesforce/field/${transferConfigId}/${salesforce.object_name}`
  requestGet(url, response => {
    fieldList.value = []
    response.data.forEach(dt => {
      const column_id = salesforce.fields.filter(field => field.field_name === dt.name).map(field => field.form_column_id)[0]
      dt.column_id = column_id ?? null
      fieldList.value.push(dt)
    })
  })
}

defineExpose({
  load,
  onObjectChange
})
</script>
