<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">Мои мероприятия</h1>

    <ProgressSpinner v-if="registrationStore.loading" class="flex justify-center" />

    <div v-else-if="registrationStore.myRegistrations.length === 0" class="text-center text-gray-500 py-8">
      Вы пока не записаны ни на одно мероприятие
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card v-for="reg in registrationStore.myRegistrations" :key="reg.id">
        <template #title>
          <span class="text-lg">{{ reg.eventTitle }}</span>
        </template>
        <template #content>
          <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
              <i class="pi pi-calendar text-gray-500" />
              <span>{{ formatDate(reg.registeredAt) }}</span>
            </div>
            <div>
              <Tag :value="regStatusLabel(reg.status)" :severity="regStatusSeverity(reg.status)" />
            </div>
          </div>
        </template>
        <template #footer>
          <Button
            label="Подробнее"
            severity="info"
            variant="outlined"
            size="small"
            @click="router.push({ name: 'event-detail', params: { id: reg.eventId } })"
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
import { useRegistrationStore } from '../stores/registration'

const router = useRouter()
const registrationStore = useRegistrationStore()

function regStatusSeverity(status: string) {
  const map: Record<string, string> = {
    REGISTERED: 'success',
    WAITLIST: 'warn',
    CANCELLED: 'danger',
    ATTENDED: 'info'
  }
  return map[status] ?? 'secondary'
}

function regStatusLabel(status: string) {
  const map: Record<string, string> = {
    REGISTERED: 'Зарегистрирован',
    WAITLIST: 'Лист ожидания',
    CANCELLED: 'Отменено',
    ATTENDED: 'Посетил'
  }
  return map[status] ?? status
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  registrationStore.fetchMyRegistrations()
})
</script>
