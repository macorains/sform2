import './assets/main.css'

import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import http from './plugins/axios'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

const token = localStorage.getItem('sformToken');
if (token) {
    http.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

const app = createApp(App)
app.config.globalProperties.$http = http
app.use(BootstrapVue)
app.use(IconsPlugin)
app.use(createPinia())
app.use(router)

app.mount('#app')
