<script setup>
import { RouterLink } from 'vue-router'
import HeaderMenu from '../components/HeaderMenu.vue'

const features = [
  {
    icon: 'bi-ui-checks',
    title: 'フォーム作成',
    text: '管理画面から入力項目や入力チェック、確認画面を設定して、Webフォームを作成できます。'
  },
  {
    icon: 'bi-plug',
    title: 'サイトに埋め込み',
    text: 'WordPressプラグインを導入すれば、ショートコードひとつで外部サイトにフォームを表示できます。'
  },
  {
    icon: 'bi-arrow-left-right',
    title: 'データ連携',
    text: '送信されたデータをSalesforceへ転送したり、自動返信メールを送ったりできます。'
  }
]
</script>

<template>
  <main>
    <HeaderMenu/>
    <BContainer>
      <section class="hero text-center py-5 mb-4">
        <h1 class="display-5 fw-bold">SForm</h1>
        <p class="lead text-secondary mb-4">Webフォームの作成・ホスティングサービス</p>
        <RouterLink to="/login" class="btn btn-primary btn-lg px-5">ログイン</RouterLink>
      </section>

      <BRow class="g-4 mb-5">
        <BCol v-for="feature in features" :key="feature.title" md="4">
          <BCard class="h-100 text-center">
            <i :class="['bi', feature.icon, 'feature-icon', 'text-primary']" aria-hidden="true"></i>
            <h5 class="mt-3">{{ feature.title }}</h5>
            <p class="text-secondary mb-0">{{ feature.text }}</p>
          </BCard>
        </BCol>
      </BRow>
    </BContainer>
  </main>
</template>

<style scoped>
.hero {
  border-bottom: 1px solid var(--bs-border-color);
}

.feature-icon {
  font-size: 2.5rem;
}
</style>
