<template>
    <BContainer class="text-left">
      <BRow class="mb-2">
        <BCol cols="2">
          転送タスク名
        </BCol>
        <BCol cols="10">
          <BFormInput
              id="transferTask.name"
              v-model="transferTask.name"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          メール件名
        </BCol>
        <BCol cols="10">
          <BFormInput
              id="transferTask.smtpmail.subject"
              v-model="transferTask.smtpmail.subject"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          to
        </BCol>
        <BCol cols="10">
          <BFormRadioGroup
              id="mail_to_type_group"
              v-model="transferTask.smtpmail.to_address_type"
              name="mail_to_type_component"
              :options="[
                { value: 'to_mail_address', text: 'メールアドレス指定' },
                { value: 'to_mail_address_field', text: 'フォーム項目選択' },
              ]"
          />
          <BFormInput
              v-if="transferTask.smtpmail.to_address_type === 'to_mail_address'"
              id="transferTask.smtpmail.to_address"
              v-model="transferTask.smtpmail.to_address"
              type="text"
          />
          <BFormSelect
              v-if="transferTask.smtpmail.to_address_type === 'to_mail_address_field'"
              id="transferTask.smtpmail.to_address_field"
              v-model="transferTask.smtpmail.to_address_field"
              :options="fieldList"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          cc
        </BCol>
        <BCol cols="10">
          <BFormInput
              id="transferTask.smtpmail.cc_address"
              v-model="transferTask.smtpmail.cc_address"
              type="text"
              placeholder="省略可"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          bcc
        </BCol>
        <BCol cols="10">
          <BFormInput
              id="transferTask.smtpmail.bcc_address"
              v-model="transferTask.smtpmail.bcc_address"
              type="text"
              placeholder="省略可"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          メール本文
        </BCol>
        <BCol cols="10">
          <BFormTextarea
              id="transferTask.smtpmail.body"
              ref="body"
              v-model="transferTask.smtpmail.body"
              :rows="10"
          />
          <span
              v-for="field in fieldList"
              :key="field.value"
          >
              <BButton
                  pill
                  @click="insertTag(field.value)"
              >
                {{ field.text }}
              </BButton>
            </span>
        </BCol>
      </BRow>
    </BContainer>
</template>

<script setup>
import { ref, reactive } from "vue"

const transferTask = reactive({
  id: null,
  transfer_config_id: null,
  name: '',
  smtpmail: {
    id: null,
    form_transfer_task_id: null,
    subject: '',
    body: '',
    to_address_type: 'to_mail_address',
    to_address: '',
    to_address_field: null,
    cc_address: '',
    bcc_address: '',
  }
})
const fieldList = ref([])
const body = ref(null)

const load = (data, form_cols) => {
  transferTask.id = data.id
  transferTask.transfer_config_id = data.transfer_config_id
  transferTask.name = data.name

  const smtpmail = data.smtpmail || {}
  transferTask.smtpmail.id = smtpmail.id ?? null
  transferTask.smtpmail.form_transfer_task_id = smtpmail.form_transfer_task_id ?? null
  transferTask.smtpmail.subject = smtpmail.subject ?? ''
  transferTask.smtpmail.body = smtpmail.body ?? ''
  transferTask.smtpmail.to_address = smtpmail.to_address ?? ''
  transferTask.smtpmail.to_address_field = smtpmail.to_address_field ?? null
  transferTask.smtpmail.cc_address = smtpmail.cc_address ?? ''
  transferTask.smtpmail.bcc_address = smtpmail.bcc_address ?? ''
  transferTask.smtpmail.to_address_type = smtpmail.to_address_field ? 'to_mail_address_field' : 'to_mail_address'

  fieldList.value = form_cols.map(fc => ({ value: fc.id, text: fc.name }))
}

const insertTag = (fieldId) => {
  const el = body.value.$el.querySelector('textarea')
  const start = el.selectionStart
  const end = el.selectionEnd
  const tag = `{${fieldId}}`
  transferTask.smtpmail.body = transferTask.smtpmail.body.slice(0, start) + tag + transferTask.smtpmail.body.slice(end)
  const newPos = start + tag.length
  el.focus()
  el.setSelectionRange(newPos, newPos)
}

const getData = () => {
  return {
    id: transferTask.smtpmail.id,
    form_transfer_task_id: transferTask.smtpmail.form_transfer_task_id,
    subject: transferTask.smtpmail.subject,
    body: transferTask.smtpmail.body,
    to_address: transferTask.smtpmail.to_address_type === 'to_mail_address' ? transferTask.smtpmail.to_address : null,
    to_address_field: transferTask.smtpmail.to_address_type === 'to_mail_address_field' ? transferTask.smtpmail.to_address_field : null,
    cc_address: transferTask.smtpmail.cc_address || null,
    bcc_address: transferTask.smtpmail.bcc_address || null,
  }
}

defineExpose({ load, getData })
</script>
