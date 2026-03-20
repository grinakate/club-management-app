<template>
  <aside class="w-64 bg-surface-0 border-r border-surface-200 p-4">
    <nav class="flex flex-col gap-1">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="flex items-center gap-3 px-4 py-3 rounded-lg text-surface-700 hover:bg-surface-100 transition-colors"
        active-class="!bg-primary-50 !text-primary-700 font-semibold"
      >
        <i :class="item.icon" />
        <span>{{ item.label }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup lang="ts">
const menuItems = [
  { path: '/clubs', label: 'Клубы', icon: 'pi pi-building' },
  { path: '/my-clubs', label: 'Мои клубы', icon: 'pi pi-heart' },
  { path: '/events', label: 'Мероприятия', icon: 'pi pi-calendar' },
  { path: '/my-events', label: 'Мои мероприятия', icon: 'pi pi-check-circle' },
  { path: '/profile', label: 'Профиль', icon: 'pi pi-user' }
]
</script>
