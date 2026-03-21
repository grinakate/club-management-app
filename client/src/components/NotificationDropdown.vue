<template>
  <div class="w-80 bg-white rounded-xl shadow-lg border border-surface-200 overflow-hidden">
    <div class="flex items-center justify-between px-4 py-3 border-b border-surface-100">
      <span class="font-semibold text-surface-800">Уведомления</span>
      <Button
        v-if="store.unreadCount > 0"
        label="Прочитать все"
        variant="text"
        size="small"
        severity="secondary"
        :loading="markingAll"
        @click="handleMarkAll"
      />
    </div>

    <div v-if="store.loading" class="flex justify-center py-6">
      <ProgressSpinner style="width: 1.5rem; height: 1.5rem" />
    </div>

    <div v-else-if="recent.length === 0" class="text-center text-surface-400 py-8 text-sm">
      Нет уведомлений
    </div>

    <div v-else>
      <div
        v-for="n in recent"
        :key="n.id"
        class="flex items-start gap-3 px-4 py-3 hover:bg-surface-50 transition-colors cursor-pointer border-b border-surface-50 last:border-0"
        :class="{ 'bg-primary-50': !n.isRead }"
        @click="handleRead(n)"
      >
        <div class="mt-0.5 flex-shrink-0">
          <div v-if="!n.isRead" class="w-2 h-2 rounded-full bg-primary-500 mt-1" />
          <div v-else class="w-2 h-2 rounded-full bg-surface-200 mt-1" />
        </div>
        <div class="flex-1 min-w-0">
          <div class="font-medium text-sm text-surface-800 truncate">{{ n.subject }}</div>
          <div v-if="n.messageText" class="text-xs text-surface-500 mt-0.5 line-clamp-2">{{ n.messageText }}</div>
          <div class="text-xs text-surface-400 mt-1">{{ formatDate(n.scheduledAt) }}</div>
        </div>
      </div>
    </div>

    <div class="px-4 py-2 border-t border-surface-100">
      <Button
        label="Все уведомления"
        variant="text"
        size="small"
        class="w-full"
        @click="emit('navigate')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import { useNotificationStore } from '../stores/notification'
import type { Notification } from '../types'

const emit = defineEmits<{ navigate: [] }>()

const store = useNotificationStore()
const markingAll = ref(false)

const recent = computed(() => store.notifications.slice(0, 5))

async function handleRead(n: Notification) {
  if (!n.isRead) {
    try {
      await store.markAsRead(n.id)
    } catch {}
  }
}

async function handleMarkAll() {
  markingAll.value = true
  try {
    await store.markAllAsRead()
  } finally {
    markingAll.value = false
  }
}

function formatDate(iso: string) {
  const date = new Date(iso)
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMin = Math.floor(diffMs / 60000)
  if (diffMin < 1) return 'только что'
  if (diffMin < 60) return `${diffMin} мин. назад`
  const diffH = Math.floor(diffMin / 60)
  if (diffH < 24) return `${diffH} ч. назад`
  return date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })
}
</script>
