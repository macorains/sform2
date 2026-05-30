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
              id="transferTask.sesmail.subject"
              ref="subject"
              v-model="transferTask.sesmail.subject"
              type="text"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          from
        </BCol>
        <BCol cols="10">
          <BFormSelect
              id="transferTask.sesmail.from_address_id"
              v-model="transferTask.sesmail.from_address_id"
              :options="mailAddressList"
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
              v-model="transferTask.sesmail.to_address_type"
              name="mail_to_type_component"
              :options="[
                { value: 'to_mail_address', text: 'メールアドレス指定' },
                { value: 'to_mail_address_id', text: '登録済みメールアドレス選択' },
                { value: 'to_mail_address_field', text: 'フォーム項目選択' },
              ]"
          />
          <BFormInput
              v-if="transferTask.sesmail.to_address_type === 'to_mail_address'"
              id="transferTask.sesmail.to_address"
              ref="to_address"
              v-model="transferTask.sesmail.to_address"
              type="text"
          />
          <BFormSelect
              v-if="transferTask.sesmail.to_address_type === 'to_mail_address_id'"
              id="transferTask.sesmail.to_address_id"
              v-model="transferTask.sesmail.to_address_id"
              :options="mailAddressList"
          />
          <BFormSelect
              v-if="transferTask.sesmail.to_address_type === 'to_mail_address_field'"
              id="transferTask.sesmail.to_address_field"
              v-model="transferTask.sesmail.to_address_field"
              :options="fieldList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.sesmail.use_cc"
          class="mb-2"
      >
        <BCol cols="2">
          cc
        </BCol>
        <BCol cols="10">
          <BFormRadioGroup
              id="mail_cc_type_group"
              v-model="transferTask.sesmail.cc_address_type"
              name="mail_cc_type_component"
              :options="[
                { value: 'cc_mail_address', text: 'メールアドレス指定' },
                { value: 'cc_mail_address_id', text: '登録済みメールアドレス選択' },
                { value: 'cc_mail_address_field', text: 'フォーム項目選択' },
              ]"
          />
          <BFormInput
              v-if="transferTask.sesmail.cc_address_type === 'cc_mail_address'"
              id="transferTask.sesmail.cc_address"
              ref="cc_address"
              v-model="transferTask.sesmail.cc_address"
              type="text"
          />
          <BFormSelect
              v-if="transferTask.sesmail.cc_address_type === 'cc_mail_address_id'"
              id="transferTask.sesmail.cc_address_id"
              v-model="transferTask.sesmail.cc_address_id"
              :options="mailAddressList"
          />
          <BFormSelect
              v-if="transferTask.sesmail.cc_address_type === 'cc_mail_address_field'"
              id="transferTask.sesmail.cc_address_field"
              v-model="transferTask.sesmail.cc_address_field"
              :options="fieldList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.sesmail.use_bcc"
          class="mb-2"
      >
        <BCol cols="2">
          bcc
        </BCol>
        <BCol cols="10">
          <BFormSelect
              id="transferTask.sesmail.bcc_address_id"
              v-model="transferTask.sesmail.bcc_address_id"
              :options="mailAddressList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.sesmail.use_replyto"
          class="mb-2"
      >
        <BCol cols="2">
          {{ $t('message.mail_replyto') }}
        </BCol>
        <BCol cols="10">
          <BFormSelect
              id="transferTask.sesmail.replyto_address_id"
              v-model="transferTask.sesmail.replyto_address_id"
              :options="mailAddressList"
          />
        </BCol>
      </BRow>
      <BRow class="mb-2">
        <BCol cols="2">
          メール本文
        </BCol>
        <BCol cols="10">
          <BFormTextarea
              id="transferTask.sesmail.body"
              ref="body"
              v-model="transferTask.sesmail.body"
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
import { useHttpRequest } from "@/composables/useHttpRequest.js"

const { requestGet } = useHttpRequest()

const transferTask = reactive({
  id: null,
  transfer_config_id: null,
  name: '',
  sesmail: {
    subject: '',
    from_address_id: null,
    to_address_type: 'to_mail_address',
    to_address: '',
    to_address_id: null,
    to_address_field: null,
    cc_address_type: 'cc_mail_address',
    cc_address: '',
    cc_address_id: null,
    cc_address_field: null,
    bcc_address_id: null,
    replyto_address_id: null,
    body: '',
  }
})
const transferConfig = reactive({ detail: { sesmail: { use_cc: false, use_bcc: false, use_replyto: false } } })
const fieldList = ref([])
const mailAddressList = ref([])
const body = ref(null)

const load = (data, form_cols) => {
  transferTask.id = data.id
  transferTask.transfer_config_id = data.transfer_config_id
  transferTask.name = data.name
  Object.assign(transferTask.sesmail, data.sesmail)

  fieldList.value = form_cols.map(fc => ({ value: fc.id, text: fc.name }))

  requestGet(
    `/transfer/config/${data.transfer_config_id}`,
    response => {
      const mail = response.data.detail.sesmail
      mailAddressList.value = mail.mail_address_list.map(item => ({ value: item.id, text: item.name }))
      transferConfig.detail.sesmail.use_cc = mail.use_cc
      transferConfig.detail.sesmail.use_bcc = mail.use_bcc
      transferConfig.detail.sesmail.use_replyto = mail.use_replyto
    },
    error => {
      console.log(error)
    }
  )
}

const insertTag = (fieldId) => {
  const el = body.value.$el
  const start = el.selectionStart
  const end = el.selectionEnd
  const tag = `{${fieldId}}`
  transferTask.sesmail.body = transferTask.sesmail.body.slice(0, start) + tag + transferTask.sesmail.body.slice(end)
  const newPos = start + tag.length
  el.focus()
  el.setSelectionRange(newPos, newPos)
}

const getMailData = () => {
  return {
    from_address_id: transferTask.sesmail.from_address_id,
    to_address: transferTask.sesmail.to_address_type === 'to_mail_address' ? transferTask.sesmail.to_address : null,
    to_address_id: transferTask.sesmail.to_address_type === 'to_mail_address_id' ? transferTask.sesmail.to_address_id : null,
    subject: transferTask.sesmail.subject,
    body: transferTask.sesmail.body,
    cc_address: transferTask.sesmail.cc_address_type === 'cc_mail_address' ? transferTask.sesmail.cc_address : null,
    cc_address_id: transferTask.sesmail.cc_address_type === 'cc_mail_address_id' ? transferTask.sesmail.cc_address_id : null,
    bcc_address_id: transferTask.sesmail.bcc_address_id,
    replyto_address_id: transferTask.sesmail.replyto_address_id,
  }
}

defineExpose({ load, getMailData })
</script>
