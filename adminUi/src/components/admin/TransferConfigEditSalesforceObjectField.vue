<template>
  <BTable striped hover :items="sfObjectFieldList.fields" :fields="sfObjectFieldFields" select-mode="single" v-if="hasList && sfObjectFieldList.isExpand">
    <template #cell(valid)="data">
      <BFormCheckbox
          v-model="data.item.valid"
          name="valid"
      ></BFormCheckbox>
    </template>
  </BTable>
</template>
<script setup>
import {BTable} from "bootstrap-vue-3"
import {getCurrentInstance, ref, reactive, onMounted, computed, watch, defineEmits} from "vue";

const props = defineProps({
  transferConfigId: Number,
  data: Object
})
watch (
    () => props.data,
    (newVal, oldVal) => {
      sfObjectFieldList.transferConfigId = props.transferConfigId
      sfObjectFieldList.objectName = newVal.name
      sfObjectFieldList.isExpand = newVal.isExpand
      sfObjectFieldList.fields = newVal.fields
    },
    { deep: true }
)

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const emit = defineEmits(['updateField']);

const sfObjectFieldList = reactive({
  fields: []
})

const sfObjectFieldFields = ref([
  { key: 'valid', sortable: true, label: '有効'},
  { key: 'label', sortable: true, label: 'フィールド名'},
])

onMounted(() => {
  sfObjectFieldList.transferConfigId = props.transferConfigId
})

const hasList = computed(() => sfObjectFieldList.fields?.length > 0)

const importObjectField = (objectName) => {
  $http.get('/transfer/salesforce/field/' + sfObjectFieldList.transferConfigId + '/' + objectName)
      .then(response => {
        emit('updateField', objectName, response.data)
      })
}

defineExpose({
  importObjectField,
})
</script>
