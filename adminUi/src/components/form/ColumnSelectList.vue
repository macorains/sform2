<template>
  <BTable striped hover :items="localList" :fields="fields">
    <template #cell(select_name)="row">
      <template v-if="row.item.in_edit">
        <BFormInput
          v-model="row.item.select_name"
          type="text"
        />
      </template>
      <template v-if="!row.item.in_edit">
        {{ row.item.select_name }}
      </template>
    </template>
    <template #cell(select_value)="row">
      <template v-if="row.item.in_edit">
        <BFormInput
          v-model="row.item.select_value"
          type="text"
        />
      </template>
      <template v-if="!row.item.in_edit">
        {{ row.item.select_value }}
      </template>
    </template>
    <template #cell(is_default)="row">
      <BFormCheckbox
          v-model="row.item.is_default"
          :disabled="!row.item.in_edit"
      ></BFormCheckbox>
    </template>
    <template #cell(actions)="row">
      <BButton pill size="sm" class="me-1" v-if="row.item.in_edit" @click="cancelEditColSelectList(row.item)"><i class="bi bi-x-circle"></i></BButton>
      <BButton pill size="sm" class="me-1" variant="primary" v-if="row.item.in_edit" @click="endEditColSelectList(row.item)"><i class="bi bi-check-circle"></i></BButton>
      <BButton pill size="sm" class="me-1" variant="danger" v-if="!row.item.in_edit && !inEdit() && isDeletable()" @click="deleteColSelectList(row.index)"><i class="bi bi-trash"></i></BButton>
      <BButton pill size="sm" variant="primary" v-if="!row.item.in_edit && !inEdit()" @click="editColSelectList(row.item)"><i class="bi bi-pencil"></i></BButton>
    </template>
  </BTable>
  <BContainer class="m-0 p-0">
    <BRow class="m-0 p-0" align-h="end">
      <BCol cols="3" class="d-flex justify-content-end">
        <BButton size="sm" variant="primary" v-show="!inEdit()" @click="addColSelectList()">追加</BButton>
      </BCol>
    </BRow>
  </BContainer>
</template>
<script setup>
import {BTable} from "bootstrap-vue-3";
import {computed, defineEmits, ref, watch} from "vue";

const emit = defineEmits(['update:selectList', 'deleteItem', 'editItem', 'validate'])

const props = defineProps({
  selectList: {
    type: Array,
    default: () => []
  }
});

const fields = ref([
  { key: 'select_index', sortable: true, label: 'No'},
  { key: 'select_name', sortable: true, label: '名前'},
  { key: 'select_value', sortable: true, label: '値'},
  { key: 'is_default', sortable: false, label: 'デフォルト値?'},
  { key: 'actions', label: '操作' },
])

// local copy to avoid mutating props directly; synchronize both ways
const localList = ref((props.selectList || []).map(i => ({ ...i })))

watch(() => props.selectList, (v) => {
  localList.value = (v || []).map(i => ({ ...i }))
}, { immediate: true }) // deep: true は不要（参照変化で同期すれば十分）

function syncToParent() {
  emit('update:selectList', localList.value.map(i => ({ ...i })))
}

const oldValue = {
  select_name: null,
  select_value: null
}

const formColSelectList = computed({
  get: () => props.selectList,
  set: (v) => emit('update:selectList', v)
})

const addColSelectList = () => {
  const newIndex = Array.isArray(localList.value) ? localList.value.length + 1 : 1
  const newItem = {
    select_index: newIndex,
    select_name: '選択肢' + newIndex,
    select_value: 'answer' + newIndex,
    is_default: false,
    in_edit: true,
  }
  localList.value = [...localList.value, newItem];
  syncToParent()
}
const inEdit = () => {
  return localList.value.find(it => it.in_edit === true)
}

function deleteColSelectList(index) {
  const arr = [...localList.value];
  arr.splice(index, 1);
  localList.value = arr.map((it, index) => {
    const newItem = { ...it }
    newItem.select_index = index + 1
    return newItem
  })
  syncToParent()
}

function isDeletable() {
  return localList.value.length > 1
}

function editColSelectList(item) {
  const arr = localList.value.map(it => it.select_index === item.select_index ? { ...it, in_edit: true } : it)
  localList.value = arr;
  syncToParent()
}

function endEditColSelectList(item) {
  console.log(item)
  const arr = localList.value.map(it => it.select_index === item.select_index ? { ...it, in_edit: false, select_name: item.select_name, select_value: item.select_value, is_default: item.is_default } : it)
  localList.value = arr;
  //console.log(arr)
  syncToParent()
  emit('validate')
}

function cancelEditColSelectList(item) {
  const arr = localList.value.map(it => it.select_index === item.select_index ? { ...it, in_edit: false } : it)
  localList.value = arr;
  syncToParent()
}

defineExpose({
  addColSelectList,
})

</script>
<!--
    <b-row
        v-if="isSelectable()"
        class="mb-3"
    >
      <b-col cols="4">
        選択肢
      </b-col>
      <b-col>
        <table class="table table-striped">
          <thead>
          <tr>
            <th scope="col">
              No.
            </th>
            <th scope="col">
              名称
            </th>
            <th scope="col">
              値
            </th>
            <th scope="col">
              操作
            </th>
          </tr>
          </thead>
          <tbody>
          <tr
              v-for="(item, index) in formCol.select_list"
              :key="item.select_index"
          >
            <th scope="row">
              {{ Number(index) + 1 }}
            </th>
            <td>
                    <span v-show="!item.in_edit">
                      {{ item.select_name }}
                    </span>
              <b-form-input
                  v-show="item.in_edit"
                  v-model="item.select_name"
                  type="text"
              />
            </td>
            <td>
                    <span v-show="!item.in_edit">
                      {{ item.select_value }}
                    </span>
              <b-form-input
                  v-show="item.in_edit"
                  v-model="item.select_value"
                  type="text"
              />
            </td>
            <td>
              <b-btn
                  v-show="!item.in_edit"
                  size="sm"
                  @click="deleteColSelectList(item.select_index)"
              >
                      <span
                          class="oi oi-trash"
                          title="trash"
                          aria-hidden="true"
                      />
                {{ $t('message.delete') }}
              </b-btn>
              <b-btn
                  v-show="!item.in_edit"
                  size="sm"
                  @click="editColSelectList(item.select_index)"
              >
                      <span
                          class="oi oi-x"
                          title="x"
                          aria-hidden="true"
                      />
                {{ $t('message.edit') }}
              </b-btn>
              <b-btn
                  v-show="item.in_edit"
                  size="sm"
                  @click="endEditColSelectList(item.select_index)"
              >
                      <span
                          class="oi oi-check"
                          title="check"
                          aria-hidden="true"
                      />
                {{ $t('message.ok') }}
              </b-btn>
            </td>
          </tr>
          </tbody>
        </table>
        <b-btn
            class="mt-3"
            block
            @click="addColSelectList"
        >
              <span
                  class="oi oi-plus"
                  title="plus"
                  aria-hidden="true"
              />
          {{ $t('message.add') }}
        </b-btn>
      </b-col>
    </b-row>
-->
