<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import ConfirmDeleteDialog from '@/components/dialog/ConfirmDeleteDialog.vue'
  import CreatePantryDialog from '@/components/dialog/CreatePantryDialog.vue'
  import EditPantryDialog from '@/components/dialog/EditPantryDialog.vue'
  import ProductCard from '@/components/Products/ProductCard.vue'
  import SearchBar from '@/components/SearchBar.vue'
  import StandardButton from '@/components/StandardButton.vue'
  import ToastNotification from '@/components/ToastNotification.vue'
  import { useToast } from '@/composables/useToast'
  import { PantryApi, type Pantry as PantryApiType } from '@/services/pantry'
  import { PantryItemApi, type PantryItem as PantryItemApiType } from '@/services/pantryItem'

  const router = useRouter()

  // Toast notifications
  const { showToast, toastMessage, toastType, showSuccess, showError } = useToast()

  // Pantries and Items state - extending API types for UI needs
  type PantryItem = PantryItemApiType
  type Pantry = PantryApiType & {
    title: string // UI display name (mapped from 'name')
    icon: string // UI icon (from metadata)
    items: PantryItem[] // UI-specific aggregated items
  }

  const pantries = ref<Pantry[]>([])
  const isLoading = ref(true)
  const searchQuery = ref('')
  const sortBy = ref('Name')
  const showCreateDialog = ref(false)
  const showEditDialog = ref(false)
  const showDeleteDialog = ref(false)
  const pantryToDelete = ref<Pantry | null>(null)
  const pantryToEdit = ref<Pantry | null>(null)

  const filteredPantries = computed<Pantry[]>(() => {
    const q = searchQuery.value.trim().toLowerCase()
    if (!q) return pantries.value
    return pantries.value
      .map((pantry: Pantry) => ({
        ...pantry,
        items: pantry.items.filter((item: PantryItem) =>
          item.product?.name?.toLowerCase().includes(q),
        ),
      }))
      .filter(
        (pantry: Pantry) =>
          pantry.title.toLowerCase().includes(q) || pantry.items.length > 0,
      )
  })

  function sortPantries (list: Pantry[]): Pantry[] {
    if (sortBy.value === 'Name') {
      return [...list].sort((a: Pantry, b: Pantry) =>
        a.title.localeCompare(b.title),
      )
    }
    if (sortBy.value === 'Items') {
      return [...list].sort(
        (a: Pantry, b: Pantry) => b.items.length - a.items.length,
      )
    }
    return list
  }

  onMounted(async () => {
    isLoading.value = true
    try {
      const apiPantriesResponse = await PantryApi.getAll()

      const apiPantries: PantryApiType[] = Array.isArray(apiPantriesResponse)
        ? apiPantriesResponse
        : (apiPantriesResponse as any)?.data || []

      // Normalize pantries for UI
      const normalizedPantries: Pantry[] = apiPantries.map(pantry => ({
        ...pantry,
        title: pantry.name,
        icon: pantry.metadata?.icon || 'mdi-fridge',
        items: [],
      }))

      // Load items for each pantry
      for (const pantry of normalizedPantries) {
        if (pantry.id) {
          try {
            const items = await PantryItemApi.getAll(pantry.id)
            const itemsArray = Array.isArray(items) ? items : (items as any)?.data || []
            pantry.items = itemsArray
          } catch (error) {
            console.error(`Error loading items for pantry ${pantry.id}:`, error)
            pantry.items = []
          }
        }
      }

      pantries.value = normalizedPantries
    } catch (error) {
      console.error('Error loading pantries:', error)
      pantries.value = []
    } finally {
      isLoading.value = false
    }
  })

  function handlePantryClick (pantry: Pantry) {
    router.push(`/pantry/${pantry.id}`)
  }

  function handleSelectionToggle (isSelected: boolean) {
    console.log('Selection toggled:', isSelected ? 'SELECTED' : 'UNSELECTED')
  }

  function handleEdit (pantryId: number) {
    const pantry = pantries.value.find(p => p.id === pantryId)
    if (!pantry) return

    pantryToEdit.value = pantry
    showEditDialog.value = true
  }

  async function handleEditPantry (data: { id: number, name: string, icon: string }) {
    try {
      // Update pantry via API
      const target = pantries.value.find(p => p.id === data.id)

      await PantryApi.modify(data.id, {
        name: data.name,
        metadata: { icon: data.icon },
      })

      // Update local state
      const pantry = target
      if (pantry) {
        pantry.name = data.name
        pantry.title = data.name
        pantry.icon = data.icon
        if (pantry.metadata) {
          pantry.metadata.icon = data.icon
        }
      }

      showEditDialog.value = false
      pantryToEdit.value = null
      showSuccess(`Pantry "${data.name}" updated successfully!`)
    } catch (error) {
      console.error('Error updating pantry:', error)
      showError('Failed to update pantry. Please try again.')
    }
  }

  function handleDelete (pantryId: number) {
    const pantry = pantries.value.find(p => p.id === pantryId)
    if (!pantry) return

    pantryToDelete.value = pantry
    showDeleteDialog.value = true
  }

  async function confirmDelete () {
    if (!pantryToDelete.value?.id) return

    try {
      const pantryName = pantryToDelete.value.title
      const pantryId = pantryToDelete.value.id
      console.log('🗑️ Deleting pantry:', pantryName)

      // Get all items in the pantry that will be deleted
      const itemsToDelete = pantryToDelete.value.items || []

      // Delete all items in the pantry first
      for (const item of itemsToDelete) {
        if (item.id) {
          try {
            await PantryItemApi.remove(pantryId, item.id)
            console.log(`🗑️ Deleted item: ${item.product?.name}`)
          } catch (error) {
            console.error(`Failed to delete item ${item.product?.name}:`, error)
          }
        }
      }

      // Delete pantry via API
      await PantryApi.remove(pantryId)

      // Remove the pantry from local state
      pantries.value = pantries.value.filter(
        pantry => pantry.id !== pantryId,
      )

      console.log('✅ Pantry and its items deleted successfully')

      // Reset state
      showDeleteDialog.value = false
      pantryToDelete.value = null
      showSuccess(`Pantry "${pantryName}" and its ${itemsToDelete.length} item(s) deleted successfully!`)
    } catch (error) {
      console.error('Error deleting pantry:', error)
      showError('Failed to delete pantry. Please try again.')
    }
  }

  async function handleCreatePantry (pantryData: { name: string, icon: string }) {
    try {
      // Create pantry via API
      const newPantry = await PantryApi.add({
        name: pantryData.name,
        metadata: { icon: pantryData.icon || 'mdi-fridge' },
      })

      // Add to local state with UI format
      const pantryForUI: Pantry = {
        ...newPantry,
        title: newPantry.name,
        icon: newPantry.metadata?.icon || 'mdi-fridge',
        items: [],
      }

      pantries.value.unshift(pantryForUI)
      showCreateDialog.value = false
      showSuccess(`Pantry "${pantryData.name}" created successfully!`)
    } catch (error) {
      console.error('Error creating pantry:', error)
      showError('Failed to create pantry. Please try again.')
    }
  }

  // Transform pantry for dialog
  const pantryDataForDialog = computed(() => {
    if (!pantryToEdit.value || !pantryToEdit.value.id) return null
    return {
      id: pantryToEdit.value.id,
      title: pantryToEdit.value.title,
      icon: pantryToEdit.value.icon,
    }
  })
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add Section -->
      <v-row align="center" class="mb-6">
        <v-col cols="12" md="8">
          <SearchBar
            v-model="searchQuery"
            placeholder="Search pantries or items..."
          />
        </v-col>
        <v-col class="d-flex justify-end align-end" cols="12" md="4">
          <StandardButton
            icon="mdi-plus"
            title="Add Pantry"
            @click="showCreateDialog = true"
          />
        </v-col>
      </v-row>

      <!-- Sort Section -->
      <v-row align="center" class="mb-4">
        <v-spacer />
        <v-col cols="auto">
          <div class="d-flex align-center">
            <span class="mr-3 text-medium-emphasis">Sort by:</span>
            <v-select
              v-model="sortBy"
              density="compact"
              hide-details
              :items="['Name', 'Items']"
              class="u-min-width-100"
              variant="outlined"
            />
          </div>
        </v-col>
      </v-row>

      <!-- Loading state -->
      <div v-if="isLoading" class="text-center pa-8">
        <v-progress-circular indeterminate size="64" />
        <p class="mt-4 text-h6">Loading pantries...</p>
      </div>

      <!-- Pantries grid -->
      <v-row v-else>
        <v-col
          v-for="pantry in sortPantries(filteredPantries)"
          :key="pantry.id"
          cols="12"
          lg="4"
          md="6"
        >
          <ProductCard
            :icon="pantry.icon"
            :items-count="pantry.items.length"
            :title="pantry.title"
            @click="() => handlePantryClick(pantry)"
            @delete="() => pantry.id && handleDelete(pantry.id)"
            @edit="() => pantry.id && handleEdit(pantry.id)"
          />
        </v-col>
      </v-row>

      <!-- Empty state - no pantries at all -->
      <div
        v-if="!isLoading && pantries.length === 0"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-fridge-outline" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">
          There are no pantries created
        </p>
        <p class="text-body-2 text-medium-emphasis">
          Create your first pantry to get started
        </p>
      </div>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredPantries.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-magnify" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">
          No pantries or items found
        </p>
        <p class="text-body-2 text-medium-emphasis">
          Try adjusting your search terms
        </p>
      </div>
    </v-container>

    <!-- Create Pantry Dialog -->
    <CreatePantryDialog
      v-model="showCreateDialog"
      @create-pantry="handleCreatePantry"
    />

    <!-- Edit Pantry Dialog -->
    <EditPantryDialog
      v-model="showEditDialog"
      :pantry-data="pantryDataForDialog"
      @edit-pantry="handleEditPantry"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      description="This action cannot be undone. All items in this pantry will be permanently deleted."
      :item-name="pantryToDelete?.title"
      item-type="pantry"
      @confirm="confirmDelete"
    />

    <!-- Toast Notification -->
    <ToastNotification
      v-model="showToast"
      :message="toastMessage"
      :type="toastType"
    />
  </div>
</template>

<style scoped>
.add-list-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
  min-width: 120px;
}

@media (max-width: 960px) {
  .text-md-right {
    text-align: left !important;
  }
}

.product-item {
  border-radius: 12px;
  border: 1px solid var(--v-border-color, #e0e0e0);
  transition: box-shadow 0.2s ease;
  cursor: pointer;
}

.product-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
