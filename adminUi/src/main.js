import './assets/main.css'

import { BootstrapVue3 } from 'bootstrap-vue-3'
import { createApp, configureCompat  } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import http from './plugins/axios'
import { useAuthStore } from './stores/auth'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

configureCompat({
    MODE: 3
})

const app = createApp(App)
app.config.globalProperties.$http = http
app.use(BootstrapVue3)
app.use(createPinia())

// 401 を受けたらログイン状態を破棄してログイン画面へ（フルリロードはしない）
// 同時に複数の 401 が返っても遷移は1回だけにする
const auth = useAuthStore()
let redirecting = false
http.setUnauthorizedHandler(() => {
    auth.clear()
    const current = router.currentRoute.value
    if (redirecting || current.meta.public) return
    redirecting = true
    router.push({ name: 'login', query: { redirect: current.fullPath } })
        .finally(() => { redirecting = false })
})

app.use(router)

app.mount('#app')
