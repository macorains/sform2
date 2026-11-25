import { defineStore } from 'pinia'
import http from '@/plugins/axios'

const REDIRECT_KEY = 'sformLoginRedirect'

// 同時に複数回 ensureLoaded() が呼ばれても /me は1回だけにする
let pending = null

/**
 * フロント側のログイン状態。
 * status: 'unknown'（未確認） | 'authenticated' | 'unauthenticated'
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    status: 'unknown',
    user: null
  }),
  getters: {
    isAuthenticated: (state) => state.status === 'authenticated',
    isAdmin: (state) => state.user?.is_admin === true
  },
  actions: {
    ensureLoaded() {
      if (this.status !== 'unknown') return Promise.resolve()
      if (!pending) {
        pending = this.fetchMe().finally(() => {
          pending = null
        })
      }
      return pending
    },
    async fetchMe() {
      if (!http.hasToken()) {
        this.setUnauthenticated()
        return
      }
      try {
        const response = await http.get('/me', { skipAuthRedirect: true })
        this.user = response.data
        this.status = 'authenticated'
      } catch (error) {
        if (error.response?.status === 401) {
          this.clear()
        } else {
          // 通信エラー等。トークンは残し、次の画面遷移で再確認する
          console.error(error)
          this.user = null
          this.status = 'unknown'
          throw error
        }
      }
    },
    setUnauthenticated() {
      this.user = null
      this.status = 'unauthenticated'
    },
    clear() {
      http.clearToken()
      this.setUnauthenticated()
    },
    // OIDC ログイン後は固定URLに戻るため、元の遷移先を一時保存しておく
    saveRedirect(path) {
      try {
        if (path) sessionStorage.setItem(REDIRECT_KEY, path)
        else sessionStorage.removeItem(REDIRECT_KEY)
      } catch {
        // sessionStorage が使えない場合は復帰先なし
      }
    },
    consumeRedirect() {
      try {
        const path = sessionStorage.getItem(REDIRECT_KEY)
        sessionStorage.removeItem(REDIRECT_KEY)
        return path
      } catch {
        return null
      }
    }
  }
})
