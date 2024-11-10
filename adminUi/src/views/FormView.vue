<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import HeaderMenu from '../components/HeaderMenu.vue'
import {useRouter} from "vue-router";

// Composition APIを使うので、export default {} は不要
const data = ref([])
const fields = ref([
    { key: 'hashed_id', sortable: true, label: 'ID'},
    { key: 'name', sortable: true, label: '名前'},
    { key: 'title', sortable: true, label: 'フォームタイトル'},
    { key: 'status_name', sortable: true, label: 'ステータス'},
])
const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http
const router = useRouter()

// ライフサイクルフック
onMounted(() => {
  $http.get('/form/list')
      .then(response => {
        // this.$data.loading = false
        data.value = convert(response.data.forms)
      })
})

function convert(data) {
  for(const d of data) {
    d.status_name = d.status ? '有効' : '無効'
  }
  return data
}

function detail(data) {
  console.log(data)
  router.push({ name: 'form_detail', params: { form_id: data.hashed_id } })
}

</script>

<template>
  <main>
    <HeaderMenu />
    <div class="container">
      <h1 class="mt-5 mb-5">
        フォーム
      </h1>
      <b-table striped hover :items="data" :fields="fields" @row-clicked="detail"></b-table>
    </div>
  </main>
</template>
