<template>
  <BTable striped hover :items="formCol.select_list" :fields="fields">
    <template #cell(select_name)="row">
      { row.item }
    </template>
    <template #cell(select_value)="row"></template>
    <template #cell(actions)="row"></template>
  </BTable>
</template>
<script setup>
import {BTable} from "bootstrap-vue-3";
import {inject, onMounted, ref} from "vue";

const formCol = inject('formCol')
const fields = ref([
  { key: 'select_index', sortable: true, label: 'No'},
  { key: 'select_name', sortable: true, label: '名前'},
  { key: 'select_value', sortable: true, label: '値'},
  { key: 'actions', label: '操作' },
])

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
