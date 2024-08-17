<script setup>
import { getCurrentInstance, ref } from "vue"
import { useRouter } from 'vue-router'

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()

const form = ref({
  email: '',
  group: '',
  password: ''
})

const onLogin = (event) => {
  event.preventDefault()
  const params = new URLSearchParams()
  params.append('username', form.value.email)
  params.append('group', form.value.group)
  params.append('password', form.value.password)
  $http.post('/signIn', params)
      .then(response => {
        router.push({ name: 'verification', params: { authkey: response.data.authkey } })
            .catch(err => { console.log(err) })
      })
}

</script>
<template>
  <main>
    <div>
      <b-jumbotron header="SForm" lead="Login">
        <b-form @submit="onLogin">
          <b-container>
            <b-row>
              <b-form-group
                  id="input-group-1"
                  label="ユーザー名（メールアドレス）:"
                  label-for="input-1"
                  description="someone@msform.appの形式で入力してください"
              >
                <b-form-input
                    id="input-1"
                    v-model="form.email"
                    type="email"
                    required
                ></b-form-input>
              </b-form-group>
            </b-row>
            <b-row>
              <b-form-group
                  id="input-group-2"
                  label="グループ名:"
                  label-for="input-2"
                  description="組織ごとに指定されたグループ名を入力してください"
              >
                <b-form-input
                    id="input-2"
                    v-model="form.group"
                    required
                ></b-form-input>
              </b-form-group>
            </b-row>
            <b-row>
              <b-form-group
                  id="input-group-3"
                  label="パスワード:"
                  label-for="input-3"
              >
                <b-form-input
                    id="input-3"
                    v-model="form.password"
                    type="password"
                    required
                ></b-form-input>
              </b-form-group>
            </b-row>
            <hr />
            <b-row align-h="end">
              <b-button type="submit" variant="primary">Login</b-button>
            </b-row>
          </b-container>
        </b-form>
      </b-jumbotron>
    </div>
  </main>
</template>
