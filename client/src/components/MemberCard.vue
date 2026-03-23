<template>
  <div class="flex items-center justify-between p-4 rounded-lg border bg-white hover:shadow-sm transition-shadow">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-sm">
        {{ initials(member.userFullName) }}
      </div>
      <div class="flex flex-col gap-0.5">
        <span class="font-semibold text-surface-800">{{ member.userFullName }}</span>
        <span class="text-xs text-surface-500">С {{ formatDate(member.joinedAt) }}</span>
      </div>
    </div>
    <div class="flex items-center gap-2">
      <Tag :value="roleLabel(member.memberRole)" :severity="roleSeverity(member.memberRole)" />
      <div v-if="canManage" class="flex gap-1">
        <Select
          v-if="editingId === member.id"
          v-model="selectedRole"
          :options="roleOptions"
          optionLabel="label"
          optionValue="value"
          size="small"
          class="w-36"
          @change="emit('updateRole', member.id, selectedRole)"
        />
        <Button
          v-else
          icon="pi pi-pencil"
          variant="text"
          size="small"
          severity="secondary"
          @click="startEdit"
        />
        <Button
          icon="pi pi-trash"
          variant="text"
          size="small"
          severity="danger"
          :loading="loading"
          @click="emit('remove', member.id)"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import Select from 'primevue/select'
import type { ClubMembership } from '../types'

const props = defineProps<{
  member: ClubMembership
  canManage?: boolean
  loading?: boolean
  editingId?: number | null
}>()

const emit = defineEmits<{
  (e: 'updateRole', id: number, role: 'MEMBER' | 'MODERATOR' | 'ADMIN'): void
  (e: 'remove', id: number): void
  (e: 'startEdit', id: number): void
}>()

const selectedRole = ref(props.member.memberRole)

const roleOptions = [
  { label: 'Участник', value: 'MEMBER' },
  { label: 'Модератор', value: 'MODERATOR' },
  { label: 'Администратор', value: 'ADMIN' }
]

function startEdit() {
  selectedRole.value = props.member.memberRole
  emit('startEdit', props.member.id)
}

function roleLabel(role: ClubMembership['memberRole']) {
  const map: Record<string, string> = { MEMBER: 'Участник', MODERATOR: 'Модератор', ADMIN: 'Администратор' }
  return map[role] ?? role
}

function roleSeverity(role: ClubMembership['memberRole']) {
  const map: Record<string, string> = { MEMBER: 'secondary', MODERATOR: 'info', ADMIN: 'warn' }
  return map[role] ?? 'secondary'
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' })
}

function initials(name: string) {
  return name.split(' ').slice(0, 2).map(n => n[0]).join('').toUpperCase()
}
</script>
