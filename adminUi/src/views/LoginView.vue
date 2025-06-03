<template>
  <main>
    <HeaderMenu :hasMenu="false"/>
    <BContainer>
      <BRow class="mb-4">
        <BCard>
          <h4>SFormにログイン</h4>
          <p>利用するためには有効なGoogleアカウントを準備した上で、ユーザー情報の事前登録が必要です。</p>
        </BCard>
      </BRow>
      <BRow>
        <BButton variant="primary" @click="onLogin">Googleでログイン</BButton>
      </BRow>
    </BContainer>
  </main>
</template>

<script setup>
import HeaderMenu from "@/components/HeaderMenu.vue";

const onLogin = (event) => {
  event.preventDefault()
  console.log('*** import.meta.env.VITE_APP_API_URL ***')
  console.log(import.meta.env.VITE_APP_API_URL)
  //location.href = import.meta.env.VITE_APP_API_URL + '/oidc'
}
</script>
