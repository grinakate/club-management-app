<template>
  <div class="flex items-center justify-center min-h-screen bg-surface-ground py-8">
    <Card class="w-full max-w-lg shadow-lg">
      <template #title>
        <div class="text-center text-2xl font-bold">Регистрация</div>
      </template>
      <template #content>
        <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
          <div class="flex flex-col gap-2">
            <label for="fullName">ФИО</label>
            <InputText
              id="fullName"
              v-model="form.fullName"
              placeholder="Иванов Иван Иванович"
              :invalid="!form.fullName && submitted"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="email">Email</label>
            <InputText
              id="email"
              v-model="form.email"
              type="email"
              placeholder="example@mail.ru"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="phone">Телефон</label>
            <InputText
              id="phone"
              v-model="form.phone"
              placeholder="+7 (999) 123-45-67"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="password">Пароль</label>
            <Password
              id="password"
              v-model="form.password"
              toggle-mask
              fluid
              :invalid="!form.password && submitted"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="city">Город</label>
            <InputText
              id="city"
              v-model="form.city"
              placeholder="Москва"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="birthDate">Дата рождения</label>
            <DatePicker
              id="birthDate"
              v-model="birthDate"
              date-format="dd.mm.yy"
              show-icon
              fluid
              :invalid="!birthDate && submitted"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="interests">Интересы</label>
            <Textarea
              id="interests"
              v-model="form.interests"
              rows="3"
              placeholder="Программирование, спорт, музыка..."
            />
          </div>
          <Button
            type="submit"
            label="Зарегистрироваться"
            :loading="loading"
            class="mt-2"
          />
          <div class="text-center text-sm mt-2">
            Уже есть аккаунт?
            <router-link to="/login" class="text-primary font-semibold hover:underline">
              Войти
            </router-link>
          </div>
        </form>
      </template>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import DatePicker from 'primevue/datepicker'
import Textarea from 'primevue/textarea'
import { useToast } from 'primevue/usetoast'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const toast = useToast()

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  password: '',
  city: '',
  interests: ''
})
const birthDate = ref<Date | null>(null)
const loading = ref(false)
const submitted = ref(false)

function formatDate(date: Date): string {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

async function onSubmit() {
  submitted.value = true
  if (!form.fullName || !form.password || !birthDate.value) return
  if (!form.email && !form.phone) {
    toast.add({
      severity: 'warn',
      summary: 'Внимание',
      detail: 'Укажите email или телефон',
      life: 4000
    })
    return
  }

  loading.value = true
  try {
    await authStore.register({
      fullName: form.fullName,
      email: form.email || null,
      phone: form.phone || null,
      password: form.password,
      city: form.city || null,
      interests: form.interests || null,
      birthDate: formatDate(birthDate.value)
    })
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Не удалось зарегистрироваться',
      life: 4000
    })
  } finally {
    loading.value = false
  }
}
</script>
