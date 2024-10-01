<script setup>
import {onMounted, ref} from "vue";
import ColumnEdit from "@/components/form/ColumnEdit.vue";
defineProps(['formId'])

const fields = ref([
  { key: 'index', sortable: true, label: '順序'},
  { key: 'name', sortable: true, label: '転送タスク名'},
  { key: 'config_name', sortable: true, label: '転送設定名'},
  { key: 'actions', label: '操作' },
])
const data = ref([
  {index: 1, name: 'hoge', config_name: 'hoge'} // TODO APIから取得できるようになったらテストデータ削除
])

onMounted(() => {
  // TODO 転送タスク一覧取得APIから取得（無ければ作る）
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
  <h4>転送タスク設定</h4>
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
