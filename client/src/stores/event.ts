import { defineStore } from 'pinia'
import { ref } from 'vue'
import { eventsApi } from '../api/events'
import type { EventCreatePayload, EventUpdatePayload } from '../api/events'
import type { Event } from '../types'

export const useEventStore = defineStore('event', () => {
  const events = ref<Event[]>([])
  const currentEvent = ref<Event | null>(null)
  const loading = ref(false)

  async function fetchEvents(params?: { clubId?: number; status?: 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED' }) {
    loading.value = true
    try {
      const { data } = await eventsApi.getAll(params)
      events.value = data
    } finally {
      loading.value = false
    }
  }

  async function fetchEventById(id: number) {
    loading.value = true
    try {
      const { data } = await eventsApi.getById(id)
      currentEvent.value = data
    } finally {
      loading.value = false
    }
  }

  async function createEvent(data: EventCreatePayload) {
    const { data: created } = await eventsApi.create(data)
    events.value.push(created)
    return created
  }

  async function updateEvent(id: number, data: EventUpdatePayload) {
    const { data: updated } = await eventsApi.update(id, data)
    const idx = events.value.findIndex((e) => e.id === id)
    if (idx !== -1) events.value[idx] = updated
    if (currentEvent.value?.id === id) currentEvent.value = updated
    return updated
  }

  async function deleteEvent(id: number) {
    await eventsApi.delete(id)
    events.value = events.value.filter((e) => e.id !== id)
    if (currentEvent.value?.id === id) currentEvent.value = null
  }

  return {
    events,
    currentEvent,
    loading,
    fetchEvents,
    fetchEventById,
    createEvent,
    updateEvent,
    deleteEvent
  }
})
