<template>
  <div class="min-h-screen flex flex-col">
    <AppHeader />
    <div class="flex flex-1">
      <AppSidebar />
      <main class="flex-1 p-6 bg-surface-ground">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '../components/AppHeader.vue'
import AppSidebar from '../components/AppSidebar.vue'
import { useAuthStore } from '../stores/auth'
import { onMounted } from 'vue'

const auth = useAuthStore()

onMounted(() => {
  if (auth.isAuthenticated && !auth.user) {
    auth.fetchUser()
  }
})
</script>
