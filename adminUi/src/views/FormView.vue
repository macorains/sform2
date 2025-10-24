<script setup>
import { ref, onMounted } from 'vue'
import HeaderMenu from '../components/HeaderMenu.vue'
import { useRouter } from "vue-router";
import { useHttpRequest } from "@/composables/useHttpRequest.js"

// Composition APIを使うので、export default {} は不要
const data = ref([])
const fields = ref([
    { key: 'hashed_id', sortable: true, label: 'ID'},
    { key: 'name', sortable: true, label: '名前'},
    { key: 'title', sortable: true, label: 'フォームタイトル'},
    { key: 'status_name', sortable: true, label: 'ステータス'},
])
const isAdmin = ref(false)
const router = useRouter()
const { requestGet, loading } = useHttpRequest()


// ライフサイクルフック
onMounted(() => {
  requestGet(
      '/user/isadmin',
      response => {
        isAdmin.value = response.data.is_admin
      }
  )
  requestGet(
      '/form/list',
      response => {
        data.value = convert(response.data.forms)
      }
  )
})

function convert(data) {
  for(const d of data) {
    d.status_name = d.status ? '有効' : '無効'
  }
  return data
}

function detail(data) {
  router.push({ name: 'form_detail', params: { form_id: data.hashed_id } })
}

</script>

<template>
  <main>
    <HeaderMenu :hasMenu="true" :isAdmin="isAdmin"/>
    <div class="container">
      <h1 class="mt-5 mb-5">
        フォーム
      </h1>
      <b-table striped hover :items="data" :fields="fields" @row-clicked="detail"></b-table>
    </div>
  </main>
</template>
