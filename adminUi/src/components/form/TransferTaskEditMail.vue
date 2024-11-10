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
              id="transferTask.mail.subject"
              ref="subject"
              v-model="transferTask.mail.subject"
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
              id="transferTask.mail.from_address_id"
              v-model="transferTask.mail.from_address_id"
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
              v-model="transferTask.mail.to_address_type"
              name="mail_to_type_component"
          >
            <BFormRadio value="to_mail_address">
              メールアドレス指定
            </BFormRadio>
            <BFormRadio value="to_mail_address_id">
              登録済みメールアドレス選択
            </BFormRadio>
            <BFormRadio value="to_mail_address_field">
              フォーム項目選択
            </BFormRadio>
          </BFormRadioGroup>
          <BFormInput
              v-if="transferTask.mail.to_address_type === 'to_mail_address'"
              id="transferTask.mail.to_address"
              ref="to_address"
              v-model="transferTask.mail.to_address"
              type="text"
          />
          <BFormSelect
              v-if="transferTask.mail.to_address_type === 'to_mail_address_id'"
              id="transferTask.mail.to_address_id"
              v-model="transferTask.mail.to_address_id"
              :options="mailAddressList"
          />
          <BFormSelect
              v-if="transferTask.mail.to_address_type === 'to_mail_address_field'"
              id="transferTask.mail.to_address_field"
              v-model="transferTask.mail.to_address_field"
              :options="fieldList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.mail.use_cc"
          class="mb-2"
      >
        <BCol cols="2">
          cc
        </BCol>
        <BCol cols="10">
          <BFormRadioGroup
              id="mail_cc_type_group"
              v-model="transferTask.mail.cc_address_type"
              name="mail_cc_type_component"
          >
            <BFormRadio value="cc_mail_address">
              メールアドレス指定
            </BFormRadio>
            <BFormRadio value="cc_mail_address_id">
              登録済みメールアドレス選択
            </BFormRadio>
            <BFormRadio value="cc_mail_address_field">
              フォームフィールド選択
            </BFormRadio>
          </BFormRadioGroup>
          <BFormInput
              v-if="transferTask.mail.cc_address_type === 'cc_mail_address'"
              id="transferTask.mail.cc_address"
              ref="cc_address"
              v-model="transferTask.mail.cc_address"
              type="text"
          />
          <BFormSelect
              v-if="transferTask.mail.cc_address_type === 'cc_mail_address_id'"
              id="transferTask.mail.cc_address_id"
              v-model="transferTask.mail.cc_address_id"
              :options="mailAddressList"
          />
          <BFormSelect
              v-if="transferTask.mail.cc_address_type === 'cc_mail_address_field'"
              id="transferTask.mail.cc_address_field"
              v-model="transferTask.mail.cc_address_field"
              :options="fieldList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.mail.use_bcc"
          class="mb-2"
      >
        <BCol cols="2">
          bcc
        </BCol>
        <BCol cols="10">
          <BFormSelect
              id="transferTask.mail.bcc_address_id"
              v-model="transferTask.mail.bcc_address_id"
              :options="mailAddressList"
          />
        </BCol>
      </BRow>
      <BRow
          v-if="transferConfig.detail.mail.use_replyto"
          class="mb-2"
      >
        <BCol cols="2">
          {{ $t('message.mail_replyto') }}
        </BCol>
        <BCol cols="10">
          <BFormSelect
              id="transferTask.mail.replyto_address_id"
              v-model="transferTask.mail.replyto_address_id"
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
              id="transferTask.mail.body"
              ref="body"
              v-model="transferTask.mail.body"
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
import {onBeforeMount, ref} from "vue";

const transferTask = ref({ mail: {}})
const transferConfig = ref({ detail: { mail: { use_cc: true }}})

onBeforeMount(() => {
  // TODO 本実装する

})

</script>
