<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Мероприятия</h1>
      <Button
        v-if="authStore.isAdmin || authStore.isManager"
        label="Создать"
        icon="pi pi-plus"
        @click="router.push({ name: 'event-create' })"
      />
    </div>

    <div class="mb-4">
      <Select
        v-model="statusFilter"
        :options="statusOptions"
        optionLabel="label"
        optionValue="value"
        placeholder="Фильтр по статусу"
        showClear
        class="w-64"
      />
    </div>

    <ProgressSpinner v-if="eventStore.loading" class="flex justify-center" />

    <div v-else-if="eventStore.events.length === 0" class="text-center text-gray-500 py-8">
      Мероприятия не найдены
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <Card v-for="event in eventStore.events" :key="event.id" class="cursor-pointer hover:shadow-lg transition-shadow">
        <template #title>
          <span class="text-lg">{{ event.title }}</span>
        </template>
        <template #subtitle>
          <span v-if="event.clubName">{{ event.clubName }}</span>
        </template>
        <template #content>
          <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
              <i class="pi pi-calendar text-gray-500" />
              <span>{{ formatDate(event.startAt) }} — {{ formatDate(event.endAt) }}</span>
            </div>
            <div class="flex items-center gap-2">
              <Tag :value="statusLabel(event.status)" :severity="statusSeverity(event.status)" />
            </div>
            <div v-if="event.participantLimit != null" class="flex items-center gap-2">
              <i class="pi pi-users text-gray-500" />
              <span>Лимит: {{ event.participantLimit }}</span>
            </div>
            <div v-if="event.price != null" class="flex items-center gap-2">
              <i class="pi pi-wallet text-gray-500" />
              <span>{{ event.price }} ₽</span>
            </div>
          </div>
        </template>
        <template #footer>
          <Button
            label="Подробнее"
            severity="info"
            variant="outlined"
            size="small"
            @click="router.push({ name: 'event-detail', params: { id: event.id } })"
          />
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useEventStore } from '../stores/event'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const eventStore = useEventStore()
const authStore = useAuthStore()

const statusFilter = ref<'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED' | null>(null)

const statusOptions = [
  { label: 'Опубликовано', value: 'PUBLISHED' },
  { label: 'Черновик', value: 'DRAFT' },
  { label: 'Отменено', value: 'CANCELLED' },
  { label: 'Завершено', value: 'COMPLETED' }
]

function statusSeverity(status: string) {
  const map: Record<string, string> = {
    PUBLISHED: 'success',
    CANCELLED: 'danger',
    COMPLETED: 'info',
    DRAFT: 'warn'
  }
  return map[status] ?? 'secondary'
}

function statusLabel(status: string) {
  const map: Record<string, string> = {
    PUBLISHED: 'Опубликовано',
    CANCELLED: 'Отменено',
    COMPLETED: 'Завершено',
    DRAFT: 'Черновик'
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

async function loadEvents() {
  const params = statusFilter.value ? { status: statusFilter.value } : undefined
  try {
    await eventStore.fetchEvents(params)
  } catch {}
}

watch(statusFilter, loadEvents)

onMounted(loadEvents)
</script>
