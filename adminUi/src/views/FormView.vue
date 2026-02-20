<script setup>
import { ref, inject, onMounted } from 'vue'
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
    { key: 'actions', label: '操作' },
])
const isAdmin = ref(false)
const router = useRouter()
const { requestGet, requestPost, requestDelete, loading } = useHttpRequest()
const showConfirm = inject("showConfirm")


function loadFormList(callback) {
  requestGet(
      '/form/list',
      response => {
        data.value = convert(response.data.forms)
        if (callback) callback()
      }
  )
}

// ライフサイクルフック
onMounted(() => {
  requestGet(
      '/user/isadmin',
      response => {
        isAdmin.value = response.data.is_admin
      }
  )
  loadFormList()
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

function createForm() {
  const name = 'フォーム' + (data.value.length + 1)
  const formData = {
    id: null,
    name: name,
    title: name,
    form_index: data.value.length + 1,
    status: 0,
    cancel_url: 'http://default1.sform.app',
    complete_url: 'http://default2.sform.app',
    input_header: 'フォームに入力してください',
    confirm_header: '入力内容を確認してください',
    complete_text: '入力内容を受け付けました',
    close_text: 'フォームは停止中です',
    hashed_id: '',
    form_cols: [],
    form_transfer_tasks: []
  }
  requestPost('/form/new', formData, (response) => {
    const newId = response.data.id
    loadFormList(() => {
      const newForm = data.value.find(f => f.id === newId)
      if (newForm) {
        router.push({ name: 'form_detail', params: { form_id: newForm.hashed_id } })
      }
    })
  })
}

function copyId(id) {
  navigator.clipboard.writeText(id)
}

function deleteForm(row) {
  showConfirm('フォーム: ' + row.item.name + 'を削除します。よろしいですか？', (result) => {
    if (result) {
      requestDelete(
          '/form/' + row.item.hashed_id,
          _ => {
            loadFormList()
          }
      )
    }
  })
}

</script>

<template>
  <main>
    <HeaderMenu :hasMenu="true" :isAdmin="isAdmin"/>
    <div class="container">
      <h1 class="mt-5 mb-5">フォーム</h1>
      <b-table striped hover :items="data" :fields="fields">
        <template #cell(hashed_id)="row">
          {{ row.item.hashed_id }}
          <i class="bi bi-clipboard ms-1" role="button" @click="copyId(row.item.hashed_id)"></i>
        </template>
        <template #cell(actions)="row">
          <BButton variant="primary" size="sm" @click="detail(row.item)" class="me-1">
            <i class="bi bi-pencil"></i>
            編集
          </BButton>
          <BButton variant="danger" size="sm" @click="deleteForm(row)">
            <i class="bi bi-trash"></i>
            削除
          </BButton>
        </template>
      </b-table>
      <BButton class="mt-3" variant="primary" @click="createForm"><i class="bi bi-plus"></i>フォーム追加</BButton>
    </div>
  </main>
</template>
