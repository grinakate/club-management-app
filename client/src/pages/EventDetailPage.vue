<template>
  <div>
    <ProgressSpinner v-if="eventStore.loading" class="flex justify-center" />

    <div v-else-if="event">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold">{{ event.title }}</h1>
        <Tag :value="statusLabel(event.status)" :severity="statusSeverity(event.status)" />
      </div>

      <Card>
        <template #content>
          <div class="flex flex-col gap-4">
            <div v-if="event.description" class="text-gray-700">
              {{ event.description }}
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex items-center gap-2">
                <i class="pi pi-building text-gray-500" />
                <span class="font-medium">Клуб:</span>
                <router-link
                  v-if="event.clubId"
                  :to="{ name: 'club-detail', params: { id: event.clubId } }"
                  class="text-blue-600 hover:underline"
                >
                  {{ event.clubName || `Клуб #${event.clubId}` }}
                </router-link>
              </div>

              <div class="flex items-center gap-2">
                <i class="pi pi-calendar text-gray-500" />
                <span class="font-medium">Начало:</span>
                <span>{{ formatDate(event.startAt) }}</span>
              </div>

              <div class="flex items-center gap-2">
                <i class="pi pi-calendar-times text-gray-500" />
                <span class="font-medium">Окончание:</span>
                <span>{{ formatDate(event.endAt) }}</span>
              </div>

              <div v-if="event.participantLimit" class="flex items-center gap-2">
                <i class="pi pi-users text-gray-500" />
                <span class="font-medium">Лимит участников:</span>
                <span>{{ event.participantLimit }}</span>
              </div>

              <div class="flex items-center gap-2">
                <i class="pi pi-wallet text-gray-500" />
                <span class="font-medium">Цена:</span>
                <span>{{ event.price ? `${event.price} ₽` : 'Бесплатно' }}</span>
              </div>
            </div>

            <div class="mt-4">
              <RegistrationButton
                :event-id="event.id"
                :event-status="event.status"
                :participant-limit="event.participantLimit"
              />
            </div>

            <div v-if="isOwner" class="flex gap-2 mt-4 pt-4 border-t">
              <Button
                label="Редактировать"
                icon="pi pi-pencil"
                severity="info"
                variant="outlined"
              />
              <Button
                label="Отменить мероприятие"
                icon="pi pi-times"
                severity="danger"
                variant="outlined"
                :disabled="event.status === 'CANCELLED'"
                @click="handleCancelEvent"
              />
              <Button
                label="Участники"
                icon="pi pi-users"
                severity="secondary"
                variant="outlined"
                @click="router.push({ name: 'event-participants', params: { id: event.id } })"
              />
            </div>
          </div>
        </template>
      </Card>
    </div>

    <div v-else class="text-center text-gray-500 py-8">
      Мероприятие не найдено
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useToast } from 'primevue/usetoast'
import { useEventStore } from '../stores/event'
import { useAuthStore } from '../stores/auth'
import RegistrationButton from '../components/RegistrationButton.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const eventStore = useEventStore()
const authStore = useAuthStore()

const event = computed(() => eventStore.currentEvent)
const isOwner = computed(() => event.value?.createdBy === authStore.user?.id)

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

async function handleCancelEvent() {
  if (!event.value) return
  try {
    await eventStore.updateEvent(event.value.id, { status: 'CANCELLED' })
    toast.add({ severity: 'info', summary: 'Отменено', detail: 'Мероприятие отменено', life: 3000 })
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось отменить мероприятие',
      life: 5000
    })
  }
}

onMounted(() => {
  const id = Number(route.params.id)
  eventStore.fetchEventById(id)
})
</script>
