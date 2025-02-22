import {getCurrentInstance, ref} from "vue";

const loading = ref(false)

export function useHttpRequest() {
    const instance = getCurrentInstance()
    const $http = instance.appContext.config.globalProperties.$http

    const requestGet = (url, callback) => {
        loading.value = true
        $http.get(url).then(response => callback(response)).finally(() => loading.value = false)
    }

    const requestPost = (url, data, callback) => {
        loading.value = true
        $http.post(url, data).then(response => callback(response)).finally(() => loading.value = false)
    }

    return {
        requestGet,
        requestPost,
        loading
    }
}
