<template>
  <main>
    <HeaderMenu hide-menu/>
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
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const route = useRoute()
const auth = useAuthStore()

const onLogin = (event) => {
  event.preventDefault()
  // OIDC 後は固定URLに戻るので、元の遷移先を保存しておく（ルートガードで復帰）
  auth.saveRedirect(typeof route.query.redirect === 'string' ? route.query.redirect : null)
  location.href = import.meta.env.VITE_APP_API_URL + '/oidc'
}
</script>
