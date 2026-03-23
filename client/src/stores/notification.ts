import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationsApi } from '../api/notifications'
import type { Notification } from '../types'

const POLL_INTERVAL_MS = 30_000

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)
  let pollTimer: ReturnType<typeof setInterval> | null = null

  async function fetchNotifications() {
    loading.value = true
    try {
      const { data } = await notificationsApi.getAll()
      notifications.value = data
    } catch {
      notifications.value = []
    } finally {
      loading.value = false
    }
  }

  async function fetchUnreadCount() {
    try {
      const { data } = await notificationsApi.getUnreadCount()
      unreadCount.value = data.count
    } catch {}
  }

  async function markAsRead(id: number) {
    const { data } = await notificationsApi.markAsRead(id)
    const idx = notifications.value.findIndex(n => n.id === id)
    if (idx !== -1) {
      notifications.value[idx] = data
      if (unreadCount.value > 0) unreadCount.value--
    }
  }

  async function markAllAsRead() {
    const prev = notifications.value.map(n => ({ ...n }))
    const prevCount = unreadCount.value
    notifications.value = notifications.value.map(n => ({ ...n, isRead: true }))
    unreadCount.value = 0
    try {
      await notificationsApi.markAllAsRead()
    } catch {
      notifications.value = prev
      unreadCount.value = prevCount
    }
  }

  function startPolling() {
    fetchUnreadCount()
    if (pollTimer) return
    pollTimer = setInterval(fetchUnreadCount, POLL_INTERVAL_MS)
  }

  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
  }

  return {
    notifications,
    unreadCount,
    loading,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    markAllAsRead,
    startPolling,
    stopPolling
  }
})
