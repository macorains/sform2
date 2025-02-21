<template>
  <div v-if="loading" class="loading-overlay">
    <BSpinner label="Processing..." variant="light" />
  </div>
  <BContainer class="text-left">
    <BRow class="mb-3">
      <BCol cols="3">
        転送タスク名
      </BCol>
      <BCol>
        <BFormInput
            id="transferTask.name"
            v-model="transferTask.name"
            type="text"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="3">
        転送先オブジェクト
      </BCol>
      <BCol>
        <BFormSelect
            v-model="transferTask.salesforce.object_name"
            :options="object_list"
            class="mb-3"
            @change="updateFieldList"
        />
      </BCol>
    </BRow>
    <BRow class="mb-3">
      <BCol cols="3">
        項目割付
      </BCol>
      <BCol id="attach_list">
        <TransferTaskEditSalesforceColumn ref="salesforceColumnRef"/>
      </BCol>
    </BRow>
  </BContainer>
</template>

<script setup>
import {ref, reactive, toRaw} from "vue"
import TransferTaskEditSalesforceColumn from "@/components/form/TransferTaskEditSalesforceColumn.vue"
import { useHttpRequest } from "@/composables/useHttpRequest.js"
import { BSpinner } from "bootstrap-vue-3";

const { requestGet, loading } = useHttpRequest()

const transferTask = reactive({ salesforce: {}})
const formCols = reactive([])
const object_list = ref([])
const salesforceColumnRef = ref(null)

const load = (data, form_cols) => {
  formCols.length = 0 // 配列を空にする
  transferTask.id = data.id
  transferTask.transfer_config_id = data.transfer_config_id
  transferTask.name = data.name
  transferTask.salesforce = data.salesforce
  form_cols.forEach(fc => {
    formCols.push(fc)
  })
  salesforceColumnRef.value.load(data.transfer_config_id, transferTask.salesforce, formCols)

  const url = `/transfer/salesforce/object/${data.transfer_config_id}`
  requestGet(url, response => {
    response.data.forEach(dt => {
      object_list.value.push({value: dt.name, text: dt.label})
    })
  })
}
const updateFieldList = () => {
  salesforceColumnRef.value.onObjectChange(transferTask.transfer_config_id, transferTask.salesforce.object_name, formCols)
}

defineExpose({
  load
})
</script>
