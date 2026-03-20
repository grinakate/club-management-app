import { defineStore } from 'pinia'
import { ref } from 'vue'
import { registrationsApi } from '../api/registrations'
import type { EventRegistration } from '../types'

export const useRegistrationStore = defineStore('registration', () => {
  const registrations = ref<EventRegistration[]>([])
  const myRegistrations = ref<EventRegistration[]>([])
  const loading = ref(false)

  async function registerForEvent(eventId: number) {
    const { data } = await registrationsApi.register(eventId)
    registrations.value.push(data)
    return data
  }

  async function cancelRegistration(eventId: number) {
    await registrationsApi.cancel(eventId)
    registrations.value = registrations.value.filter(
      (r) => !(r.eventId === eventId && (r.status === 'REGISTERED' || r.status === 'WAITLIST'))
    )
  }

  async function fetchEventRegistrations(eventId: number) {
    loading.value = true
    try {
      const { data } = await registrationsApi.getByEvent(eventId)
      registrations.value = data
    } finally {
      loading.value = false
    }
  }

  async function markAttendance(registrationId: number) {
    await registrationsApi.markAttendance(registrationId)
    const reg = registrations.value.find((r) => r.id === registrationId)
    if (reg) {
      reg.attendanceMark = true
      reg.status = 'ATTENDED'
    }
  }

  async function fetchMyRegistrations() {
    loading.value = true
    try {
      const { data } = await registrationsApi.getMyRegistrations()
      myRegistrations.value = data
    } finally {
      loading.value = false
    }
  }

  return {
    registrations,
    myRegistrations,
    loading,
    registerForEvent,
    cancelRegistration,
    fetchEventRegistrations,
    markAttendance,
    fetchMyRegistrations
  }
})
