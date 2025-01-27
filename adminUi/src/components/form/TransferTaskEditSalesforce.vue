<template>
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
        <TransferTaskEditSalesforceColumn />
      </BCol>
    </BRow>
  </BContainer>
</template>

<script setup>
import {onMounted, ref, inject} from "vue"
import TransferTaskEditSalesforceColumn from "@/components/form/TransferTaskEditSalesforceColumn.vue"

const transferTask = ref({ salesforce: {}})
const object_list = ref([])

const updateFieldList = () => {}
</script>
