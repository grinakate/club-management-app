<template>
  <div>
    <div class="flex items-center gap-4 mb-6">
      <Button icon="pi pi-arrow-left" variant="text" rounded @click="router.push({ name: 'club-detail', params: { id: clubId } })" />
      <h1 class="text-2xl font-bold">Заявки на вступление</h1>
    </div>

    <div v-if="membershipStore.loading" class="flex justify-center py-12">
      <ProgressSpinner style="width: 2rem; height: 2rem" />
    </div>

    <div v-else>
      <div v-if="applications.length === 0" class="text-center text-surface-500 py-12">
        Заявок пока нет
      </div>

      <div v-else class="flex flex-col gap-3">
        <div class="flex gap-2 mb-4">
          <Button
            v-for="tab in tabs"
            :key="tab.value"
            :label="`${tab.label} (${countByStatus(tab.value)})`"
            :severity="activeTab === tab.value ? 'primary' : 'secondary'"
            :variant="activeTab === tab.value ? undefined : 'outlined'"
            size="small"
            @click="activeTab = tab.value"
          />
        </div>

        <ApplicationCard
          v-for="app in filteredApplications"
          :key="app.id"
          :application="app"
          :can-review="true"
          :loading="reviewingId === app.id"
          @approve="openReviewDialog(app.id, 'APPROVED')"
          @reject="openReviewDialog(app.id, 'REJECTED')"
        />
      </div>
    </div>

    <Dialog v-model:visible="dialogVisible" modal :header="dialogDecision === 'APPROVED' ? 'Одобрение заявки' : 'Отклонение заявки'" class="w-full max-w-md">
      <div class="flex flex-col gap-4">
        <div class="flex flex-col gap-2">
          <label class="font-medium">Комментарий <span class="text-surface-400 font-normal">(необязательно)</span></label>
          <Textarea v-model="reviewComment" rows="3" class="w-full" />
        </div>
        <div class="flex gap-3 justify-end">
          <Button label="Отмена" severity="secondary" variant="outlined" @click="dialogVisible = false" />
          <Button
            :label="dialogDecision === 'APPROVED' ? 'Одобрить' : 'Отклонить'"
            :severity="dialogDecision === 'APPROVED' ? 'success' : 'danger'"
            :loading="reviewLoading"
            @click="confirmReview"
          />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import Dialog from 'primevue/dialog'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'
import { useMembershipStore } from '../stores/membership'
import ApplicationCard from '../components/ApplicationCard.vue'
import type { MembershipApplication } from '../types'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const membershipStore = useMembershipStore()

const clubId = computed(() => Number(route.params.id))
const applications = computed(() => membershipStore.applications)

const activeTab = ref<MembershipApplication['status']>('NEW')
const reviewingId = ref<number | null>(null)
const reviewLoading = ref(false)
const dialogVisible = ref(false)
const dialogDecision = ref<'APPROVED' | 'REJECTED'>('APPROVED')
const dialogApplicationId = ref<number | null>(null)
const reviewComment = ref('')

const tabs: { label: string; value: MembershipApplication['status'] }[] = [
  { label: 'Новые', value: 'NEW' },
  { label: 'Одобренные', value: 'APPROVED' },
  { label: 'Отклонённые', value: 'REJECTED' }
]

const filteredApplications = computed(() =>
  applications.value.filter(a => a.status === activeTab.value)
)

function countByStatus(status: MembershipApplication['status']) {
  return applications.value.filter(a => a.status === status).length
}

function openReviewDialog(id: number, decision: 'APPROVED' | 'REJECTED') {
  dialogApplicationId.value = id
  dialogDecision.value = decision
  reviewComment.value = ''
  dialogVisible.value = true
}

async function confirmReview() {
  if (!dialogApplicationId.value) return
  reviewLoading.value = true
  reviewingId.value = dialogApplicationId.value
  try {
    await membershipStore.reviewApplication(dialogApplicationId.value, dialogDecision.value, reviewComment.value || null)
    dialogVisible.value = false
    toast.add({
      severity: 'success',
      summary: dialogDecision.value === 'APPROVED' ? 'Заявка одобрена' : 'Заявка отклонена',
      life: 3000
    })
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: err.response?.data?.message || 'Не удалось обработать заявку', life: 5000 })
  } finally {
    reviewLoading.value = false
    reviewingId.value = null
  }
}

onMounted(async () => {
  try {
    await membershipStore.fetchApplications(clubId.value)
  } catch {}
})
</script>
