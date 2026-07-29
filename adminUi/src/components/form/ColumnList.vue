<template>
  <h4>フォーム項目<BButton class="ms-2" size="sm" @click="addColumn">追加</BButton></h4>
  <table class="table table-striped table-hover">
    <thead>
      <tr>
        <th>No</th>
        <th>名前</th>
        <th>項目ID</th>
        <th>型</th>
        <th>操作</th>
      </tr>
    </thead>
    <draggable :list="form_cols" tag="tbody" item-key="col_id" @end="onDragEnd">
      <template #item="{ element }">
        <tr>
          <td>{{ element.col_index + 1 }}</td>
          <td>{{ element.name }}</td>
          <td>{{ element.col_id }}</td>
          <td>{{ formColTypeText(element.col_type) }}</td>
          <td>
            <BButton size="sm" @click="edit(element)" class="mr-1">編集</BButton>
            <BButton class="ms-2" size="sm" @click="deleteColumn(element)">削除</BButton>
          </td>
        </tr>
      </template>
    </draggable>
  </table>
  <BModal v-model="editModalVisible" size="xl" title="フォーム項目編集" @ok="updateColumn" @changeOkButtonStatus="" :ok-disabled="okButtonDisabled">
    <ColumnEdit ref="columnEditRef" @checkColumnIdExists="checkColumnIdExists" @checkColumnNameExists="checkColumnNameExists" @update-button-state="updateButtonState" :column-id-check-result="columnIdCheckResult" :column-name-check-result="columnNameCheckResult" :formColId="selectedFormColId"/>
  </BModal>
</template>

<script setup>
import ColumnEdit from "@/components/form/ColumnEdit.vue"
import {onMounted, ref, inject, reactive, computed} from "vue"
import { BButton, BModal } from 'bootstrap-vue-3'
import draggable from 'vuedraggable'
import {formColTypeText} from "@/composables/useFormColType"
const emit = defineEmits(['update-column'])

const editModalVisible = ref(false)
const selectedFormColId = ref(null)
const columnEditRef = ref(null)
const form_cols = reactive([])

const columnIdCheckResult = ref(false)
const columnNameCheckResult = ref(false)
const okButtonDisabled = ref(true)

const load = (col_list) => {
  col_list.forEach(col => form_cols.push(col))
  emit('update-column', form_cols)
}

const onDragEnd = () => {
  form_cols.forEach((col, i) => {
    col.col_index = i
  })
  emit('update-column', form_cols)
}

const updateColumn = () => {
  const formCol = columnEditRef.value.getFormCol()
  const target_index = form_cols.findIndex(col => col.col_index === formCol.col_index)
  if (target_index !== -1) {
    Object.assign(form_cols[target_index], formCol)
  }
  emit('update-column', form_cols)
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
    form_id: null,
    id: null,
    validations: {
      id: null,
      input_type: 0,
      min_value: null,
      max_value: null,
      min_length: null,
      max_length: null,
      form_col_id: null,
      form_id: null,
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
const edit = (item) => {
  columnEditRef.value.setFormCol(item)
  selectedFormColId.value = item.id
  editModalVisible.value = true
}

const checkColumnIdExists = (data) => {
  columnIdCheckResult.value = form_cols.some((col) => col.col_id === data.col_id && col.id !== data.id)
}

const checkColumnNameExists = (data) => {
  columnNameCheckResult.value = form_cols.some((col) => col.name === data.name && col.id !== data.id)
}

const updateButtonState = (state) => {
  okButtonDisabled.value = state
}

defineExpose({
  load
});
</script>
