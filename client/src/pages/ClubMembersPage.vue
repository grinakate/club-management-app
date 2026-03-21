<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-4">
        <Button icon="pi pi-arrow-left" variant="text" rounded @click="router.push({ name: 'club-detail', params: { id: clubId } })" />
        <h1 class="text-2xl font-bold">Участники клуба</h1>
      </div>
      <Tag v-if="!membershipStore.loading" :value="`${members.length} участников`" severity="secondary" />
    </div>

    <div v-if="membershipStore.loading" class="flex justify-center py-12">
      <ProgressSpinner style="width: 2rem; height: 2rem" />
    </div>

    <div v-else>
      <div v-if="members.length === 0" class="text-center text-surface-500 py-12">
        Участников пока нет
      </div>

      <div v-else class="flex flex-col gap-3">
        <MemberCard
          v-for="member in members"
          :key="member.id"
          :member="member"
          :can-manage="isOwner"
          :loading="removingId === member.id"
          :editing-id="editingId"
          @update-role="handleUpdateRole"
          @remove="handleRemove"
          @start-edit="editingId = $event"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useToast } from 'primevue/usetoast'
import { useMembershipStore } from '../stores/membership'
import { useAuthStore } from '../stores/auth'
import { useClubStore } from '../stores/club'
import MemberCard from '../components/MemberCard.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const membershipStore = useMembershipStore()
const authStore = useAuthStore()
const clubStore = useClubStore()

const clubId = computed(() => Number(route.params.id))
const members = computed(() => membershipStore.members)
const isOwner = computed(() => clubStore.currentClub?.ownerUserId === authStore.user?.id)

const removingId = ref<number | null>(null)
const editingId = ref<number | null>(null)

async function handleUpdateRole(id: number, role: 'MEMBER' | 'MODERATOR' | 'ADMIN') {
  try {
    await membershipStore.updateRole(id, role)
    editingId.value = null
    toast.add({ severity: 'success', summary: 'Роль обновлена', life: 3000 })
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: err.response?.data?.message || 'Не удалось изменить роль', life: 5000 })
  }
}

async function handleRemove(id: number) {
  removingId.value = id
  try {
    await membershipStore.removeMember(id)
    toast.add({ severity: 'success', summary: 'Участник исключён', life: 3000 })
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: err.response?.data?.message || 'Не удалось исключить участника', life: 5000 })
  } finally {
    removingId.value = null
  }
}

onMounted(async () => {
  try {
    await clubStore.fetchClubById(clubId.value)
    await membershipStore.fetchMembers(clubId.value)
  } catch {}
})
</script>
