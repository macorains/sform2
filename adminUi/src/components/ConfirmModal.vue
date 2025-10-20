<template>
  <b-modal v-model="show" title="確認" hide-footer modal-class="custom-confirm-modal">
    <p>{{ confirmMessage }}</p>
    <b-button class="float-end" variant="primary" @click="modalOk"><i class="bi bi-check"></i>OK</b-button>
    <b-button class="float-end me-3"  @click="modalCancel"><i class="bi bi-x"></i>キャンセル</b-button>
  </b-modal>
</template>
<style>
.custom-confirm-modal {
  z-index: 2000 !important; /* 他のモーダルより上にする */
}
</style>
<script setup>
import { ref } from "vue";

const show = ref(false);
const confirmMessage = ref("");
let resolvePromise;

const openModal = (message) => {
  confirmMessage.value = message;
  show.value = true;
  return new Promise((resolve) => {
    resolvePromise = resolve;
  });
};

const modalCancel = () => {
  show.value = false;
  if (resolvePromise) resolvePromise(false)
}

const modalOk = () => {
  show.value = false;
  if (resolvePromise) resolvePromise(true)
}

defineExpose({ openModal }); // 親コンポーネントから openModal を呼び出せるようにする
</script>
