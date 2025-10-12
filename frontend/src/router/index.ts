/*
 * router/index.ts
 *
 * Manual routes for the app.
 */

import { createRouter, createWebHistory } from 'vue-router'
import pinia from '@/stores'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: 'Iniciar sesión', layout: 'plain' },
  },
  {
    path: '/forgot-password',
    name: 'forgot-password',
    component: () => import('@/views/ForgotPasswordView.vue'),
    meta: { title: 'Forgot Password', layout: 'plain' },
  },
  {
    path: '/lists',
    name: 'lists',
    component: () => import('@/views/ListsView.vue'),
    meta: { title: 'Lists' },
  },
  {
    path: '/lists/history',
    name: 'lists-history',
    component: () => import('@/views/HistoryView.vue'),
    meta: { title: 'History' },
  },
  {
    path: '/products',
    name: 'products',
    component: () => import('@/views/ProductsView.vue'),
    meta: { title: 'Products' },
  },
  {
    path: '/products/:id',
    name: 'product-category-details',
    component: () => import('@/views/ProductCategoryView.vue'),
    meta: { title: 'Product Category Details' },
  },
  {
    path: '/pantry',
    name: 'pantry',
    component: () => import('@/views/PantryView.vue'),
    meta: { title: 'Pantry' },
  },
  {
    path: '/pantry/:id',
    name: 'pantry-details',
    component: () => import('@/views/PantryDetailsView.vue'),
    meta: { title: 'Pantry Details' },
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('@/views/SettingsView.vue'),
    meta: { title: 'Settings' },
  },
  {
    path: '/lists/:id',
    name: 'list-details',
    component: () => import('@/views/ListDetailsView.vue'),
    meta: { title: 'List Details' },
  },
  {
    path: '/lists/history/:id',
    name: 'history-list-details',
    component: () => import('@/views/ListDetailsView.vue'),
    meta: { title: 'List Details' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

const publicRouteNames = new Set(['login', 'forgot-password'])

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore(pinia)

  if (!userStore.initialized) {
    userStore.init()
  }

  const routeName = typeof to.name === 'string' ? to.name : null
  const isAuthenticated = Boolean(userStore.token)

  if (routeName === 'login' && isAuthenticated) {
    next({ name: 'lists' })
    return
  }

  if (!isAuthenticated && routeName && !publicRouteNames.has(routeName)) {
    next({ name: 'login' })
    return
  }

  next()
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (localStorage.getItem('vuetify:dynamic-reload')) {
      console.error('Dynamic import error, reloading page did not fix it', err)
    } else {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router
