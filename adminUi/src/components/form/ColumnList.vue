<script setup>
import ColumnEdit from "@/components/form/ColumnEdit.vue";
import {onMounted, ref} from "vue";
defineProps(['formId'])

const data = ref([
  {'col_index': 1, name: 'hoge', col_id: 'hoge', col_type: 1} // TODO APIから取得できるようになったらテストデータ削除
])
const fields = ref([
  { key: 'col_index', sortable: true, label: 'No'},
  { key: 'name', sortable: true, label: '名前'},
  { key: 'col_id', sortable: true, label: 'フォームタイトル'},
  { key: 'col_type', sortable: true, label: 'ステータス'},
  { key: 'actions', label: '操作' },
])

onMounted(() => {
  // TODO 項目一覧取得APIから取得（無ければ作る）
  /*
  $http.get('/form/list')
      .then(response => {
        // this.$data.loading = false
        data.value = convert(response.data.forms)
      })
   */
})

</script>

<template>
  <h4>フォーム項目</h4>
  <b-table striped hover :items="data" :fields="fields" @row-clicked="detail">
    <template #cell(actions)="row">
      <b-button size="sm" @click="edit(row.item, row.index, $event.target)" class="mr-1">
        編集
      </b-button>
      <b-button size="sm" @click="delete(row.item)">
        削除
      </b-button>
    </template>
  </b-table>
  <b-modal id="my-modal">
    <ColumnEdit formColId="1"/>
  </b-modal>
</template>
