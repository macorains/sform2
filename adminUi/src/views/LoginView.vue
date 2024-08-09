<script setup>
import {reactive} from "vue";

function setup() {
  const form = reactive({
    email: '',
    group: '',
    password: ''
  })

  return {
    form
  }
}

</script>
<template>
  <main>
    <div>
      <b-jumbotron header="SForm" lead="Login">
        <b-form @login="onLogin">
          <b-form-group
              id="input-group-1"
              label="ユーザー名（メールアドレス）:"
              label-for="input-1"
              description="We'll never share your email with anyone else."
          >
            <b-form-input
                id="input-1"
                v-model="form.email"
                type="email"
                required
            ></b-form-input>
          </b-form-group>
          <b-form-group
              id="input-group-2"
              label="グループ名:"
              label-for="input-2"
          >
            <b-form-input
                id="input-2"
                v-model="form.group"
                required
            ></b-form-input>
          </b-form-group>
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
          <b-button variant="primary" href="#">Login</b-button>
        </b-form>
      </b-jumbotron>
    </div>
  </main>
</template>
