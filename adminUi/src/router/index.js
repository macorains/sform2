import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AdminView from '../views/AdminView.vue'
import FormView from '../views/FormView.vue'
import FormDetailView from '../views/FormDetailView.vue'
import LoginView from '../views/LoginView.vue'
import LoginFailedView from '../views/LoginFailedView.vue'
import VerificationView from '../views/VerificationView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
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
      component: LoginView
    },
    {
      path: '/login_failed',
      name: 'loginFailed',
      component: LoginFailedView
    },
    {
      path: '/verification/:authkey',
      name: 'verification',
      component: VerificationView
    },
  ]
})

export default router
