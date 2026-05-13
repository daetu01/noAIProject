import { createRouter, createWebHistory } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      component: () => import('@/pages/login.vue'),
    },
    {
      path: '/signup',
      component: () => import('@/pages/signup.vue'),
    },
    {
      path: '/',
      component: () => import('@/pages/index.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/board',
      component: () => import('@/pages/board.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/board/:id',
      component: () => import('@/pages/boardDetail.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/games',
      component: () => import('@/pages/games/index.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/games/maple',
      component: () => import('@/pages/games/maple.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/market',
      component: () => import('@/pages/market.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/inventory',
      component: () => import('@/pages/inventory.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      component: () => import('@/pages/admin.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
  ],
})

router.beforeEach(to => {
  const store = useAppStore()

  if (to.meta.requiresAuth && !store.isLoggedIn) {
    return { path: '/login' }
  }

  if (to.meta.requiresAdmin && !store.isAdmin) {
    return { path: '/' }
  }

  if ((to.path === '/login' || to.path === '/signup') && store.isLoggedIn) {
    return { path: '/' }
  }
})

export default router
