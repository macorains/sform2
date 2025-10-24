<template>
  <main>
    <HeaderMenu :has-menu="false"/>
    <BContainer>
      <BRow class="mb-4">
        <BCard>
        <h4>ログインできませんでした</h4>
        <p>SFormに登録されていないユーザーです。ログインが必要な場合はグループ管理者にお問い合わせください。</p>
        </BCard>
      </BRow>
      <BRow>
        <BButton variant="primary" @click="onBack">最初の画面に戻る</BButton>
      </BRow>
    </BContainer>
  </main>
</template>

<script setup>
import { useRouter } from 'vue-router'
import HeaderMenu from "@/components/HeaderMenu.vue";

const router = useRouter()

const onBack = (event) => {
  event.preventDefault()
  router.push({path: '/login'})
}
</script>
