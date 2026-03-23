<template>
  <div ref="bellContainerEl" class="relative">
    <Button
      icon="pi pi-bell"
      variant="text"
      severity="secondary"
      rounded
      aria-label="Уведомления"
      @click="toggleDropdown"
    />
    <span
      v-if="store.unreadCount > 0"
      class="absolute -top-1 -right-1 min-w-[18px] h-[18px] bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1 pointer-events-none"
    >
      {{ store.unreadCount > 99 ? '99+' : store.unreadCount }}
    </span>

    <Teleport to="body">
      <template v-if="open">
        <div class="fixed inset-0 z-40" @click="close" />
        <Transition name="dropdown">
          <div
            class="fixed z-50"
            :style="dropdownStyle"
          >
            <NotificationDropdown @navigate="handleNavigate" />
          </div>
        </Transition>
      </template>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import Button from 'primevue/button'
import { useNotificationStore } from '../stores/notification'
import NotificationDropdown from './NotificationDropdown.vue'

const store = useNotificationStore()
const router = useRouter()
const open = ref(false)
const bellContainerEl = ref<HTMLElement | null>(null)
const bellRect = ref<DOMRect | null>(null)

const dropdownStyle = computed(() => {
  if (!bellRect.value) return {}
  const rect = bellRect.value
  const right = window.innerWidth - rect.right
  return {
    top: `${rect.bottom + 8}px`,
    right: `${right}px`
  }
})

function close() {
  open.value = false
  bellRect.value = null
}

async function toggleDropdown() {
  if (open.value) {
    close()
    return
  }
  bellRect.value = bellContainerEl.value?.getBoundingClientRect() ?? null
  open.value = true
  await store.fetchNotifications()
}

function handleNavigate() {
  close()
  router.push({ name: 'notifications' })
}

function handleResize() {
  if (open.value) close()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
