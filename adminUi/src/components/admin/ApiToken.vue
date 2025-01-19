<template>
  <div v-if="expiry">
    <p>APIトークン有効期限: {{ expiry }}</p>
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
</template>

<script setup>
import {getCurrentInstance, onMounted, ref} from "vue"

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const expiry = ref(null)
const apitoken = ref(null)
const isTokenRegisterSuccess = ref(null)
const load = () => {
  $http.get('/apitoken/expiry')
      .then(response => {
        expiry.value = response.data.expiry
      })
      .catch(error => {
        if (error.code === 'ERR_BAD_REQUEST') {
          expiry.value = null
        }
      })
}

const generate = () => {
  $http.get('/apitoken/generate')
      .then(response => {
        apitoken.value = response.data.token
        const requestData = {
          token: apitoken.value,
          expiry_days: 365
        }
        $http.post('/apitoken', requestData)
            .then(saveResponse => {
              isTokenRegisterSuccess.value = true
            })
      })
}

defineExpose({
  load,
})
</script>
