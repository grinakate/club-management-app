<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useUserStore } from '../stores/user'
import { useToast } from 'primevue/usetoast'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import Password from 'primevue/password'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'

const userStore = useUserStore()
const toast = useToast()

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  city: '',
  interests: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const roleMap: Record<string, 'info' | 'success' | 'warn'> = {
  PARTICIPANT: 'info',
  ADMIN: 'success',
  MANAGER: 'warn'
}

const roleLabelMap: Record<string, string> = {
  PARTICIPANT: 'Участник',
  ADMIN: 'Администратор',
  MANAGER: 'Руководитель'
}

function syncForm() {
  if (!userStore.profile) return
  form.fullName = userStore.profile.fullName ?? ''
  form.email = userStore.profile.email ?? ''
  form.phone = userStore.profile.phone ?? ''
  form.city = userStore.profile.city ?? ''
  form.interests = userStore.profile.interests ?? ''
}

onMounted(async () => {
  await userStore.fetchProfile()
  syncForm()
})

async function saveProfile() {
  try {
    await userStore.updateProfile({
      fullName: form.fullName,
      email: form.email || null,
      phone: form.phone || null,
      city: form.city || null,
      interests: form.interests || null
    })
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Профиль обновлён', life: 3000 })
  } catch {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось обновить профиль', life: 3000 })
  }
}

async function handleChangePassword() {
  try {
    await userStore.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    toast.add({ severity: 'success', summary: 'Успешно', detail: 'Пароль изменён', life: 3000 })
  } catch {
    toast.add({ severity: 'error', summary: 'Ошибка', detail: 'Не удалось изменить пароль', life: 3000 })
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto flex flex-col gap-6">
    <div v-if="userStore.loading" class="flex justify-center py-12">
      <ProgressSpinner />
    </div>

    <template v-else-if="userStore.profile">
      <Card>
        <template #title>Профиль</template>
        <template #content>
          <div class="flex flex-col gap-4">
            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">ФИО</label>
              <InputText v-model="form.fullName" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Email</label>
              <InputText v-model="form.email" type="email" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Телефон</label>
              <InputText v-model="form.phone" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Город</label>
              <InputText v-model="form.city" />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Интересы</label>
              <Textarea v-model="form.interests" rows="3" auto-resize />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Дата рождения</label>
              <span class="text-surface-700">{{ userStore.profile.birthDate }}</span>
            </div>

            <div class="flex items-center gap-2">
              <label class="text-sm font-medium">Роль</label>
              <Tag
                :value="roleLabelMap[userStore.profile.role]"
                :severity="roleMap[userStore.profile.role]"
              />
            </div>

            <Button label="Сохранить изменения" @click="saveProfile" class="mt-2" />
          </div>
        </template>
      </Card>

      <Card>
        <template #title>Смена пароля</template>
        <template #content>
          <div class="flex flex-col gap-4">
            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Текущий пароль</label>
              <Password v-model="passwordForm.oldPassword" :feedback="false" toggle-mask />
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-sm font-medium">Новый пароль</label>
              <Password v-model="passwordForm.newPassword" toggle-mask />
            </div>

            <Button label="Изменить пароль" severity="secondary" @click="handleChangePassword" class="mt-2" />
          </div>
        </template>
      </Card>
    </template>
  </div>
</template>
