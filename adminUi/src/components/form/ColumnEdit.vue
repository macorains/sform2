<script setup>
defineProps(['formColId'])
</script>
<template>
  <b-container class="text-left form-col-edit">
    <b-row class="mb-3">
      <b-col cols="4">
        カラム名
      </b-col>
      <b-col>
        <b-form-input
            id="formColName"
            v-model="tmpFormCol.name"
            type="text"
        />
      </b-col>
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        カラムID
      </b-col>
      <b-col>
        <b-form-input
            id="formColId"
            v-model="tmpFormCol.col_id"
            type="text"
        />
      </b-col>
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        型
      </b-col>
      <b-col>
        <b-form-select
            v-model="tmpFormCol.col_type"
            :options="optionFormColType"
            class="mb-3"
        />
      </b-col>
    </b-row>
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
              v-for="(item, index) in tmpFormCol.select_list"
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
    <b-row class="mb-3">
      <b-col cols="4">
        {{ $t('message.initial_value') }}
      </b-col>
      <b-col>
        <b-form-input
            id="formColDefault"
            v-model="tmpFormCol.default_value"
            type="text"
        />
      </b-col>
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        {{ $t('message.validation') }}
      </b-col>
      <b-col>
        <b-form-select
            v-model="tmpFormCol.validations.input_type"
            :options="optionFormColValidation"
            class="mb-3"
        />
      </b-col>
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        {{ $t('message.number_range') }}
      </b-col>
      <b-col cols="2">
        <b-form-input
            id="formColValidationMinValue"
            v-model="tmpFormCol.validations.min_value"
            type="text"
        />
      </b-col>
      <b-col cols="1">
        ～
      </b-col>
      <b-col cols="2">
        <b-form-input
            id="formColValidationMaxValue"
            v-model="tmpFormCol.validations.max_value"
            type="text"
        />
      </b-col>
      <b-col cols="5" />
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        {{ $t('message.string_length') }}
      </b-col>
      <b-col cols="2">
        <b-form-input
            id="formColValidationMinLength"
            v-model="tmpFormCol.validations.min_length"
            type="text"
        />
      </b-col>
      <b-col cols="1">
        ～
      </b-col>
      <b-col cols="2">
        <b-form-input
            id="formColValidationMaxLength"
            v-model="tmpFormCol.validations.max_length"
            type="text"
        />
      </b-col>
      <b-col cols="5" />
    </b-row>
    <b-row class="mb-3">
      <b-col cols="4">
        {{ $t('message.required_item') }}
      </b-col>
      <b-col>
        <b-form-checkbox
            id="formColRequired"
            v-model="tmpFormCol.validations.required"
            value="true"
            unchecked-value="false"
        />
      </b-col>
    </b-row>
  </b-container>
</template>
