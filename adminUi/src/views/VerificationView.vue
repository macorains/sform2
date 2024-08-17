<script setup>
import {getCurrentInstance, ref} from "vue";
import {useRoute, useRouter} from "vue-router";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()
const route = useRoute()

const form = ref({
  verification_code: ''
})

const onVerification = (event) => {
  event.preventDefault()
  const reqdata = {
    authkey: route.params.authkey,
    verification_code: form.value.verification_code
  }
  $http.post('/verification', reqdata)
      .then(response => {
        console.log(response)
        // 認証トークンを受け取ってLocalStorageに格納、axiosのデフォルトヘッダにセットする
        const token = response.headers['x-auth-token']
        localStorage.setItem('sformToken', token)
        $http.defaults.headers.common['X-Auth-Token'] = token
        router.push({path: '/form'})
      }).catch(err => { console.log(err) })
}

</script>
<template>
  <b-jumbotron header="SForm" lead="認証コード確認">
    <b-form @submit="onVerification">
      <b-container>
        <b-row>
          <b-form-group
              id="input-group-1"
              label="認証コード:"
              label-for="input-1"
              description="メールで送信された認証コードを入力してください。"
          >
            <b-form-input
                id="input-1"
                v-model="form.verification_code"
                required
            ></b-form-input>
          </b-form-group>
        </b-row>
        <hr />
        <b-row align-h="end">
          <b-button type="submit" variant="primary">送信</b-button>
        </b-row>
      </b-container>
    </b-form>
  </b-jumbotron>
</template>
