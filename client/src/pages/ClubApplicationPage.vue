<template>
  <div class="max-w-xl">
    <div class="flex items-center gap-4 mb-6">
      <Button icon="pi pi-arrow-left" variant="text" rounded @click="router.back()" />
      <h1 class="text-2xl font-bold">Подача заявки в клуб</h1>
    </div>

    <Card>
      <template #content>
        <form class="flex flex-col gap-5" @submit.prevent="onSubmit">
          <div v-if="club" class="p-3 bg-surface-50 rounded-lg">
            <div class="text-sm text-surface-500">Клуб</div>
            <div class="font-semibold text-lg">{{ club.name }}</div>
            <div v-if="club.categoryName" class="text-sm text-surface-600">{{ club.categoryName }}</div>
          </div>

          <div class="flex flex-col gap-2">
            <label class="font-medium">Комментарий к заявке <span class="text-surface-400 font-normal">(необязательно)</span></label>
            <Textarea
              v-model="comment"
              rows="4"
              placeholder="Расскажите о себе и почему хотите вступить в клуб..."
              class="w-full"
            />
          </div>

          <div class="flex gap-3">
            <Button type="submit" label="Подать заявку" icon="pi pi-send" :loading="loading" />
            <Button type="button" label="Отмена" severity="secondary" variant="outlined" @click="router.back()" />
          </div>
        </form>
      </template>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'
import { useMembershipStore } from '../stores/membership'
import { useClubStore } from '../stores/club'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const membershipStore = useMembershipStore()
const clubStore = useClubStore()

const clubId = computed(() => Number(route.params.id))
const club = computed(() => clubStore.currentClub)
const comment = ref('')
const loading = ref(false)

async function onSubmit() {
  loading.value = true
  try {
    await membershipStore.applyToClub(clubId.value, comment.value || null)
    toast.add({ severity: 'success', summary: 'Заявка подана', detail: 'Ожидайте рассмотрения', life: 4000 })
    router.push({ name: 'club-detail', params: { id: clubId.value } })
  } catch (e: unknown) {
    const err = e as { response?: { data?: { message?: string } } }
    toast.add({ severity: 'error', summary: 'Ошибка', detail: err.response?.data?.message || 'Не удалось подать заявку', life: 5000 })
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    await clubStore.fetchClubById(clubId.value)
  } catch {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось загрузить клуб', life: 4000 })
  }
})
</script>
