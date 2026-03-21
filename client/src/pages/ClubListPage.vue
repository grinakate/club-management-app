<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Клубы</h1>
      <Button
        v-if="authStore.isAdmin || authStore.isManager"
        label="Создать клуб"
        icon="pi pi-plus"
        @click="router.push({ name: 'club-create' })"
      />
    </div>

    <div class="flex flex-wrap gap-4 mb-6">
      <Select
        v-model="selectedCategory"
        :options="categoryOptions"
        option-label="name"
        option-value="id"
        placeholder="Все категории"
        show-clear
        class="w-64"
      />
      <InputText
        v-model="searchQuery"
        placeholder="Поиск по названию..."
        class="w-64"
      />
    </div>

    <ProgressSpinner v-if="clubStore.loading" class="flex justify-center" />

    <div v-else-if="filteredClubs.length === 0" class="text-center text-gray-500 py-12">
      Клубы не найдены
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <Card v-for="club in filteredClubs" :key="club.id" class="shadow-md hover:shadow-lg transition-shadow">
        <template #title>
          <div class="flex items-center gap-2">
            <span>{{ club.name }}</span>
            <Tag v-if="club.categoryName" :value="club.categoryName" severity="info" />
          </div>
        </template>
        <template #content>
          <p class="text-gray-600 line-clamp-3 mb-3">
            {{ club.description || 'Описание отсутствует' }}
          </p>
          <div v-if="club.membershipFee != null" class="text-sm font-semibold text-primary mb-3">
            Взнос: {{ club.membershipFee }} ₽
          </div>
        </template>
        <template #footer>
          <Button
            label="Подробнее"
            icon="pi pi-arrow-right"
            icon-pos="right"
            variant="outlined"
            @click="router.push({ name: 'club-detail', params: { id: club.id } })"
          />
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Select from 'primevue/select'
import InputText from 'primevue/inputtext'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useClubStore } from '../stores/club'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const clubStore = useClubStore()
const authStore = useAuthStore()

const selectedCategory = ref<number | null>(null)
const searchQuery = ref('')

const categoryOptions = computed(() => clubStore.categories)

const filteredClubs = computed(() => {
  let result = clubStore.clubs
  if (selectedCategory.value) {
    result = result.filter(c => c.categoryId === selectedCategory.value)
  }
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(c => c.name.toLowerCase().includes(q))
  }
  return result
})

onMounted(async () => {
  try {
    await Promise.all([
      clubStore.fetchClubs(),
      clubStore.fetchCategories()
    ])
  } catch {}
})
</script>
