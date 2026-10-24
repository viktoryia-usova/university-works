import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import { queryClient } from './queryClient'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// Attach Bearer token to every request
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token?.accessToken
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// On 401 — clear auth and redirect to sign-in.
// Exceptions: auth endpoints and account/password endpoint handle 401 themselves.
const SKIP_REDIRECT_URLS = ['/auth/', '/account/']

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      const url: string = err.config?.url ?? ''
      const skipRedirect = SKIP_REDIRECT_URLS.some(pattern => url.includes(pattern))
      if (!skipRedirect) {
        queryClient.clear()
        useAuthStore.getState().clearToken()
        window.location.href = '/sign-in'
      }
    }
    return Promise.reject(err)
  }
)

export default api
