<template>
  <BFormSelect
      v-model="selectedTransferConfig"
      :options="transferConfigIdList"
      class="mb-3"
  />
</template>
<script setup>
import {getCurrentInstance, onMounted, ref, watch} from "vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const emit = defineEmits(['selectionChange'])

const transferConfigList = ref([])
const transferConfigIdList = ref([{ text: '-- 選択してください --', value: null }])
const selectedTransferConfig = ref(null)

watch(selectedTransferConfig, (val) => {
  emit('selectionChange', val)
})

onMounted(() => {
  $http.get('/transfer/config/list')
      .then(response => {
        response.data.forEach(res => {
          transferConfigList.value.push(res)
          transferConfigIdList.value.push({ text: res.name + '(' + res.type_code + ')', value: res.id})
        })
      })
})

const getConfig = () => {
  return transferConfigList.value.filter(config => config.id === selectedTransferConfig.value)[0]
}

const reset = () => {
  selectedTransferConfig.value = null
}

defineExpose({
  getConfig,
  reset,
})
</script>
