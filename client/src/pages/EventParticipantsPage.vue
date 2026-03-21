<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Участники мероприятия</h1>
      <Button
        label="Назад к мероприятию"
        icon="pi pi-arrow-left"
        severity="secondary"
        variant="outlined"
        @click="router.push({ name: 'event-detail', params: { id: eventId } })"
      />
    </div>

    <ProgressSpinner v-if="registrationStore.loading" class="flex justify-center" />

    <DataTable
      v-else
      :value="registrationStore.registrations"
      stripedRows
      paginator
      :rows="20"
      tableStyle="min-width: 50rem"
    >
      <template #empty>
        <div class="text-center text-gray-500 py-4">Участников пока нет</div>
      </template>

      <Column field="userFullName" header="Участник" sortable />
      <Column field="registeredAt" header="Дата регистрации" sortable>
        <template #body="{ data }">
          {{ formatDate(data.registeredAt) }}
        </template>
      </Column>
      <Column field="status" header="Статус" sortable>
        <template #body="{ data }">
          <Tag :value="regStatusLabel(data.status)" :severity="regStatusSeverity(data.status)" />
        </template>
      </Column>
      <Column header="Посещение">
        <template #body="{ data }">
          <Checkbox
            :modelValue="data.attendanceMark || data.status === 'ATTENDED'"
            :binary="true"
            :disabled="data.status === 'CANCELLED' || data.status === 'ATTENDED'"
            @update:modelValue="handleAttendance(data)"
          />
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import Checkbox from 'primevue/checkbox'
import ProgressSpinner from 'primevue/progressspinner'
import { useToast } from 'primevue/usetoast'
import { useRegistrationStore } from '../stores/registration'
import type { EventRegistration } from '../types'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const registrationStore = useRegistrationStore()

const eventId = computed(() => Number(route.params.id))

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

async function handleAttendance(registration: EventRegistration) {
  try {
    await registrationStore.markAttendance(registration.id)
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Посещение отмечено', life: 3000 })
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось отметить посещение',
      life: 5000
    })
  }
}

onMounted(async () => {
  try {
    await registrationStore.fetchEventRegistrations(eventId.value)
  } catch {}
})
</script>
