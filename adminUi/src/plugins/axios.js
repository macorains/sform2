import axios from 'axios'

const http = axios.create({
  // baseURL: process.env.VUE_APP_API_URL,
  baseURL: 'http://localhost:9001',
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
