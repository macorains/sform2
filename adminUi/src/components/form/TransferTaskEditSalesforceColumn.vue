<template>
  <table class="table table-striped">
    <thead>
    <tr>
      <th scope="col">
        Salesforce項目
      </th>
      <th scope="col">
        型
      </th>
      <th scope="col">
        フォーム項目
      </th>
    </tr>
    </thead>
    <tbody>
    <tr
        v-for="(item, index) in fieldList"
        :key="index"
    >
      <td>{{ item.label }}</td>
      <td>{{ item.field_type }}</td>
      <td>
        <BFormSelect
            v-model="item.column_id"
            :options="column_list"
            size="sm"
        />
      </td>
    </tr>
    </tbody>
  </table>

</template>
<script setup>
import {onMounted, ref, inject} from "vue"

const fields = ref([
  { key: 'label', sortable: true, label: 'No'},
  { key: 'field_type', sortable: true, label: '名前'},
  { key: 'column_id', sortable: true, label: '値'},
])

const fieldList = ref([])
</script>
