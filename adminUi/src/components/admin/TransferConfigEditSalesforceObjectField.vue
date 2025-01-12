<template>
  <BTable striped hover :items="sfObjectFieldList.fields" :fields="sfObjectFieldFields" v-if="hasList && sfObjectFieldList.isExpand">
    <template #cell(active)="data">
      <BFormCheckbox
          v-model="data.item.active"
          name="active"
          @change="updateObjectField(data.item.active, data.item.name)"
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

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const emit = defineEmits(['updateField']);

const sfObjectFieldList = reactive({
  fields: []
})

const sfObjectFieldFields = ref([
  { key: 'active', sortable: true, label: '有効'},
  { key: 'label', sortable: true, label: 'フィールド名'},
])

watch (
    () => props.data,
    (newVal, oldVal) => {
      sfObjectFieldList.transferConfigId = props.transferConfigId
      sfObjectFieldList.objectName = newVal.name
      sfObjectFieldList.isExpand = newVal.isExpand
      sfObjectFieldList.fields = newVal.fields
    },
    { deep: true, immediate: true }
)

onMounted(() => {
  sfObjectFieldList.transferConfigId = props.transferConfigId
})

const hasList = computed(() => sfObjectFieldList.fields?.length > 0)

const importObjectField = (objectName) => {
  $http.get('/transfer/salesforce/field/' + sfObjectFieldList.transferConfigId + '/' + objectName)
      .then(response => {
        const data = response.data.map(d => convert(d))
        emit('updateField', objectName, data)
      })
}

const updateObjectField = (val, name) => {
  sfObjectFieldList.fields.filter(f => f.name === name)[0].active = val
  emit('updateField', sfObjectFieldList.objectName, sfObjectFieldList.fields)
}

const convert = (src) => {
  src.active = false
  return src
}

defineExpose({
  importObjectField,
})
</script>
