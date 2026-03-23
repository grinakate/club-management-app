<template>
  <div>
    <h1 class="text-2xl font-bold mb-6">Создание мероприятия</h1>

    <Card>
      <template #content>
        <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
          <div class="flex flex-col gap-1">
            <label class="font-medium">Клуб</label>
            <Select
              v-model="form.clubId"
              :options="clubStore.clubs"
              optionLabel="name"
              optionValue="id"
              placeholder="Выберите клуб"
              class="w-full"
            />
          </div>

          <div class="flex flex-col gap-1">
            <label class="font-medium">Название</label>
            <InputText v-model="form.title" placeholder="Название мероприятия" class="w-full" />
          </div>

          <div class="flex flex-col gap-1">
            <label class="font-medium">Описание</label>
            <Textarea v-model="form.description" rows="4" placeholder="Описание мероприятия" class="w-full" />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-1">
              <label class="font-medium">Дата начала</label>
              <DatePicker v-model="form.startAt" showTime hourFormat="24" dateFormat="dd.mm.yy" class="w-full" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="font-medium">Дата окончания</label>
              <DatePicker v-model="form.endAt" showTime hourFormat="24" dateFormat="dd.mm.yy" class="w-full" />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-1">
              <label class="font-medium">Лимит участников</label>
              <InputNumber v-model="form.participantLimit" :min="1" placeholder="Без ограничений" class="w-full" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="font-medium">Цена</label>
              <InputNumber
                v-model="form.price"
                mode="currency"
                currency="RUB"
                locale="ru-RU"
                :min="0"
                placeholder="Бесплатно"
                class="w-full"
              />
            </div>
          </div>

          <div class="flex gap-2 mt-4">
            <Button type="submit" label="Создать" icon="pi pi-check" :loading="submitting" />
            <Button
              type="button"
              label="Отмена"
              severity="secondary"
              variant="outlined"
              @click="router.push({ name: 'events' })"
            />
          </div>
        </form>
      </template>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Card from 'primevue/card'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import InputNumber from 'primevue/inputnumber'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'
import { useToast } from 'primevue/usetoast'
import { useEventStore } from '../stores/event'
import { useClubStore } from '../stores/club'

const router = useRouter()
const toast = useToast()
const eventStore = useEventStore()
const clubStore = useClubStore()

const submitting = ref(false)

const form = reactive({
  clubId: null as number | null,
  title: '',
  description: '',
  startAt: null as Date | null,
  endAt: null as Date | null,
  participantLimit: null as number | null,
  price: null as number | null
})

async function handleSubmit() {
  if (!form.clubId || !form.title || !form.startAt || !form.endAt) {
    toast.add({ severity: 'warn', summary: 'Внимание', detail: 'Заполните обязательные поля', life: 3000 })
    return
  }

  submitting.value = true
  try {
    const payload = {
      clubId: form.clubId,
      title: form.title,
      description: form.description || null,
      startAt: form.startAt.toISOString(),
      endAt: form.endAt.toISOString(),
      participantLimit: form.participantLimit,
      price: form.price
    }
    const created = await eventStore.createEvent(payload)
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Мероприятие создано', life: 3000 })
    router.push({ name: 'event-detail', params: { id: created.id } })
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось создать мероприятие',
      life: 5000
    })
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    await clubStore.fetchMyClubs()
  } catch {
    await clubStore.fetchClubs()
  }
})
</script>
