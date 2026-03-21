<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Уведомления</h1>
      <Button
        v-if="store.unreadCount > 0"
        label="Прочитать все"
        icon="pi pi-check-circle"
        severity="secondary"
        variant="outlined"
        :loading="markingAll"
        @click="handleMarkAll"
      />
    </div>

    <div v-if="store.loading" class="flex justify-center py-16">
      <ProgressSpinner style="width: 2rem; height: 2rem" />
    </div>

    <div v-else-if="store.notifications.length === 0" class="text-center text-surface-400 py-16">
      <i class="pi pi-bell text-4xl mb-4 block" />
      <p>Нет уведомлений</p>
    </div>

    <div v-else class="flex flex-col gap-2">
      <div
        v-for="n in paginated"
        :key="n.id"
        class="flex items-start gap-4 p-4 rounded-lg border bg-white hover:shadow-sm transition-all cursor-pointer"
        :class="{ 'border-primary-200 bg-primary-50': !n.isRead, 'border-surface-100': n.isRead }"
        @click="handleRead(n)"
      >
        <div class="flex-shrink-0 mt-1">
          <div
            class="w-9 h-9 rounded-full flex items-center justify-center"
            :class="n.isRead ? 'bg-surface-100' : 'bg-primary-100'"
          >
            <i
              class="pi text-sm"
              :class="[iconForNotification(n), n.isRead ? 'text-surface-400' : 'text-primary-600']"
            />
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <div class="flex items-start justify-between gap-2">
            <span class="font-semibold text-surface-800" :class="{ 'font-bold': !n.isRead }">
              {{ n.subject }}
            </span>
            <span class="text-xs text-surface-400 flex-shrink-0">{{ formatDate(n.scheduledAt) }}</span>
          </div>
          <p v-if="n.messageText" class="text-sm text-surface-600 mt-1">{{ n.messageText }}</p>
        </div>
        <div v-if="!n.isRead" class="w-2 h-2 rounded-full bg-primary-500 mt-2 flex-shrink-0" />
      </div>

      <Paginator
        v-if="store.notifications.length > pageSize"
        :rows="pageSize"
        :totalRecords="store.notifications.length"
        :first="(page - 1) * pageSize"
        class="mt-4"
        @page="onPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import Paginator from 'primevue/paginator'
import type { PageState } from 'primevue/paginator'
import { useNotificationStore } from '../stores/notification'
import type { Notification } from '../types'

const store = useNotificationStore()
const markingAll = ref(false)
const page = ref(1)
const pageSize = 20

const paginated = computed(() => {
  const start = (page.value - 1) * pageSize
  return store.notifications.slice(start, start + pageSize)
})

function onPage(event: PageState) {
  page.value = event.page + 1
}

function iconForNotification(n: Notification): string {
  if (n.eventId != null) return 'pi-calendar'
  if (n.clubId != null) return 'pi-users'
  return 'pi-bell'
}

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
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  try {
    await store.fetchNotifications()
  } catch {}
})
</script>
