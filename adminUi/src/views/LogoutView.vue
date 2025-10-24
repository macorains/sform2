<template>
  <main>
    <HeaderMenu :has-menu="false"/>
    <BContainer>
      <BRow class="mb-4">
        <BCard>
          <h4>ログアウトしますか?</h4>
        </BCard>
      </BRow>
      <BRow align-h="end">
        <BCol class="d-flex justify-content-end" cols="5">
          <BButton class="me-2" variant="primary" @click="onLogout">はい</BButton>
          <BButton @click="onCancel">いいえ</BButton>
        </BCol>
      </BRow>
    </BContainer>
  </main>
</template>

<script setup>
import HeaderMenu from "@/components/HeaderMenu.vue";
import { useRouter } from "vue-router";

const router = useRouter()

const onLogout = (event) => {
  event.preventDefault()
  localStorage.removeItem('sformToken')
  location.href = import.meta.env.VITE_APP_API_URL + '/logout'
}
const onCancel = (event) => {
  event.preventDefault()
  router.push({path: '/form'})
}
</script>
