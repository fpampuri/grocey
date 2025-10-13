<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import ConfirmDeleteDialog from '@/components/dialog/ConfirmDeleteDialog.vue'
  import CreateProductDialog from '@/components/dialog/CreateProductDialog.vue'
  import EditPantryDialog from '@/components/dialog/EditPantryDialog.vue'
  import MoveToPantryDialog from '@/components/dialog/MoveToPantryDialog.vue'
  import PantryItemCard from '@/components/Pantry/PantryItemCard.vue'
  import StandardButton from '@/components/StandardButton.vue'
  import ToastNotification from '@/components/ToastNotification.vue'
  import { useToast } from '@/composables/useToast'
  import { PantryApi, type Pantry as PantryApiType } from '@/services/pantry'
  import { PantryItemApi, type PantryItem as PantryItemApiType } from '@/services/pantryItem'
  import ProductApi from '@/services/product'
  import CategoryApi, { type Category as CategoryApiType } from '@/services/category'

  const route = useRoute()
  const router = useRouter()

  // Toast notifications
  const { showToast, toastMessage, toastType, showSuccess, showError } = useToast()

  type PantryItem = PantryItemApiType & {
    completed?: boolean
  }
  type Pantry = {
    id: number
    name: string
    title: string
    icon: string
    metadata?: any
    createdAt?: string
    updatedAt?: string
  }

  const pantryId = computed(() => Number(route.params.id))

  const pantryData = ref<Pantry | null>(null)
  const items = ref<PantryItem[]>([])
  const isLoading = ref(true)

  // Data for dialogs
  const allPantries = ref<PantryApiType[]>([])
  const allCategories = ref<CategoryApiType[]>([])

  // Dialog states
  const showMoveToPantryDialog = ref(false)
  const showCreateItemDialog = ref(false)
  const showEditPantryDialog = ref(false)
  const showDeletePantryDialog = ref(false)
  const selectedItemId = ref<number | null>(null)

  // Pantries for moving items between pantries
  const availablePantries = computed(() => {
    return allPantries.value
      .filter(pantry => pantry.id !== pantryId.value)
      .map(pantry => ({
        value: pantry.id!,
        label: pantry.name,
        icon: pantry.metadata?.icon || 'mdi-fridge',
      }))
  })

  // Categories for CreateProductDialog
  const availableCategories = computed(() => {
    return allCategories.value.map(cat => ({
      value: cat.id!,
      label: cat.name,
      icon: cat.metadata?.icon || 'mdi-box',
    }))
  })

  // Default category ID (use first category or Miscellaneous)
  const defaultCategoryId = computed<number | undefined>(() => {
    const misc = allCategories.value.find(
      cat => cat.name.toLowerCase() === 'miscellaneous'
    )
    return misc?.id ?? allCategories.value[0]?.id ?? undefined
  })

  // Current pantry data for EditPantryDialog
  const currentPantryData = computed(() => {
    if (!pantryData.value) return null
    return {
      id: pantryData.value.id,
      title: pantryData.value.name,
      icon: pantryData.value.icon || 'mdi-fridge',
    }
  })

  function goBack () {
    router.push('/pantry')
  }

  // Fetch pantries for moving items
  async function fetchPantries () {
    if (allPantries.value.length > 0) return // Already loaded

    try {
      const apiPantriesResponse = await PantryApi.getAll()
      allPantries.value = Array.isArray(apiPantriesResponse)
        ? apiPantriesResponse
        : (apiPantriesResponse as any)?.data || []
    } catch (error) {
      console.error('Error fetching pantries:', error)
      allPantries.value = []
    }
  }

  // Fetch categories for CreateProductDialog
  async function fetchCategories () {
    if (allCategories.value.length > 0) return // Already loaded

    try {
      const apiCategoriesResponse = await CategoryApi.getAll()
      allCategories.value = Array.isArray(apiCategoriesResponse)
        ? apiCategoriesResponse
        : (apiCategoriesResponse as any)?.data || []
    } catch (error) {
      console.error('Error fetching categories:', error)
      allCategories.value = []
    }
  }

  onMounted(async () => {
    isLoading.value = true
    try {
      // Fetch pantry data
      const apiPantry = await PantryApi.get(pantryId.value)
      pantryData.value = {
        id: apiPantry.id || pantryId.value,
        name: apiPantry.name,
        title: apiPantry.name,
        icon: apiPantry.metadata?.icon || 'mdi-fridge',
        metadata: apiPantry.metadata,
        createdAt: apiPantry.createdAt,
        updatedAt: apiPantry.updatedAt,
      }

      // Fetch items for this pantry
      const apiItemsResponse = await PantryItemApi.getAll(pantryId.value)
      const allItems: PantryItemApiType[] = Array.isArray(apiItemsResponse)
        ? apiItemsResponse
        : (apiItemsResponse as any)?.data || []

      items.value = allItems.map(item => ({
        ...item,
        completed: false, // Default UI state
      }))
    } catch (error) {
      console.error('Error loading pantry data:', error)
      pantryData.value = null
      items.value = []
    }
    isLoading.value = false
  })

  function handleToggleComplete (itemId: number, completed: boolean) {
    const item = items.value.find(p => p.id === itemId)
    if (item) item.completed = completed
  }

  async function handleUpdateQuantity (itemId: number, newQuantity: number) {
    const item = items.value.find(p => p.id === itemId)
    if (!item) return

    const oldQuantity = item.quantity

    try {
      // Update local state optimistically
      item.quantity = newQuantity

      // Update via API
      await PantryItemApi.modify(pantryId.value, itemId, {
        quantity: newQuantity,
        unit: item.unit,
      })

      showSuccess(`Quantity updated to ${newQuantity}`)
    } catch (error) {
      console.error('Error updating quantity:', error)
      // Rollback
      item.quantity = oldQuantity
      showError('Failed to update quantity. Please try again.')
    }
  }

  async function handleDeleteItem (itemId: number) {
    const itemToDelete = items.value.find(p => p.id === itemId)
    const itemName = itemToDelete?.product?.name || 'Item'

    try {
      console.log('Deleting pantry item:', itemId)

      // Delete via API
      await PantryItemApi.remove(pantryId.value, itemId)

      // Remove from local state
      items.value = items.value.filter(p => p.id !== itemId)

      showSuccess(`"${itemName}" removed from pantry!`)
    } catch (error) {
      console.error('Error deleting pantry item:', error)
      showError(`Failed to remove "${itemName}". Please try again.`)
    }
  }

  async function handleRenameProduct (payload: { id: number, name: string }) {
    // For pantry items, we need to update the product name
    const originalItem = items.value.find(p => p.id === payload.id)
    if (!originalItem?.product?.id) return

    const oldName = originalItem.product.name
    const newName = payload.name

    try {
      // Update local state optimistically
      const itemIndex = items.value.findIndex(p => p.id === payload.id)
      if (itemIndex !== -1 && items.value[itemIndex].product) {
        items.value[itemIndex].product!.name = newName
      }

      // Update the product name via ProductApi
      await ProductApi.modify(originalItem.product.id, {
        name: newName,
      })

      showSuccess(`Item renamed from "${oldName}" to "${newName}"!`)
    } catch (error) {
      console.error('Error renaming item:', error)

      // Rollback
      const itemIndex = items.value.findIndex(p => p.id === payload.id)
      if (itemIndex !== -1 && items.value[itemIndex].product) {
        items.value[itemIndex].product!.name = oldName
      }

      showError(`Failed to rename "${oldName}". Please try again.`)
    }
  }

  async function handleMoveToPantry (itemId: number) {
    selectedItemId.value = itemId
    await fetchPantries()
    showMoveToPantryDialog.value = true
  }

  async function confirmMoveToPantry (data: { pantryId: number }) {
    if (!selectedItemId.value) return

    const itemToMove = items.value.find(p => p.id === selectedItemId.value)
    if (!itemToMove?.product?.id) {
      showError('Cannot move item: product not found.')
      return
    }

    const itemName = itemToMove.product.name
    const targetPantry = allPantries.value.find(pantry => pantry.id === data.pantryId)
    const targetPantryName = targetPantry?.name || 'pantry'

    try {
      // Step 1: Add item to target pantry
      await PantryItemApi.add(data.pantryId, {
        product: { id: itemToMove.product.id },
        quantity: itemToMove.quantity,
        unit: itemToMove.unit,
      })

      // Step 2: Remove item from current pantry
      await PantryItemApi.remove(pantryId.value, selectedItemId.value)

      // Remove from local state
      items.value = items.value.filter(p => p.id !== selectedItemId.value)

      showSuccess(`"${itemName}" moved to ${targetPantryName}!`)
    } catch (error) {
      console.error('Error moving item:', error)
      showError(`Failed to move "${itemName}". Please try again.`)
    }

    showMoveToPantryDialog.value = false
    selectedItemId.value = null
  }

  async function handleAddItem () {
    await fetchCategories()
    showCreateItemDialog.value = true
  }

  function openEditPantryDialog () {
    showEditPantryDialog.value = true
  }

  function openDeletePantryDialog () {
    showDeletePantryDialog.value = true
  }

  async function onItemCreated (itemData: {
    name: string
    categoryId: number
  }) {
    try {
      // Step 1: Create the product first (required by backend)
      // We use the categoryId from the dialog (which is actually the pantryId in this context)
      const newProduct = await ProductApi.add({
        name: itemData.name,
        category: { id: itemData.categoryId }, // This will create product in a category
      })

      // Step 2: Add the newly created product to the pantry
      const newItem = await PantryItemApi.add(pantryId.value, {
        product: { id: newProduct.id! },
        quantity: 1,
        unit: 'unit',
      })

      // Add to local state
      items.value.push({
        ...newItem,
        completed: false,
      })

      showCreateItemDialog.value = false
      showSuccess(`Item "${itemData.name}" added to pantry!`)
    } catch (error) {
      console.error('Error creating pantry item:', error)
      const errorMessage = (error as any)?.message || ''
      if (
        errorMessage.toLowerCase().includes('product already exists')
        || errorMessage.toLowerCase().includes('already exists')
      ) {
        showError(
          `Cannot add item, product "${itemData.name}" already exists`,
        )
      } else {
        showError(`Failed to add item "${itemData.name}". Please try again.`)
      }
    }
  }

  async function handleEditPantry (data: {
    id: number
    name: string
    icon: string
  }) {
    if (!pantryData.value) return

    try {
      // Update pantry via API
      await PantryApi.modify(data.id, {
        name: data.name,
        metadata: { icon: data.icon },
      })

      // Update local state
      pantryData.value.name = data.name
      pantryData.value.title = data.name
      pantryData.value.icon = data.icon
      pantryData.value.metadata = {
        ...pantryData.value.metadata,
        icon: data.icon,
      }

      showSuccess('Pantry updated successfully.')

      // Trigger a custom event for navbar updates
      window.dispatchEvent(
        new CustomEvent('pantry-updated', {
          detail: {
            pantryId: pantryId.value,
            newName: data.name,
          },
        }),
      )
    } catch (error) {
      console.error('Error updating pantry:', error)
      showError('Unable to update pantry.')
    } finally {
      showEditPantryDialog.value = false
    }
  }

  async function handleDeletePantry () {
    if (!pantryData.value) return

    try {
      // Delete all items first
      for (const item of items.value) {
        if (item.id) {
          await PantryItemApi.remove(pantryId.value, item.id)
        }
      }

      // Delete pantry via API
      await PantryApi.remove(pantryId.value)

      showSuccess('Pantry deleted successfully.')

      // Navigate back after delay
      setTimeout(() => {
        goBack()
      }, 1500)
    } catch (error) {
      console.error('Error deleting pantry:', error)
      showError('Unable to delete pantry.')
    } finally {
      showDeletePantryDialog.value = false
    }
  }
</script>

<template>
  <div class="pa-4">
    <v-container fluid>
      <v-row align="center" class="mb-4">
        <v-col cols="auto">
          <v-btn
            icon="mdi-arrow-left"
            size="large"
            variant="text"
            @click="goBack"
          />
        </v-col>
        <v-spacer />
        <v-col class="d-flex justify-end align-center u-gap-8">
          <StandardButton
            icon="mdi-plus"
            title="Add Item"
            @click="handleAddItem"
          />

          <v-menu v-if="pantryData" offset-y>
            <template #activator="{ props: activator }">
              <v-btn
                v-bind="activator"
                class="pantry-menu-btn"
                icon="mdi-dots-horizontal"
                size="large"
                variant="text"
              />
            </template>
            <v-list class="action-menu">
              <v-list-item class="menu-item" @click="openEditPantryDialog">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-pencil</v-icon>
                </template>
                <v-list-item-title class="menu-title">Edit Pantry</v-list-item-title>
              </v-list-item>

              <v-list-item class="menu-item" @click="openDeletePantryDialog">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-delete</v-icon>
                </template>
                <v-list-item-title class="menu-title">Delete Pantry</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div v-if="isLoading">
            <v-card class="pa-4">
              <v-card-text class="text-center">
                <v-progress-circular indeterminate />
                <p class="mt-2">Loading pantry items...</p>
              </v-card-text>
            </v-card>
          </div>
          <div v-else-if="pantryData">
            <div v-if="items.length > 0">
              <PantryItemCard
                v-for="item in items"
                :id="item.id || 0"
                :key="item.id || item.product?.name"
                :quantity="item.quantity"
                :title="item.product?.name || 'Unknown Item'"
                :unit="item.unit"
                @delete="(id) => handleDeleteItem(id)"
                @move-to-pantry="handleMoveToPantry"
                @rename="handleRenameProduct"
                @update-quantity="(quantity) => handleUpdateQuantity(item.id || 0, quantity)"
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon color="grey-lighten-1" icon="mdi-inbox" size="64" />
              <p class="mt-2 text-h6 text-medium-emphasis">
                This pantry is empty
              </p>
              <p class="text-body-2 text-medium-emphasis">
                Add items to your pantry to get started
              </p>
            </div>
          </div>
          <div v-else>
            <v-card class="pa-4">
              <v-card-title>Pantry Not Found</v-card-title>
              <v-card-text>
                <p>
                  No pantry found with ID: <strong>{{ pantryId }}</strong>
                </p>
                <v-btn class="mt-2" color="primary" @click="goBack">
                  Back to Pantry
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <!-- Move to Pantry Dialog -->
    <MoveToPantryDialog
      v-model="showMoveToPantryDialog"
      :pantries="availablePantries"
      @move-to-pantry="confirmMoveToPantry"
    />

    <!-- Create Item Dialog (reusing CreateProductDialog with pantry context) -->
    <CreateProductDialog
      v-model="showCreateItemDialog"
      :categories="availableCategories"
      :default-category-id="defaultCategoryId"
      @create-product="onItemCreated"
    />

    <!-- Edit Pantry Dialog -->
    <EditPantryDialog
      v-model="showEditPantryDialog"
      :pantry-data="currentPantryData"
      @edit-pantry="handleEditPantry"
    />

    <!-- Delete Pantry Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeletePantryDialog"
      description="This action cannot be undone. All items in this pantry will also be permanently deleted."
      :item-name="pantryData?.title"
      item-type="pantry"
      @confirm="handleDeletePantry"
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
.pantry-menu-btn {
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.pantry-menu-btn:hover {
  background-color: var(--primary-green);
  color: white;
}

.action-menu {
  min-width: 150px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-item {
  padding: 12px 16px;
  color: var(--text-primary);
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-icon {
  margin-right: 12px;
  transition: color 0.2s ease;
  color: var(--text-primary) !important;
}

.menu-item:hover {
  background-color: var(--primary-green);
  color: white;
}

.menu-item:hover .menu-icon {
  color: white !important;
}

.menu-title {
  font-weight: 600;
}
</style>
