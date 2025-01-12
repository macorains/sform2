<template>
  <BRow class="mb-1 mt-4">
    <BCol>
      <b>転送先オブジェクト選択</b>
      <BButton class="button btn-sm ms-2" @click="importObject"><i class="bi bi-cloud-arrow-down me-1"></i>インポート</BButton>
    </BCol>
  </BRow>
  <BRow>
    <BCol>
      <BTable striped hover :items="sfObjectList.objectList" :fields="sfObjectFields">
        <template #cell(active)="data">
          <BFormCheckbox
              v-model="data.item.active"
              name="active"
          ></BFormCheckbox>
        </template>
        <template #cell(label)="data">
          <BRow>
            <BCol>
              {{ data.item.label }}
            </BCol>
            <BCol>
              <BButton class="btn btn-sm me-1" @click="onExpand(data.item)" v-if="!data.item.isExpand && hasObjectField(data.item)"><i class="bi bi-arrows-expand"></i>フィールド表示</BButton>
              <BButton class="btn btn-sm me-1" @click="onCollapse(data.item)" v-if="data.item.isExpand && hasObjectField(data.item)"><i class="bi bi-arrows-collapse"></i>フィールド非表示</BButton>
              <BButton class="btn btn-sm me-1" @click="callObjectFieldImport(data.item)"><i class="bi bi-cloud-arrow-down"></i>インポート</BButton>
            </BCol>
          </BRow>
          <BRow>
            <BCol>
              <TransferConfigEditSalesforceObjectField :ref="setRef(data.item)" :transferConfigId="sfObjectList.transferConfigId" :data="data.item" @updateField="updateField"/>
            </BCol>
          </BRow>
        </template>
      </BTable>
    </BCol>
  </BRow>
</template>
<script setup>
import TransferConfigEditSalesforceObjectField from "@/components/admin/TransferConfigEditSalesforceObjectField.vue"
import {BTable} from "bootstrap-vue-3"
import {getCurrentInstance, ref, reactive, computed} from "vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http


const sfObjectFieldRef = ref([])
const sfObjectList = reactive({})

const sfObjectFields = ref([
  { key: 'active', sortable: true, label: '有効'},
  { key: 'label', sortable: true, label: 'オブジェクト名'},
])

const loadData = (data, transferConfigId) => {
  sfObjectList.transferConfigId = transferConfigId
  sfObjectList.objectList = data
}

const getData = () => {
  return sfObjectList.objectList
}

const importObject = () => {
  $http.get('/transfer/salesforce/object/' + sfObjectList.transferConfigId)
      .then(response => {
        sfObjectList.objectList = response.data.map(d => convert(d))
      })
}

const convert = (src) => {
  src.active = false
  src.fields = []
  return src
}

const setRef = (item) => (el) => {
  sfObjectFieldRef.value[item.name] = el
}

const callObjectFieldImport = (item) => {
  const target = sfObjectFieldRef.value[item.name]
  if (target) {
    target.importObjectField(item.name)
  }
}

const hasObjectField = computed(() => {
  return (item) => sfObjectList.objectList.filter(obj => obj.name === item.name)[0].fields?.length > 0
})

const onExpand = (item) => {
  sfObjectList.objectList.filter(obj => obj.name === item.name)[0].isExpand = true
}

const onCollapse = (item) => {
  sfObjectList.objectList.filter(obj => obj.name === item.name)[0].isExpand = false
}

const updateField = (objectName, list) => {
  const target = sfObjectList.objectList.filter(obj => obj.name === objectName)[0]
  target.fields = list
  target.isExpand = true
}

defineExpose({
  loadData,
  getData,
})

</script>
