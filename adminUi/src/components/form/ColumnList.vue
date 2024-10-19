<template>
  <h4>フォーム項目 <BButton size="sm">追加</BButton></h4>
  <BTable striped hover :items="form.form_cols" :fields="fields">
    <template #cell(actions)="row">
      <BButton size="sm" @click="edit(row.item, row.index, $event.target)" class="mr-1">
        編集
      </BButton>
      <BButton size="sm" @click="delete(row.item)">
        削除
      </BButton>
    </template>
  </BTable>
  <BModal v-model="editModalVisible" size="xl" title="フォーム項目編集">
    <ColumnEdit ref="columnEditRef" :formColId="selectedFormColId"/>
  </BModal>
</template>

<script setup>
import ColumnEdit from "@/components/form/ColumnEdit.vue"
import {onMounted, ref, inject} from "vue"
import { BButton, BTable, BModal } from 'bootstrap-vue-3'
const emit = defineEmits(['update-column'])

const form = inject('form')
const fields = ref([
  { key: 'col_index', sortable: true, label: 'No'},
  { key: 'name', sortable: true, label: '名前'},
  { key: 'col_id', sortable: true, label: 'フォームタイトル'},
  { key: 'col_type', sortable: true, label: 'ステータス'},
  { key: 'actions', label: '操作' },
])
const editModalVisible = ref(false)
const selectedFormColId = ref(null)
const columnEditRef = ref(null)


onMounted(() => {
  // data.value = props.form.form_cols
})

const saveColumn = () => {
  alert('fuga!')
  emit('update-column', { name: '新しい名前' });
}


const edit = (item, index, target) => {
  columnEditRef.value.setFormCol(item)
  selectedFormColId.value = item.id
  editModalVisible.value = true
}
defineExpose({
  saveColumn
});
</script>

