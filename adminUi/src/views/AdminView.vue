<template>
    <HeaderMenu />
      <div class="container">
        <h1 class="mt-5 mb-5">
          管理
        </h1>
        <BCard no-body>
          <BTabs
              pills
              card
              class="custom-tabs"
              :aria-selected="null"
          >
            <BTab active>
              <template #title>
              <span
                  class="oi oi-transfer"
                  title="transfer"
                  aria-hidden="true"
              />
                転送
              </template>
              <TransferConfig />
            </BTab>
            <BTab>
              <template #title>
              <span
                  class="oi oi-person"
                  title="person"
                  aria-hidden="true"
              />
                ユーザー
              </template>
              <UserConfig />
            </BTab>
            <BTab @click="loadToken">
              <template #title>
              <span
                  class="oi oi-person"
                  title="person"
                  aria-hidden="true"
              />
                APIトークン
              </template>
              <ApiToken ref="apiTokenRef"/>
            </BTab>
          </BTabs>
        </BCard>
      </div>
</template>




<style>
.custom-tabs .nav-link.active {
  background-color: #007bff !important; /* 任意の背景色 */
  color: white; /* 任意の文字色 */
}
</style>

<script setup>
import {ref} from "vue"
import HeaderMenu from '../components/HeaderMenu.vue'
import TransferConfig from '../components/admin/TransferConfig.vue'
import ApiToken from '../components/admin/ApiToken.vue'
import UserConfig from '../components/admin/UserConfig.vue'

const apiTokenRef = ref(null)
const loadToken = () => {
  apiTokenRef.value.load()
}
</script>
