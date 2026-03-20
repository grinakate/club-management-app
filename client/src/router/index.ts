import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../pages/LoginPage.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../pages/RegisterPage.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('../layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/clubs' },
        { path: 'clubs', name: 'clubs', component: () => import('../pages/ClubListPage.vue') },
        {
          path: 'clubs/create',
          name: 'club-create',
          component: () => import('../pages/ClubCreatePage.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        { path: 'clubs/:id', name: 'club-detail', component: () => import('../pages/ClubDetailPage.vue') },
        { path: 'events', name: 'events', component: () => import('../pages/EventListPage.vue') },
        {
          path: 'events/create',
          name: 'event-create',
          component: () => import('../pages/EventCreatePage.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        { path: 'events/:id', name: 'event-detail', component: () => import('../pages/EventDetailPage.vue') },
        {
          path: 'events/:id/participants',
          name: 'event-participants',
          component: () => import('../pages/EventParticipantsPage.vue'),
          meta: { roles: ['ADMIN', 'MANAGER'] }
        },
        { path: 'my-events', name: 'my-events', component: () => import('../pages/MyEventsPage.vue') },
        { path: 'my-clubs', name: 'my-clubs', component: () => import('../pages/MyClubsPage.vue') },
        { path: 'profile', name: 'profile', component: () => import('../pages/ProfilePage.vue') }
      ]
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth !== false && !token) {
    return next({ name: 'login' })
  }

  if ((to.name === 'login' || to.name === 'register') && token) {
    return next({ path: '/' })
  }

  const requiredRoles = to.meta.roles as string[] | undefined
  if (requiredRoles && token) {
    const { useAuthStore } = await import('../stores/auth')
    const authStore = useAuthStore()

    if (!authStore.user) {
      await authStore.fetchUser()
    }

    if (!authStore.user) {
      return next({ name: 'login' })
    }

    if (!requiredRoles.includes(authStore.user.role)) {
      return next({ path: '/' })
    }
  }

  next()
})

export default router
