import { computed, getCurrentInstance, inject, ref } from "vue"

const toMessage = (error) => {
    const response = error.response
    if (!response) return '通信エラーが発生しました'
    const data = response.data
    if (data?.message) return data.message
    // Play のエラーページ等の HTML はそのまま表示しない
    if (typeof data === 'string' && data && !data.trimStart().startsWith('<')) return data
    return `エラーが発生しました (HTTP ${response.status})`
}

/**
 * API 呼び出し用 composable。
 * - 認証切れ(401)は axios の interceptor がログイン画面へ遷移させるので、ここでは何もしない
 * - errorCallback を省略した場合はエラーモーダルで通知する
 * - loading はこの composable を使うコンポーネントごとに、実行中リクエストがある間 true
 */
export function useHttpRequest() {
    const instance = getCurrentInstance()
    const $http = instance.appContext.config.globalProperties.$http
    const showError = inject("showError", null)

    const pendingCount = ref(0)
    const loading = computed(() => pendingCount.value > 0)

    const run = (request, callback, errorCallback) => {
        pendingCount.value++
        return request
            .then(response => {
                if (callback) callback(response)
            })
            .catch(error => {
                if (error.response?.status === 401) return
                if (errorCallback) {
                    errorCallback(error)
                } else if (showError) {
                    showError(toMessage(error))
                } else {
                    console.error(error)
                }
            })
            .finally(() => {
                pendingCount.value--
            })
    }

    const requestGet = (url, callback, errorCallback) =>
        run($http.get(url), callback, errorCallback)

    const requestPost = (url, data, callback, errorCallback) =>
        run($http.post(url, data), callback, errorCallback)

    const requestDelete = (url, callback, errorCallback) =>
        run($http.delete(url), callback, errorCallback)

    return {
        requestGet,
        requestPost,
        requestDelete,
        loading
    }
}
