<template>
  <div v-if="isLoading" class="loading-overlay">
    <BSpinner label="Processing..." variant="light" />
  </div>
  <BForm>
    <BRow>
      <BCol>
        <BFormGroup label="姓" label-for="lastName" label-cols="2">
          <BFormInput
              id="lastName"
              v-model="userData.last_name"
              type="text"
              :state="userDataLastNameState"
              required
              @input="validateUserDataLastNameState"
          />
          <BFormInvalidFeedback>
            姓を入力してください
          </BFormInvalidFeedback>
        </BFormGroup>
      </BCol>
    </BRow>
    <BRow>
      <BCol>
        <BFormGroup label="名" label-for="firstName" label-cols="2">
          <BFormInput
              id="lastName"
              v-model="userData.first_name"
              type="text"
              :state="userDataFirstNameState"
              required
              @input="validateUserDataFirstNameState"
          />
          <BFormInvalidFeedback>
            名を入力してください
          </BFormInvalidFeedback>
        </BFormGroup>
      </BCol>
    </BRow>
    <BRow>
      <BCol>
        <BFormGroup label="メールアドレス" label-for="email" label-cols="2">
          <BFormInput
              id="email"
              v-model="userData.email"
              type="text"
              :state="userDataEmailState"
              required
              @input="validateUserDataEmailState"
          />
          <BFormInvalidFeedback>
            メールアドレスを入力してください
          </BFormInvalidFeedback>
        </BFormGroup>
      </BCol>
    </BRow>
    <BRow>
      <BCol>
        <BFormGroup label="ロール" label-for="role" label-cols="2">
          <BFormSelect
              id="role"
              v-model="userData.role"
              :options= "roleOptions"
              :state="userDataRoleState"
              required
              @input="validateUserDataRoleState"
          />
          <BFormInvalidFeedback>
            ロールを選択してください
          </BFormInvalidFeedback>
        </BFormGroup>
      </BCol>
    </BRow>
  </BForm>
</template>
<script setup>
import {BSpinner} from "bootstrap-vue-3";
import {computed, getCurrentInstance, reactive, ref, defineEmits} from "vue";

const instance = getCurrentInstance()
const $http = instance.appContext.config.globalProperties.$http

const emit = defineEmits(['load']);

const userData = reactive({
  last_name: '',
  first_name: '',
  email: '',
  role: ''
})

const roleOptions = ref([
  { value: 'operator', text: '一般' },
  { value: 'admin', text: '管理者' },
])

const isLoading = ref(false)

const userDataLastNameState = computed(() => userData.last_name.trim() !== '')
const userDataFirstNameState = computed(() => userData.first_name.trim() !== '')
const userDataEmailState = computed(() => userData.email.trim() !== '')
const userDataRoleState = computed(() => userData.role.trim() !== '')

const validateUserDataLastNameState = () => {
  return userDataLastNameState.value
}
const validateUserDataFirstNameState = () => {
  return userDataFirstNameState.value
}
const validateUserDataEmailState = () => {
  return userDataEmailState.value
}
const validateUserDataRoleState = () => {
  return userDataRoleState.value
}

const loadUser = (data) => {
  userData.user_id = data.user_id
  userData.user_group = data.user_group
  userData.first_name = data.first_name
  userData.last_name = data.last_name
  userData.full_name = data.full_name
  userData.email = data.email
  userData.role = data.role
  userData.avatar_url = data.avatar_url
  console.log(userData)
}
const saveUser = () => {
  $http.post('/user', userData)
      .then(response => {
        console.log(response)
        emit('load')
      })
}

const clearModal = () => {
  userData.user_id = ''
  userData.user_group = ''
  userData.first_name = ''
  userData.last_name = ''
  userData.full_name = ''
  userData.email = ''
  userData.role = ''
  userData.avatar_url = ''
}

defineExpose({
  loadUser,
  saveUser,
  clearModal,
})
</script>
