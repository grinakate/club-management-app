<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">Мои клубы</h1>

    <ProgressSpinner v-if="clubStore.loading" class="flex justify-center" />

    <div v-else-if="clubStore.clubs.length === 0" class="text-center text-gray-500 py-12">
      <i class="pi pi-users text-4xl mb-4 block" />
      <p class="text-lg">Вы пока не состоите ни в одном клубе</p>
      <Button
        label="Посмотреть клубы"
        class="mt-4"
        variant="outlined"
        @click="router.push({ name: 'clubs' })"
      />
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <Card v-for="club in clubStore.clubs" :key="club.id" class="shadow-md hover:shadow-lg transition-shadow">
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
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useClubStore } from '../stores/club'

const router = useRouter()
const clubStore = useClubStore()

onMounted(async () => {
  try {
    await clubStore.fetchMyClubs()
  } catch {}
})
</script>
