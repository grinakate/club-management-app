import { defineStore } from 'pinia'
import { ref } from 'vue'
import { clubsApi } from '../api/clubs'
import type { Club, Category } from '../types'

export const useClubStore = defineStore('club', () => {
  const clubs = ref<Club[]>([])
  const categories = ref<Category[]>([])
  const currentClub = ref<Club | null>(null)
  const loading = ref(false)

  async function fetchClubs(params?: { categoryId?: number; status?: string }) {
    loading.value = true
    try {
      const { data } = await clubsApi.getAll(params)
      clubs.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchClubById(id: number) {
    loading.value = true
    try {
      const { data } = await clubsApi.getById(id)
      currentClub.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchCategories() {
    const { data } = await clubsApi.getCategories()
    categories.value = data
  }

  async function createClub(data: Partial<Club>): Promise<Club> {
    const { data: created } = await clubsApi.create(data)
    return created
  }

  async function updateClub(id: number, data: Partial<Club>): Promise<Club> {
    const { data: updated } = await clubsApi.update(id, data)
    if (currentClub.value?.id === id) {
      currentClub.value = updated
    }
    return updated
  }

  async function deleteClub(id: number) {
    await clubsApi.delete(id)
    clubs.value = clubs.value.filter(c => c.id !== id)
    if (currentClub.value?.id === id) {
      currentClub.value = null
    }
  }

  async function fetchMyClubs() {
    loading.value = true
    try {
      const { data } = await clubsApi.getMyClubs()
      clubs.value = data
    } finally {
      loading.value = false
    }
  }

  return {
    clubs,
    categories,
    currentClub,
    loading,
    fetchClubs,
    fetchClubById,
    fetchCategories,
    createClub,
    updateClub,
    deleteClub,
    fetchMyClubs
  }
})
