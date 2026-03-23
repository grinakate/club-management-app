import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'
import type { User, LoginRequest, RegisterRequest } from '../types'
import router from '../router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isManager = computed(() => user.value?.role === 'MANAGER')

  async function login(data: LoginRequest) {
    const res = await authApi.login(data)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUser()
    router.push('/')
  }

  async function register(data: RegisterRequest) {
    await authApi.register(data)
    await login({ login: data.email || data.phone || '', password: data.password })
  }

  async function fetchUser() {
    if (!token.value) return
    try {
      const res = await authApi.me()
      user.value = res.data
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push({ name: 'login' })
  }

  return { token, user, isAuthenticated, isAdmin, isManager, login, register, fetchUser, logout }
})
