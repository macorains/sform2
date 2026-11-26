import axios from 'axios'

// ログイン状態は adminApi が発行する HttpOnly のセッション Cookie だけで管理する。
// - withCredentials: クロスオリジンでも Cookie を送受信するため必須
// - X-Requested-With: adminApi の CSRF チェックはこのヘッダーで通す設定になっている（外さないこと）
const http = axios.create({
  baseURL: import.meta.env.VITE_APP_API_URL,
  withCredentials: true,
  headers: {
    'x-Requested-With': '*',
    'Access-Control-Allow-Origin': '*',
    timeout: 3000,
    common: {
      'Access-Control-Allow-Origin': '*'
    }
  }
})

// 旧方式（JWT を localStorage に保存）の残骸を消しておく
try {
  localStorage.removeItem('sformToken')
} catch {
  // localStorage が使えない環境では何もしない
}

// 認証切れ(401)時の処理。ルーター/ストアへの依存を避けるため main.js から登録する
let unauthorizedHandler = null
http.setUnauthorizedHandler = (handler) => {
  unauthorizedHandler = handler
}

http.interceptors.response.use(
  response => response,
  error => {
    // skipAuthRedirect: 401 を呼び出し元で扱う場合（/me による状態確認など）
    if (error.response?.status === 401 && !error.config?.skipAuthRedirect && unauthorizedHandler) {
      unauthorizedHandler(error)
    }
    return Promise.reject(error)
  }
)

export default http
