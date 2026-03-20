<template>
  <div class="flex items-center justify-center min-h-screen bg-surface-ground">
    <Card class="w-full max-w-md shadow-lg">
      <template #title>
        <div class="text-center text-2xl font-bold">Вход</div>
      </template>
      <template #content>
        <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
          <div class="flex flex-col gap-2">
            <label for="login">Email или телефон</label>
            <InputText
              id="login"
              v-model="form.login"
              placeholder="example@mail.ru"
              :invalid="!form.login && submitted"
            />
          </div>
          <div class="flex flex-col gap-2">
            <label for="password">Пароль</label>
            <Password
              id="password"
              v-model="form.password"
              :feedback="false"
              toggle-mask
              fluid
              :invalid="!form.password && submitted"
            />
          </div>
          <Button
            type="submit"
            label="Войти"
            :loading="loading"
            class="mt-2"
          />
          <div class="text-center text-sm mt-2">
            Нет аккаунта?
            <router-link to="/register" class="text-primary font-semibold hover:underline">
              Зарегистрироваться
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
import { useToast } from 'primevue/usetoast'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const toast = useToast()

const form = reactive({ login: '', password: '' })
const loading = ref(false)
const submitted = ref(false)

async function onSubmit() {
  submitted.value = true
  if (!form.login || !form.password) return

  loading.value = true
  try {
    await authStore.login({ login: form.login, password: form.password })
  } catch (e: unknown) {
    const axiosErr = e as { response?: { data?: { message?: string } } }
    toast.add({
      severity: 'error',
      summary: 'Ошибка',
      detail: axiosErr.response?.data?.message || 'Неверный логин или пароль',
      life: 4000
    })
  } finally {
    loading.value = false
  }
}
</script>
