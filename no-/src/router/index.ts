import { createRouter, createWebHistory } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ─── Studio (no auth) ──────────────────────────────
    {
      path: '/',
      component: () => import('@/pages/StudioPage.vue'),
    },

    // ─── Auth pages ────────────────────────────────────
    {
      path: '/login',
      component: () => import('@/pages/login.vue'),
    },
    {
      path: '/signup',
      component: () => import('@/pages/signup.vue'),
    },

    // ─── Service app (auth required) ───────────────────
    {
      path: '/city',
      component: () => import('@/pages/city/index.vue'),
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

    // ─── Fallback ──────────────────────────────────────
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
  scrollBehavior(to) {
    if (to.hash) return { el: to.hash, behavior: 'smooth', top: 72 }
    return { top: 0, behavior: 'smooth' }
  },
})

router.beforeEach(to => {
  // Studio page is always public
  if (to.path === '/') return

  const store = useAppStore()

  if (to.meta.requiresAuth && !store.isLoggedIn) {
    return { path: '/login' }
  }
  if (to.meta.requiresAdmin && !store.isAdmin) {
    return { path: '/' }
  }
  if ((to.path === '/login' || to.path === '/signup') && store.isLoggedIn) {
    return { path: '/city' }
  }
})

export default router
