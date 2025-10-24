<template>
  <div v-if="expiry">
    <p>APIトークン有効期限: {{ format(parseISO(expiry), 'yyyy/MM/dd HH:mm:ss')}}</p>
    <BButton size="sm" @click="generate()" class="mr-1">
      APIトークン更新
    </BButton>
  </div>
  <div v-else>
    <p>APIトークンがありません</p>
    <BButton size="sm" @click="generate()" class="mr-1">
      APIトークン作成
    </BButton>
  </div>
  <div v-if="apitoken" class="alert alert-danger mt-4">
    <p>APIトークン: {{ apitoken }}</p>
    <p>※この画面から移動すると再度表示はできません。</p>
  </div>
</template>

<script setup>
import {getCurrentInstance, ref} from "vue"
import {parseISO, format} from "date-fns"
import { useHttpRequest } from "@/composables/useHttpRequest.js"


const instance = getCurrentInstance()
const { requestGet, requestPost, loading } = useHttpRequest()

const expiry = ref(null)
const apitoken = ref(null)
const isTokenRegisterSuccess = ref(null)
const load = () => {
  requestGet('/apitoken/expiry', response => {
    expiry.value = response.data.expiry
  }, error => {
    if (error.code === 'ERR_BAD_REQUEST') {
      expiry.value = null
    }
  })
}

const generate = () => {
  const requestData = {
    expiry_days: 365
  }
  requestPost('/apitoken', requestData, response => {
    isTokenRegisterSuccess.value = true
    apitoken.value = response.data.token
  })
}

defineExpose({
  load,
})
</script>
