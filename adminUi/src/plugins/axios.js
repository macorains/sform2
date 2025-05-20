import axios from 'axios'

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

const token = localStorage.getItem('sformToken');
if (token) {
    http.defaults.headers.common['X-Auth-Token'] = `Bearer ${token}`;
}

http.setToken = (newToken) => {
    localStorage.setItem('sformToken', newToken);
    http.defaults.headers.common['X-Auth-Token'] = newToken;
}

// トークンを削除する関数
http.clearToken = () => {
    localStorage.removeItem('sformToken');
    delete http.defaults.headers.common['X-Auth-Token'];
}

http.interceptors.request.use(config => {
    const token = localStorage.getItem('sformToken');
    if (token) {
        config.headers['X-Auth-Token'] = token;
    }
    return config;
}, error => {
    return Promise.reject(error);
})
http.interceptors.response.use(
    function (response)
    {
      return response
    },
    function (error) {
      return Promise.reject(error)
    }
)

export default http
