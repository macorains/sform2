<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import HeaderMenu from '../components/HeaderMenu.vue'

// Composition APIを使うので、export default {} は不要
const data = ref([
  { id:1, name: 'hoge'},
  { id:2, name: 'fuga'},
  { id:3, name: 'foo'},
])
const fields = ref([
    { key: 'id', sortable: true, label: 'ID'},
    { key: 'name', sortable: true, label: '名前'},
])
const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

// ライフサイクルフック
onMounted(() => {
  $http.get('/form/list')
      .then(response => {
        this.$data.loading = false
        this.fields = response.data.forms
      })
})

</script>

<template>
  <main>
    <HeaderMenu />
    Form!
    <b-table striped hover :items="data" :fields="fields"></b-table>
  </main>
</template>
