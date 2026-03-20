import { defineStore } from 'pinia'
import { ref } from 'vue'
import { usersApi } from '../api/users'
import type { User } from '../types'

export const useUserStore = defineStore('user', () => {
  const profile = ref<User | null>(null)
  const loading = ref(false)

  async function fetchProfile() {
    loading.value = true
    try {
      const res = await usersApi.getMe()
      profile.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function updateProfile(data: Partial<User>) {
    if (!profile.value) return
    await usersApi.updateProfile(profile.value.id, data)
    await fetchProfile()
  }

  async function changePassword(oldPassword: string, newPassword: string) {
    await usersApi.changePassword({ oldPassword, newPassword })
  }

  return { profile, loading, fetchProfile, updateProfile, changePassword }
})
