import {getCurrentInstance, ref} from "vue"
import { useRouter } from 'vue-router'

const loading = ref(false)

export function useHttpRequest() {
    const instance = getCurrentInstance()
    const $http = instance.appContext.config.globalProperties.$http
    const router = useRouter()

    const requestGet = (url, callback, errorCallback) => {
        loading.value = true
        checkSession(
            () => {
                $http.get(url).then(response => callback(response))
                    .catch(error => {
                        if(errorCallback){errorCallback(error) }
                    })
                    .finally(() => loading.value = false)
            },
            (sessionError) => {
                // セッション無効時の処理
                if (errorCallback) {
                    errorCallback(sessionError)
                } else {
                    router.push({path: '/login'})
                }
                loading.value = false;
            }
        )
    }

    const requestPost = (url, data, callback, errorCallback) => {
        loading.value = true
        checkSession(
            () => {
                $http.post(url, data).then(response => callback(response))
                    .catch(error => {
                        if(errorCallback){errorCallback(error) }
                    })
                    .finally(() => loading.value = false)
            },
            (sessionError) => {
                // セッション無効時の処理
                if (errorCallback) {
                    errorCallback(sessionError)
                } else {
                    router.push({path: '/login'})
                }
                loading.value = false;
            }
        )
    }

    const checkSession = (onSuccess, onFailure) => {
        $http
            .get('/checkSession')
            .then((response) => {
                console.log('**********')
                console.log(response)
                console.log('**********')
                if (onSuccess) onSuccess()
            })
            .catch((error) => {
                console.log('**********')
                console.error(error)
                console.log('**********')
                if (onFailure) onFailure(error)
            })
    }

    return {
        requestGet,
        requestPost,
        loading
    }
}
