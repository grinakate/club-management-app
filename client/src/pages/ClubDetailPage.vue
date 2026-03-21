<template>
  <div>
    <ProgressSpinner v-if="clubStore.loading" class="flex justify-center" />

    <div v-else-if="!club" class="text-center text-gray-500 py-12">
      Клуб не найден
    </div>

    <div v-else>
      <div class="flex items-center gap-4 mb-6">
        <Button
          icon="pi pi-arrow-left"
          variant="text"
          rounded
          @click="router.push({ name: 'clubs' })"
        />
        <h1 class="text-2xl font-bold">{{ club.name }}</h1>
        <Tag :value="club.status" :severity="statusSeverity" />
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card class="lg:col-span-2 shadow-md">
          <template #title>Описание</template>
          <template #content>
            <p class="text-gray-700 whitespace-pre-line">
              {{ club.description || 'Описание отсутствует' }}
            </p>
          </template>
        </Card>

        <Card class="shadow-md">
          <template #title>Информация</template>
          <template #content>
            <div class="flex flex-col gap-3">
              <div>
                <span class="text-sm text-gray-500">Категория</span>
                <div class="font-semibold">{{ club.categoryName || '—' }}</div>
              </div>
              <div>
                <span class="text-sm text-gray-500">Возраст</span>
                <div class="font-semibold">{{ ageRange }}</div>
              </div>
              <div>
                <span class="text-sm text-gray-500">Членский взнос</span>
                <div class="font-semibold">
                  {{ club.membershipFee != null ? `${club.membershipFee} ₽` : 'Бесплатно' }}
                </div>
              </div>
              <div>
                <span class="text-sm text-gray-500">Владелец</span>
                <div class="font-semibold">{{ club.ownerFullName }}</div>
              </div>
              <div>
                <span class="text-sm text-gray-500">Дата создания</span>
                <div class="font-semibold">{{ formatDate(club.createdAt) }}</div>
              </div>
            </div>
          </template>
        </Card>
      </div>

      <div class="flex flex-wrap gap-3 mt-6">
        <template v-if="isOwner">
          <Button
            label="Редактировать"
            icon="pi pi-pencil"
            @click="onEdit"
          />
          <Button
            label="Участники"
            icon="pi pi-users"
            severity="secondary"
            variant="outlined"
            @click="router.push({ name: 'club-members', params: { id: clubId } })"
          />
          <Button
            label="Заявки"
            icon="pi pi-list"
            severity="secondary"
            variant="outlined"
            @click="router.push({ name: 'club-applications', params: { id: clubId } })"
          />
          <Button
            v-if="club.status === 'ACTIVE'"
            label="Архивировать"
            icon="pi pi-inbox"
            severity="warn"
            variant="outlined"
            @click="onArchive"
          />
        </template>
        <template v-else>
          <Button
            v-if="!isActiveMember && club.status === 'ACTIVE'"
            label="Подать заявку"
            icon="pi pi-send"
            @click="router.push({ name: 'club-apply', params: { id: clubId } })"
          />
          <Button
            v-if="isActiveMember"
            label="Покинуть клуб"
            icon="pi pi-sign-out"
            severity="danger"
            variant="outlined"
            :loading="leavingClub"
            @click="onLeave"
          />
          <Button
            v-if="isActiveMember"
            label="Участники"
            icon="pi pi-users"
            severity="secondary"
            variant="outlined"
            @click="router.push({ name: 'club-members', params: { id: clubId } })"
          />
        </template>
      </div>

      <Card class="mt-6 shadow-md">
        <template #title>Мероприятия клуба</template>
        <template #content>
          <ProgressSpinner v-if="eventsLoading" class="flex justify-center" />
          <div v-else-if="clubEvents.length === 0" class="text-gray-500">
            Мероприятий пока нет
          </div>
          <div v-else class="flex flex-col gap-3">
            <div
              v-for="event in clubEvents"
              :key="event.id"
              class="flex items-center justify-between p-3 rounded-lg border hover:bg-gray-50 cursor-pointer transition-colors"
              @click="router.push({ name: 'event-detail', params: { id: event.id } })"
            >
              <div>
                <div class="font-semibold">{{ event.title }}</div>
                <div class="text-sm text-gray-500">{{ formatDate(event.startAt) }}</div>
              </div>
              <Tag :value="event.status" :severity="eventStatusSeverity(event.status)" />
            </div>
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import { useToast } from 'primevue/usetoast'
import { useClubStore } from '../stores/club'
import { useAuthStore } from '../stores/auth'
import { useEventStore } from '../stores/event'
import { useMembershipStore } from '../stores/membership'
import type { Event } from '../types'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const clubStore = useClubStore()
const authStore = useAuthStore()
const eventStore = useEventStore()
const membershipStore = useMembershipStore()

const clubEvents = ref<Event[]>([])
const eventsLoading = ref(false)
const leavingClub = ref(false)

const club = computed(() => clubStore.currentClub)
const clubId = computed(() => Number(route.params.id))
const isOwner = computed(() => club.value && authStore.user && club.value.ownerUserId === authStore.user.id)
const isActiveMember = computed(() =>
  membershipStore.members.some(m => m.userId === authStore.user?.id && m.status === 'ACTIVE')
)

const statusSeverity = computed(() => {
  switch (club.value?.status) {
    case 'ACTIVE': return 'success'
    case 'ARCHIVED': return 'warn'
    case 'DRAFT': return 'secondary'
    default: return undefined
  }
})

const ageRange = computed(() => {
  const min = club.value?.ageLimitMin
  const max = club.value?.ageLimitMax
  if (min != null && max != null) return `${min} — ${max} лет`
  if (min != null) return `от ${min} лет`
  if (max != null) return `до ${max} лет`
  return 'Без ограничений'
})

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}

function eventStatusSeverity(status: string) {
  switch (status) {
    case 'PUBLISHED': return 'success' as const
    case 'CANCELLED': return 'danger' as const
    case 'COMPLETED': return 'secondary' as const
    case 'DRAFT': return 'warn' as const
    default: return undefined
  }
}

function onEdit() {
  toast.add({ severity: 'info', summary: 'В разработке', detail: 'Редактирование пока не реализовано', life: 3000 })
}

async function onArchive() {
  if (!club.value) return
  try {
    await clubStore.deleteClub(club.value.id)
    await clubStore.fetchClubById(clubId.value)
    toast.add({ severity: 'success', summary: 'Готово', detail: 'Клуб архивирован', life: 3000 })
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: axiosErr.response?.data?.message || 'Не удалось архивировать', life: 4000 })
  }
}

async function loadEvents() {
  eventsLoading.value = true
  try {
    await eventStore.fetchEvents({ clubId: clubId.value })
    clubEvents.value = eventStore.events
  } catch {
    clubEvents.value = []
  } finally {
    eventsLoading.value = false
  }
}

async function onLeave() {
  if (!club.value) return
  leavingClub.value = true
  try {
    await membershipStore.leaveClub(club.value.id)
    await membershipStore.fetchMembers(clubId.value)
    toast.add({ severity: 'success', summary: 'Вы покинули клуб', life: 3000 })
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: axiosErr.response?.data?.message || 'Не удалось покинуть клуб', life: 4000 })
  } finally {
    leavingClub.value = false
  }
}

onMounted(async () => {
  try {
    await clubStore.fetchClubById(clubId.value)
    await membershipStore.fetchMembers(clubId.value)
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: axiosErr.response?.data?.message || 'Не удалось загрузить данные', life: 4000 })
  }
  loadEvents()
})
</script>
