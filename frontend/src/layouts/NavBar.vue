<script setup lang="ts">
import { ref, computed, watch } from "vue";
import NavBarButton from "@/components/NavBar/NavBarButton.vue";
import NavListItem from "@/components/NavBar/NavListItem.vue";
import NavBarTitle from "@/components/NavBar/NavBarTitle.vue";
import ProfilePanel from "@/components/Profile/ProfilePanel.vue";
import { useRoute } from "vue-router";
import apiClient from "@/services/api";

const route = useRoute();

const selectedItem = ref("lists"); // Default selected item
const showProfilePanel = ref(false);
const dynamicTitle = ref<string>("");

const selectItem = (item: string) => {
  selectedItem.value = item;
};

// Dynamic data fetching functions
async function getListName(id: string): Promise<string> {
  try {
    const { data } = await apiClient.get(`/shopping-lists/${id}`);
    const list = data?.list ?? data;
    if (list?.name) return list.name;
    if (list?.title) return list.title;
  } catch (error) {
    console.error("Error fetching list name:", error);
    return `List ${id}`;
  }
  return `List ${id}`;
}

async function getCategoryName(id: string): Promise<string> {
  try {
    // Try to fetch from real API
    const { data } = await apiClient.get(`/categories/${id}`);
    const category = data?.category ?? data;
    if (category?.name) return category.name;
    if (category?.title) return category.title;
  } catch (error) {
    console.error("Error fetching category name:", error);
  }
  
  return `Product Category ${id}`;
}

async function getPantryName(id: string): Promise<string> {
  try {
    // Try to fetch from real API (pantry items would be different from categories)
    const { data } = await apiClient.get(`/pantry/${id}`);
    const pantryItem = data?.pantryItem ?? data;
    if (pantryItem?.name) return pantryItem.name;
    if (pantryItem?.title) return pantryItem.title;
  } catch (error) {
    console.error("Error fetching pantry item name:", error);
  }
  
  return `Pantry Item ${id}`;
}

// Function to fetch dynamic title based on route
async function fetchDynamicTitle() {
  // Handle specific routes with dynamic names
  if (route.name === "list-details" && route.params.id) {
    dynamicTitle.value = await getListName(route.params.id as string);
    return;
  }

  if (route.name === "product-category-details" && route.params.id) {
    dynamicTitle.value = await getCategoryName(route.params.id as string);
    return;
  }

  if (route.name === "pantry-category-details" && route.params.id) {
    dynamicTitle.value = await getPantryName(route.params.id as string);
    return;
  }

  // For non-dynamic routes, clear the dynamic title
  dynamicTitle.value = "";
}

// Watch for route changes and fetch dynamic titles
watch(() => [route.name, route.params.id], fetchDynamicTitle, {
  immediate: true,
});

// Watch for route changes and update selected item
watch(() => route.name, (newRouteName) => {
  if (newRouteName === 'lists' || newRouteName === 'list-details') {
    selectedItem.value = 'lists';
  } else if (newRouteName === 'products' || newRouteName === 'product-category-details') {
    selectedItem.value = 'products';
  } else if (newRouteName === 'pantry' || newRouteName === 'pantry-category-details') {
    selectedItem.value = 'pantry';
  } else if (newRouteName === 'settings') {
    selectedItem.value = 'settings';
  }
}, { immediate: true });

const pageTitle = computed(() => {
  // Use dynamic title if available
  if (dynamicTitle.value) {
    return dynamicTitle.value;
  }

  // For other routes, use the meta title or fallback
  const matched = route.matched
    .slice()
    .reverse()
    .find((r) => r.meta && (r.meta as any).title);
  if (matched) return (matched.meta as any).title;

  // Final fallback to a nicer name based on route.name
  return route.name
    ? String(route.name).replace(/^\w/, (c) => c.toUpperCase())
    : "Grocey";
});

const onProfileClick = () => {
  showProfilePanel.value = true;
};

// Helper function to check if a route is selected, including child routes
const isRouteSelected = (routeName: string): boolean => {
  if (route.name === routeName) return true;
  
  // Check for child routes
  switch (routeName) {
    case 'lists':
      return route.name === 'list-details';
    case 'products':
      return route.name === 'product-category-details';
    case 'pantry':
      return route.name === 'pantry-category-details';
    default:
      return false;
  }
};
</script>

<template>
  <v-navigation-drawer
    permanent
    class="d-flex flex-column"
    style="height: 100vh"
  >
    <div class="d-flex flex-column h-100">
      <!-- Header -->
      <NavBarTitle icon="mdi-cart" title="Grocey" />

      <!-- Main navigation bar -->
      <v-list
        nav
        class="d-flex flex-column flex-grow-1 overflow-hidden nav-list"
        style="min-height: 0"
      >
        <!-- Lists Button -->
        <NavListItem
          icon="mdi-format-list-bulleted"
          title="Lists"
          :to="{ name: 'lists' }"
          :selected="isRouteSelected('lists')"
          @click="selectItem('lists')"
        />

        <!-- Products Button -->
        <NavListItem
          icon="mdi-package-variant-closed"
          title="Products"
          :to="{ name: 'products' }"
          :selected="isRouteSelected('products')"
          @click="selectItem('products')"
        />

        <!-- Pantry Button -->
        <NavListItem
          icon="mdi-fridge-outline"
          title="Pantry"
          :to="{ name: 'pantry' }"
          :selected="isRouteSelected('pantry')"
          @click="selectItem('pantry')"
        />
      </v-list>

      <!-- Bottom settings (Pinned to bottom) -->
      <div class="mt-auto">
        <NavListItem
          icon="mdi-cog"
          title="Settings"
          :to="{ name: 'settings' }"
          :selected="isRouteSelected('settings')"
          @click="selectItem('settings')"
        />
      </div>
    </div>
  </v-navigation-drawer>

  <v-app-bar height="72">
    <v-toolbar-title class="text-h4 font-weight-bold expanded-title">
      {{ pageTitle }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <NavBarButton icon="mdi-account-circle" @click="onProfileClick" />
  </v-app-bar>

  <v-main>
    <slot />
  </v-main>

  <!-- Profile Panel -->
  <ProfilePanel v-model="showProfilePanel" />
</template>

<style scoped>
/* Target the specific issue - v-list-item height constraints */
.nav-list :deep(.v-list-item) {
  min-height: auto !important;
  height: auto !important;
}

/* The content wrapper inside v-list-item that's constraining the text */
.nav-list :deep(.v-list-item__content) {
  min-height: auto !important;
  height: auto !important;
  padding: 0 !important;
}

/* Make sure the title can expand vertically */
.nav-list :deep(.v-list-item-title) {
  white-space: normal !important;
  line-height: normal !important;
  height: auto !important;
}

/* Expanded toolbar title styling */
.expanded-title {
  line-height: 1.3 !important;
  height: auto !important;
  min-height: 48px !important;
  display: flex !important;
  align-items: center !important;
  white-space: normal !important;
  word-wrap: break-word !important;
}
</style>
