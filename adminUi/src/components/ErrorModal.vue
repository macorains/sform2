<template>
  <b-modal v-model="show" title="エラー" hide-footer modal-class="custom-error-modal">
    <p>{{ errorMessage }}</p>
    <b-button class="float-end" variant="primary" @click="closeModal">閉じる</b-button>
  </b-modal>
</template>
<style>
.custom-error-modal {
z-index: 2000 !important; /* 他のモーダルより上にする */
}
</style>
<script setup>
import { ref } from "vue";

const show = ref(false);
const errorMessage = ref("");

const openModal = (message) => {
  errorMessage.value = message;
  show.value = true;
};

const closeModal = () => {
  show.value = false;
}

defineExpose({ openModal }); // 親コンポーネントから openModal を呼び出せるようにする
</script>
