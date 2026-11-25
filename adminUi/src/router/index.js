import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import FormView from '../views/FormView.vue'
import FormDetailView from '../views/FormDetailView.vue'
import LoginView from '../views/LoginView.vue'
import LoginFailedView from '../views/LoginFailedView.vue'
import LogoutView from '../views/LogoutView.vue'
import NotFoundView from '../views/NotFoundView.vue'
import { useAuthStore } from '@/stores/auth'

// meta.public: ログイン不要 / meta.requiresAdmin: admin ロールのみ
// それ以外のルートはログイン必須
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { public: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/form',
      name: 'form',
      component: FormView
    },
    {
      path: '/form_detail/:form_id',
      name: 'form_detail',
      component: FormDetailView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true }
    },
    {
      path: '/login_failed',
      name: 'loginFailed',
      component: LoginFailedView,
      meta: { public: true }
    },
    {
      path: '/logout',
      name: 'logout',
      component: LogoutView,
      meta: { public: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: NotFoundView,
      meta: { public: true }
    },
  ]
})

const isInternalPath = (path) => typeof path === 'string' && path.startsWith('/') && !path.startsWith('//')

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.public && to.name !== 'login') {
    // ヘッダー表示用に状態だけ確認しておく（遷移は待たない）
    auth.ensureLoaded().catch(() => {})
    return true
  }

  try {
    await auth.ensureLoaded()
  } catch {
    // 通信エラー時は状態不明のままログイン画面へ（再試行は次の遷移で行われる）
  }

  if (to.name === 'login') {
    return auth.isAuthenticated ? { name: 'form' } : true
  }

  if (!auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // ログイン前に開こうとしていた画面へ戻す
  const saved = auth.consumeRedirect()
  if (isInternalPath(saved) && saved !== to.fullPath) {
    return saved
  }

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { name: 'form' }
  }
  return true
})

export default router
