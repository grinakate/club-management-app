<template>
  <div class="flex items-start justify-between p-4 rounded-lg border bg-white hover:shadow-sm transition-shadow">
    <div class="flex flex-col gap-1">
      <span class="font-semibold text-surface-800">{{ application.userFullName }}</span>
      <span class="text-sm text-surface-500">{{ formatDate(application.appliedAt) }}</span>
      <span v-if="application.comment" class="text-sm text-surface-600 italic">{{ application.comment }}</span>
    </div>
    <div class="flex flex-col items-end gap-2">
      <Tag :value="statusLabel(application.status)" :severity="statusSeverity(application.status)" />
      <div v-if="application.status === 'NEW' && canReview" class="flex gap-2">
        <Button
          label="Одобрить"
          severity="success"
          size="small"
          :loading="loading"
          @click="emit('approve', application.id)"
        />
        <Button
          label="Отклонить"
          severity="danger"
          variant="outlined"
          size="small"
          :loading="loading"
          @click="emit('reject', application.id)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import type { MembershipApplication } from '../types'

defineProps<{
  application: MembershipApplication
  canReview?: boolean
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'approve', id: number): void
  (e: 'reject', id: number): void
}>()

function statusLabel(status: MembershipApplication['status']) {
  const map: Record<string, string> = {
    NEW: 'На рассмотрении',
    APPROVED: 'Одобрена',
    REJECTED: 'Отклонена',
    CANCELLED: 'Отменена'
  }
  return map[status] ?? status
}

function statusSeverity(status: MembershipApplication['status']) {
  const map: Record<string, string> = {
    NEW: 'warn',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELLED: 'secondary'
  }
  return map[status] ?? 'secondary'
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>
