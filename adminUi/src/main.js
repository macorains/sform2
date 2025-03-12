import './assets/main.css'

import { BootstrapVue3 } from 'bootstrap-vue-3'
import { createApp, configureCompat  } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import http from './plugins/axios'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

configureCompat({
    MODE: 3
})

const token = localStorage.getItem('sformToken');
if (token) {
    http.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

const app = createApp(App)
app.config.globalProperties.$http = http
app.use(BootstrapVue3)
app.use(createPinia())
app.use(router)

app.mount('#app')
