<template>
  <BFormSelect
      v-model="selectedTransferConfig"
      :options="transferConfigIdList"
      class="mb-3"
  />
</template>
<script setup>
import {getCurrentInstance, onMounted, ref} from "vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const transferConfigList = ref([])
const transferConfigIdList = ref([])
const selectedTransferConfig = ref(null)

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

defineExpose({
  getConfig,
})
</script>
