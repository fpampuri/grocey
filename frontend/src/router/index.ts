/*
 * router/index.ts
 *
 * Manual routes for the app.
 */

import { createRouter, createWebHistory } from "vue-router";

const routes = [
  { path: "/", redirect: "/lists" },
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/LoginView.vue"),
    meta: { title: "Iniciar sesión", layout: "plain" },
  },
  {
    path: "/lists",
    name: "lists",
    component: () => import("@/views/ListsView.vue"),
    meta: { title: "Lists" },
  },
  {
    path: "/products",
    name: "products",
    component: () => import("@/views/ProductsView.vue"),
    meta: { title: "Products" },
  },
  {
    path: "/products/:id",
    name: "product-category-details",
    component: () => import("@/views/ProductCategoryView.vue"),
    meta: { title: "Product Category Details" },
  },
  {
    path: "/pantry",
    name: "pantry",
    component: () => import("@/views/PantryView.vue"),
    meta: { title: "Pantry" },
  },
  {
    path: "/pantry/:id",
    name: "pantry-category-details",
    component: () => import("@/views/ProductCategoryView.vue"),
    meta: { title: "Pantry Category Details" },
  },
  {
    path: "/settings",
    name: "settings",
    component: () => import("@/views/SettingsView.vue"),
    meta: { title: "Settings" },
  },
  {
    path: "/lists/:id",
    name: "list-details",
    component: () => import("@/views/ListDetailsView.vue"),
    meta: { title: "List Details" },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.("Failed to fetch dynamically imported module")) {
    if (localStorage.getItem("vuetify:dynamic-reload")) {
      console.error("Dynamic import error, reloading page did not fix it", err);
    } else {
      console.log("Reloading page to fix dynamic import error");
      localStorage.setItem("vuetify:dynamic-reload", "true");
      location.assign(to.fullPath);
    }
  } else {
    console.error(err);
  }
});

router.isReady().then(() => {
  localStorage.removeItem("vuetify:dynamic-reload");
});

export default router;
