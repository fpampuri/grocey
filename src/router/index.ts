/*
 * router/index.ts
 *
 * Manual routes for the app.
 */

import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/lists' },
  { path: '/lists', name: 'lists', component: () => import('@/views/ListsView.vue'), meta: { title: 'Lists'} },
  { path: '/products', name: 'products', component: () => import('@/views/ProductsView.vue'), meta: { title: 'Products'} },
  { path: '/settings', name: 'settings', component: () => import('@/views/SettingsView.vue'), meta: { title: 'Settings'} },
  { path: '/lists/:id', name: 'list-details', component: () => import('@/views/ListDetailsView.vue'), meta: { title: 'List Details'} },
  { path: '/products/:id', name: 'product-details', component: () => import('@/views/ProductDetailsView.vue'), meta: { title: 'Product Details'} },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
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
