<template>
  <nav class="navbar navbar-expand-lg bg-body-tertiary mb-4">
    <div class="container-fluid">
      <RouterLink class="navbar-brand" :to="showMenu ? '/form' : '/'">SForm</RouterLink>
      <template v-if="showMenu">
        <button class="navbar-toggler" type="button" aria-controls="navbarNav" :aria-expanded="expanded" aria-label="Toggle navigation" @click="expanded = !expanded">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" :class="{ show: expanded }" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <RouterLink class="nav-link" active-class="active" to="/form">フォーム</RouterLink>
            </li>
            <li class="nav-item" v-if="auth.isAdmin">
              <RouterLink class="nav-link" active-class="active" to="/admin">管理</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" active-class="active" to="/logout">ログアウト</RouterLink>
            </li>
          </ul>
        </div>
      </template>
    </div>
  </nav>
</template>
<script setup>
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// ログイン状態・管理者かどうかはストアから取得する
// hideMenu: ログイン/ログアウト画面などでメニューを出さない場合に指定
const props = defineProps({
  hideMenu: Boolean
})

const auth = useAuthStore()
const expanded = ref(false)
const showMenu = computed(() => auth.isAuthenticated && !props.hideMenu)
</script>
