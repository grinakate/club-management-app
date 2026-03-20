<template>
  <header class="bg-surface-0 shadow-sm h-16 flex items-center justify-between px-6 border-b border-surface-200">
    <div class="text-xl font-bold text-primary">Club Management</div>
    <div class="flex items-center gap-4">
      <span class="text-surface-700">{{ auth.user?.fullName }}</span>
      <Button icon="pi pi-sign-out" severity="secondary" variant="text" rounded @click="auth.logout()" />
    </div>
  </header>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
</script>
