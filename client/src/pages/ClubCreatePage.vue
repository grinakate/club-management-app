<template>
  <div>
    <div class="flex items-center gap-4 mb-6">
      <Button
        icon="pi pi-arrow-left"
        variant="text"
        rounded
        @click="router.push({ name: 'clubs' })"
      />
      <h1 class="text-2xl font-bold">Создание клуба</h1>
    </div>

    <Card class="max-w-2xl shadow-md">
      <template #content>
        <form class="flex flex-col gap-5" @submit.prevent="onSubmit">
          <div class="flex flex-col gap-2">
            <label for="name" class="font-semibold">Название</label>
            <InputText
              id="name"
              v-model="form.name"
              placeholder="Название клуба"
              :invalid="submitted && !form.name"
            />
          </div>

          <div class="flex flex-col gap-2">
            <label for="description" class="font-semibold">Описание</label>
            <Textarea
              id="description"
              v-model="form.description"
              rows="4"
              placeholder="Описание клуба"
              auto-resize
            />
          </div>

          <div class="flex flex-col gap-2">
            <label for="category" class="font-semibold">Категория</label>
            <Select
              id="category"
              v-model="form.categoryId"
              :options="clubStore.categories"
              option-label="name"
              option-value="id"
              placeholder="Выберите категорию"
              :invalid="submitted && !form.categoryId"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <label for="ageMin" class="font-semibold">Мин. возраст</label>
              <InputNumber
                id="ageMin"
                v-model="form.ageLimitMin"
                placeholder="0"
                :min="0"
                :max="120"
              />
            </div>
            <div class="flex flex-col gap-2">
              <label for="ageMax" class="font-semibold">Макс. возраст</label>
              <InputNumber
                id="ageMax"
                v-model="form.ageLimitMax"
                placeholder="0"
                :min="0"
                :max="120"
              />
            </div>
          </div>

          <div class="flex flex-col gap-2">
            <label for="fee" class="font-semibold">Членский взнос</label>
            <InputNumber
              id="fee"
              v-model="form.membershipFee"
              mode="currency"
              currency="RUB"
              locale="ru-RU"
              placeholder="0"
              :min="0"
            />
          </div>

          <div class="flex gap-3 mt-2">
            <Button
              type="submit"
              label="Создать"
              icon="pi pi-check"
              :loading="loading"
            />
            <Button
              type="button"
              label="Отмена"
              severity="secondary"
              variant="outlined"
              @click="router.push({ name: 'clubs' })"
            />
          </div>
        </form>
      </template>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Select from 'primevue/select'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import InputNumber from 'primevue/inputnumber'
import { useToast } from 'primevue/usetoast'
import { useClubStore } from '../stores/club'

const router = useRouter()
const toast = useToast()
const clubStore = useClubStore()

const loading = ref(false)
const submitted = ref(false)

const form = reactive({
  name: '',
  description: '',
  categoryId: null as number | null,
  ageLimitMin: null as number | null,
  ageLimitMax: null as number | null,
  membershipFee: null as number | null
})

async function onSubmit() {
  submitted.value = true
  if (!form.name || !form.categoryId) return

  loading.value = true
  try {
    const created = await clubStore.createClub({
      name: form.name,
      description: form.description || null,
      categoryId: form.categoryId,
      ageLimitMin: form.ageLimitMin,
      ageLimitMax: form.ageLimitMax,
      membershipFee: form.membershipFee
    })
    router.push({ name: 'club-detail', params: { id: created.id } })
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось создать клуб',
      life: 4000
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  clubStore.fetchCategories()
})
</script>
