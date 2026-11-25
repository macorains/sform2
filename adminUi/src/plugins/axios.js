import axios from 'axios'

const TOKEN_KEY = 'sformToken'

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

const readToken = () => {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

http.hasToken = () => !!readToken()

http.setToken = (token) => {
  try {
    localStorage.setItem(TOKEN_KEY, token)
  } catch {
    // localStorage が使えない環境では保存できない（未ログイン扱いになる）
  }
}

/**
 * ログイン直後、API から URL フラグメント(#sform_token=...)で渡されたトークンを保存し、
 * URL からは即座に取り除く。ルーターの初回遷移より前に呼ぶこと。
 */
http.consumeTokenFromUrl = () => {
  const hash = window.location.hash
  if (!hash.startsWith('#')) return
  const params = new URLSearchParams(hash.slice(1))
  const token = params.get('sform_token')
  if (!token) return
  http.setToken(token)
  params.delete('sform_token')
  const rest = params.toString()
  window.history.replaceState(window.history.state, '', window.location.pathname + window.location.search + (rest ? '#' + rest : ''))
}

// トークンを削除する関数
http.clearToken = () => {
  try {
    localStorage.removeItem(TOKEN_KEY)
  } catch {
    // localStorage が使えない環境では何もしない
  }
}

// 認証切れ(401)時の処理。ルーター/ストアへの依存を避けるため main.js から登録する
let unauthorizedHandler = null
http.setUnauthorizedHandler = (handler) => {
  unauthorizedHandler = handler
}

// トークンの付与はここ1か所で行う（HeaderClient は生のJWTを期待する）
http.interceptors.request.use(config => {
  const token = readToken()
  if (token) {
    config.headers['X-Auth-Token'] = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

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
