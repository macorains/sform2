<template>
  <h4>フォーム項目<BButton class="ms-2" size="sm" @click="addColumn">追加</BButton></h4>
  <BTable striped hover :items="form_cols" :fields="fields">
    <template #cell(col_index)="row">
      {{ row.item.col_index + 1 }}
    </template>
    <template #cell(actions)="row">
      <BButton size="sm" @click="edit(row.item, row.index, $event.target)" class="mr-1">
        編集
      </BButton>
      <BButton class="ms-2" size="sm" @click="deleteColumn(row.item)">
        削除
      </BButton>
    </template>
  </BTable>
  <BModal v-model="editModalVisible" size="xl" title="フォーム項目編集" @ok="updateColumn">
    <ColumnEdit ref="columnEditRef" :formColId="selectedFormColId"/>
  </BModal>
</template>

<script setup>
import ColumnEdit from "@/components/form/ColumnEdit.vue"
import {onMounted, ref, inject, reactive} from "vue"
import { BButton, BTable, BModal } from 'bootstrap-vue-3'
const emit = defineEmits(['update-column'])

//const form = inject('form')
const fields = ref([
  { key: 'col_index', sortable: true, label: 'No'},
  { key: 'name', sortable: true, label: '名前'},
  { key: 'col_id', sortable: true, label: '項目ID'},
  { key: 'col_type', sortable: true, label: 'ステータス'},
  { key: 'actions', label: '操作' },
])
const editModalVisible = ref(false)
const selectedFormColId = ref(null)
const columnEditRef = ref(null)
const form_cols = reactive([])


onMounted(() => {
  // data.value = props.form.form_cols
  //console.log(form.form_cols)
  //form.form_cols.forEach(fc => form_cols.push(fc))
})
const load = (col_list) => {
  col_list.forEach(col => form_cols.push(col))
}

const updateColumn = () => {
  const formCol = columnEditRef.value.getFormCol()
  const target_index = form_cols.findIndex(col => col.col_index === formCol.col_index)
  if (target_index !== -1) {
    Object.assign(form_cols[target_index], formCol)
  }
}

const addColumn = () => {
  const i = form_cols.length
  const colNum = i + 1
  var tmp = {
    col_index: i,
    name: '項目' + colNum,
    col_id: 'col' + colNum,
    col_type: 1,
    default_value: '',
    form_id: 'hoge',
    id: null,
    validations: {
      id: null,
      input_type: 0,
      min_value: '',
      max_value: '',
      min_length: '',
      max_length: '',
      form_col_id: null,
      form_id: 'hoge',
      required: false
    },
    select_list: []
  }
  form_cols.push(tmp)
  emit('update-column', form_cols);
}

const deleteColumn = (item) => {
  form_cols.splice(item.col_index, 1)
  const cols_count = form_cols.length
  for(let i = 0; i < cols_count; i++) {
    form_cols[i].col_index = i
  }
}
const edit = (item, index, target) => {
  columnEditRef.value.setFormCol(item)
  selectedFormColId.value = item.id
  editModalVisible.value = true
}
defineExpose({
  load
});
</script>

