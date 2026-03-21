<template>
  <div>
    <ProgressSpinner v-if="loading" style="width: 2rem; height: 2rem" />
    <template v-else>
      <Button
        v-if="!myRegistration"
        label="Записаться"
        severity="success"
        :disabled="props.eventStatus !== 'PUBLISHED'"
        @click="handleRegister"
      />
      <Button
        v-else-if="myRegistration.status === 'REGISTERED'"
        label="Вы записаны. Отменить запись?"
        severity="danger"
        variant="outlined"
        @click="handleCancel"
      />
      <Button
        v-else-if="myRegistration.status === 'WAITLIST'"
        label="Вы в листе ожидания. Отменить?"
        severity="warn"
        variant="outlined"
        @click="handleCancel"
      />
      <Button
        v-else-if="myRegistration.status === 'ATTENDED'"
        label="Вы посетили мероприятие"
        severity="info"
        disabled
      />
      <Button
        v-else-if="myRegistration.status === 'CANCELLED'"
        label="Записаться"
        severity="success"
        :disabled="props.eventStatus !== 'PUBLISHED'"
        @click="handleRegister"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import { useToast } from 'primevue/usetoast'
import { registrationsApi } from '../api/registrations'
import type { EventRegistration } from '../types'

const props = defineProps<{
  eventId: number
  eventStatus: string
}>()

const emit = defineEmits<{
  (e: 'updated'): void
}>()

const toast = useToast()
const myRegistration = ref<EventRegistration | null>(null)
const loading = ref(false)

async function loadMyRegistration() {
  loading.value = true
  try {
    const { data } = await registrationsApi.getMyRegistrations()
    myRegistration.value =
      data.find(
        (r) => r.eventId === props.eventId && r.status !== 'CANCELLED'
      ) ?? null
  } catch (err: unknown) {
    myRegistration.value = null
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: axiosErr.response?.data?.message || 'Не удалось загрузить данные', life: 4000 })
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  try {
    const { data } = await registrationsApi.register(props.eventId)
    myRegistration.value = data
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Вы записаны на мероприятие', life: 3000 })
    emit('updated')
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось записаться',
      life: 5000
    })
  }
}

async function handleCancel() {
  try {
    await registrationsApi.cancel(props.eventId)
    myRegistration.value = null
    toast.add({ severity: 'info', summary: 'Отменено', detail: 'Запись отменена', life: 3000 })
    emit('updated')
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось отменить запись',
      life: 5000
    })
  }
}

onMounted(loadMyRegistration)
</script>
