<script setup lang="ts">
  import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import NavBarButton from '@/components/NavBar/NavBarButton.vue'
  import NavBarTitle from '@/components/NavBar/NavBarTitle.vue'
  import NavListItem from '@/components/NavBar/NavListItem.vue'
  import ProfilePanel from '@/components/Profile/ProfilePanel.vue'
  import apiClient from '@/services/api'
  import CategoryApi from '@/services/category'
  import ShoppingListApi from '@/services/shoppingList'

  const route = useRoute()

  const selectedItem = ref('lists') // Default selected item
  const showProfilePanel = ref(false)
  const dynamicTitle = ref<string>('')

  function selectItem (item: string) {
    selectedItem.value = item
  }

  // Dynamic data fetching functions
  async function getListName (id: string): Promise<string> {
    const numericId = Number(id)
    if (!Number.isFinite(numericId)) {
      return `List ${id}`
    }

    try {
      const list = await ShoppingListApi.get(numericId)
      if (list?.name) return list.name
    } catch (error) {
      console.error('Error fetching list name:', error)
      return `List ${id}`
    }
    return `List ${id}`
  }

  async function getCategoryName (id: string): Promise<string> {
    const numericId = Number(id)
    if (!Number.isFinite(numericId)) {
      return `Product Category ${id}`
    }

    try {
      const category = await CategoryApi.get(numericId)
      if (category?.name) return category.name
    } catch (error) {
      console.error('Error fetching category name:', error)
      return `Product Category ${id}`
    }

    return `Product Category ${id}`
  }

  async function getPantryName (id: string): Promise<string> {
    try {
      // Try to fetch from real API (pantry items would be different from categories)
      const { data } = await apiClient.get(`/pantry/${id}`)
      const pantryItem = data?.pantryItem ?? data
      if (pantryItem?.name) return pantryItem.name
      if (pantryItem?.title) return pantryItem.title
    } catch (error) {
      console.error('Error fetching pantry item name:', error)
    }

    return `Pantry Item ${id}`
  }

  // Function to fetch dynamic title based on route
  async function fetchDynamicTitle () {
    // Handle specific routes with dynamic names
    if (route.name === 'list-details' && route.params.id) {
      dynamicTitle.value = await getListName(route.params.id as string)
      return
    }

    if (route.name === 'product-category-details' && route.params.id) {
      dynamicTitle.value = await getCategoryName(route.params.id as string)
      return
    }

    if (route.name === 'pantry-category-details' && route.params.id) {
      dynamicTitle.value = await getPantryName(route.params.id as string)
      return
    }

    // For non-dynamic routes, clear the dynamic title
    dynamicTitle.value = ''
  }

  // Watch for route changes and fetch dynamic titles
  watch(() => [route.name, route.params.id], fetchDynamicTitle, {
    immediate: true,
  })

  // Listen for list updates to refresh the title
  function handleListUpdate (event: CustomEvent) {
    const { listId, newName } = event.detail
    // If we're currently viewing this list, update the dynamic title
    if (route.name === 'list-details' && route.params.id === String(listId)) {
      dynamicTitle.value = newName
    }
  }

  // Listen for category updates to refresh the title
  function handleCategoryUpdate (event: CustomEvent) {
    const { categoryId, newName } = event.detail
    // If we're currently viewing this category, update the dynamic title
    if (
      route.name === 'product-category-details'
      && route.params.id === String(categoryId)
    ) {
      dynamicTitle.value = newName
    }
  }

  // Set up event listeners for updates
  onMounted(() => {
    window.addEventListener('list-updated', handleListUpdate as EventListener)
    window.addEventListener(
      'category-updated',
      handleCategoryUpdate as EventListener,
    )
  })

  onBeforeUnmount(() => {
    window.removeEventListener('list-updated', handleListUpdate as EventListener)
    window.removeEventListener(
      'category-updated',
      handleCategoryUpdate as EventListener,
    )
  })

  // Watch for route changes and update selected item
  watch(
    () => route.name,
    newRouteName => {
      switch (newRouteName) {
        case 'lists':
        case 'list-details': {
          selectedItem.value = 'lists'

          break
        }
        case 'products':
        case 'product-category-details': {
          selectedItem.value = 'products'

          break
        }
        case 'pantry':
        case 'pantry-category-details': {
          selectedItem.value = 'pantry'

          break
        }
        case 'settings': {
          selectedItem.value = 'settings'

          break
        }
      // No default
      }
    },
    { immediate: true },
  )

  const pageTitle = computed(() => {
    // Use dynamic title if available
    if (dynamicTitle.value) {
      return dynamicTitle.value
    }

    // For other routes, use the meta title or fallback
    const matched = route.matched
      .slice()
      .reverse()
      .find(r => r.meta && (r.meta as any).title)
    if (matched) return (matched.meta as any).title

    // Final fallback to a nicer name based on route.name
    return route.name
      ? String(route.name).replace(/^\w/, c => c.toUpperCase())
      : 'Grocey'
  })

  function onProfileClick () {
    showProfilePanel.value = true
  }

  // Helper function to check if a route is selected, including child routes
  function isRouteSelected (routeName: string): boolean {
    if (route.name === routeName) return true

    // Check for child routes
    switch (routeName) {
      case 'lists': {
        return route.name === 'list-details'
      }
      case 'products': {
        return route.name === 'product-category-details'
      }
      case 'pantry': {
        return route.name === 'pantry-category-details'
      }
      default: {
        return false
      }
    }
  }
</script>

<template>
  <v-navigation-drawer
    class="d-flex flex-column"
    permanent
    style="height: 100vh"
  >
    <div class="d-flex flex-column h-100">
      <!-- Header -->
      <NavBarTitle icon="mdi-cart" title="Grocey" />

      <!-- Main navigation bar -->
      <v-list
        class="d-flex flex-column flex-grow-1 overflow-hidden nav-list"
        nav
        style="min-height: 0"
      >
        <!-- Lists Button -->
        <NavListItem
          icon="mdi-format-list-bulleted"
          :selected="isRouteSelected('lists')"
          title="Lists"
          :to="{ name: 'lists' }"
          @click="selectItem('lists')"
        />

        <!-- Products Button -->
        <NavListItem
          icon="mdi-package-variant-closed"
          :selected="isRouteSelected('products')"
          title="Products"
          :to="{ name: 'products' }"
          @click="selectItem('products')"
        />

        <!-- Pantry Button -->
        <NavListItem
          icon="mdi-fridge-outline"
          :selected="isRouteSelected('pantry')"
          title="Pantry"
          :to="{ name: 'pantry' }"
          @click="selectItem('pantry')"
        />
      </v-list>

      <!-- Bottom settings (Pinned to bottom) -->
      <div class="mt-auto">
        <NavListItem
          icon="mdi-cog"
          :selected="isRouteSelected('settings')"
          title="Settings"
          :to="{ name: 'settings' }"
          @click="selectItem('settings')"
        />
      </div>
    </div>
  </v-navigation-drawer>

  <v-app-bar height="72">
    <v-toolbar-title class="text-h4 font-weight-bold expanded-title">
      {{ pageTitle }}
    </v-toolbar-title>
    <v-spacer />
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
