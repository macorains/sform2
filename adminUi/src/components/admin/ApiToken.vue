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
import {getCurrentInstance, onMounted, ref} from "vue"
import {parseISO, format} from "date-fns"

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
