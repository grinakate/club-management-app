import axios from 'axios'
import router from '../router'

const http = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')

      const { useAuthStore } = await import('../stores/auth')
      const authStore = useAuthStore()
      authStore.$patch({ token: null, user: null })

      router.push({ name: 'login' })
    }
    return Promise.reject(error)
  }
)

export default http
