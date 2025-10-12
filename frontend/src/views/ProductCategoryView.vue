<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import AddToListDialog from '@/components/dialog/AddToListDialog.vue'
  import CreateProductDialog from '@/components/dialog/CreateProductDialog.vue'
  import MoveToCategoryDialog from '@/components/dialog/MoveToCategoryDialog.vue'
  import Card from '@/components/Products/ProductItemCard.vue'
  import StandardButton from '@/components/StandardButton.vue'
  import ToastNotification from '@/components/ToastNotification.vue'
  import { useToast } from '@/composables/useToast'
  import CategoryApi, { type Category as CategoryApiType } from '@/services/category'
  import ListItemApi from '@/services/listItem'
  import ProductApi, { type Product as ProductApiType } from '@/services/product'
  import ShoppingListApi, { type ShoppingList } from '@/services/shoppingList'

  const route = useRoute()
  const router = useRouter()

  // Toast notifications
  const { showToast, toastMessage, toastType, showSuccess, showError } = useToast()

  type Product = ProductApiType & { completed?: boolean, quantity?: number, expiryDate?: string }
  type Category = {
    id: number
    name?: string
    title: string
    icon: string
    metadata?: any
    createdAt?: string
    updatedAt?: string
  }

  const categoryId = computed(() => Number(route.params.id))
  const isFromPantry = computed(() => route.path.includes('/pantry/'))

  const categoryData = ref<Category | null>(null)
  const products = ref<Product[]>([])
  const isLoading = ref(true)

  // Data for dialogs
  const allCategories = ref<CategoryApiType[]>([])
  const allLists = ref<ShoppingList[]>([])

  // Dialog states
  const showMoveDialog = ref(false)
  const showAddToListDialog = ref(false)
  const showCreateProductDialog = ref(false)
  const selectedProductId = ref<number | null>(null)

  // Categories for MoveToCategoryDialog - populated from API
  const availableCategories = computed(() => {
    return allCategories.value
      .filter(cat => cat.id !== categoryId.value)
      .map(cat => ({
        value: cat.id!,
        label: cat.name,
        icon: cat.metadata?.icon || 'mdi-box',
      }))
  })

  // Lists for AddToListDialog - populated from API
  const availableLists = computed(() => {
    return allLists.value.map(list => ({
      value: list.id!,
      label: list.name,
      icon: list.metadata?.icon || 'mdi-format-list-bulleted',
    }))
  })

  // Current category for CreateProductDialog - only show current category (read-only)
  const currentCategoryOption = computed(() => {
    if (!categoryData.value) return []
    return [{
      value: categoryData.value.id,
      label: categoryData.value.name || categoryData.value.title,
      icon: categoryData.value.icon || 'mdi-box',
    }]
  })

  function goBack () {
    if (isFromPantry.value) {
      router.push('/pantry')
    } else {
      router.push('/products')
    }
  }

  // Fetch categories for MoveToCategoryDialog (with caching)
  async function fetchCategories () {
    if (allCategories.value.length > 0 || isFromPantry.value) return // Already loaded or not needed

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

  // Fetch lists for AddToListDialog (with caching)
  async function fetchLists () {
    if (allLists.value.length > 0) return // Already loaded

    try {
      const apiListsResponse = await ShoppingListApi.getAll()
      allLists.value = Array.isArray(apiListsResponse)
        ? apiListsResponse
        : (apiListsResponse as any)?.data || []
    } catch (error) {
      console.error('Error fetching lists:', error)
      allLists.value = []
    }
  }

  onMounted(async () => {
    isLoading.value = true
    try {
      if (isFromPantry.value) {
        // For pantry, show empty state - no mock data
        categoryData.value = null
        products.value = []
      } else {
        // For categories, use real API calls
        const apiCategory = await CategoryApi.get(categoryId.value)
        categoryData.value = {
          id: apiCategory.id || categoryId.value,
          name: apiCategory.name,
          title: apiCategory.name,
          icon: apiCategory.metadata?.icon || 'mdi-box',
          metadata: apiCategory.metadata,
          createdAt: apiCategory.createdAt,
          updatedAt: apiCategory.updatedAt,
        }

        // Fetch products for this category
        const apiProductsResponse = await ProductApi.getAll()

        // Handle different possible response formats
        const allProducts: ProductApiType[] = Array.isArray(apiProductsResponse)
          ? apiProductsResponse
          : (apiProductsResponse as any)?.data || []

        products.value = allProducts
          .filter(product => product.category?.id === categoryId.value)
          .map(product => ({
            ...product,
            completed: false, // Default UI state
          }))
      }
    } catch (error) {
      console.error('Error loading category data:', error)
      // Set empty state on error
      categoryData.value = null
      products.value = []
    }
    isLoading.value = false
  })

  function handleToggleComplete (itemId: number, completed: boolean) {
    const item = products.value.find(p => p.id === itemId)
    if (item) item.completed = completed
  }

  async function handleDeleteItem (itemId: number) {
    const productToDelete = products.value.find(p => p.id === itemId)
    const productName = productToDelete?.name || 'Product'

    try {
      console.log('Deleting product:', itemId)

      // Delete via API first
      await ProductApi.remove(itemId)

      // Remove from local state only after successful API call
      products.value = products.value.filter(p => p.id !== itemId)

      showSuccess(`"${productName}" deleted successfully!`)
      console.log('Product deleted successfully')
    } catch (error) {
      console.error('Error deleting product:', error)
      showError(`Failed to delete "${productName}". Please try again.`)
    }
  }

  async function handleRenameProduct (payload: { id: number, name: string }) {
    // Store original product for potential rollback
    const originalProduct = products.value.find(p => p.id === payload.id)
    if (!originalProduct) return

    const oldName = originalProduct.name
    const newName = payload.name

    try {
      // Update local state optimistically (show change immediately)
      const itemIndex = products.value.findIndex(p => p.id === payload.id)
      if (itemIndex !== -1) {
        products.value[itemIndex] = {
          ...products.value[itemIndex],
          name: payload.name,
        }
      }

      // Update via API
      const apiResponse = await ProductApi.modify(payload.id, { name: payload.name })

      // Handle different possible response formats
      const updatedProduct = Array.isArray(apiResponse)
        ? apiResponse[0]
        : (apiResponse as any)?.data || apiResponse

      // Update with server response to ensure consistency
      if (itemIndex !== -1 && updatedProduct) {
        products.value[itemIndex] = {
          ...products.value[itemIndex],
          ...updatedProduct,
          completed: products.value[itemIndex].completed, // Preserve UI state
        }
      }

      showSuccess(`Product renamed from "${oldName}" to "${newName}"!`)
      console.log('Product renamed successfully')
    } catch (error) {
      console.error('Error renaming product:', error)

      // Rollback the optimistic update
      const itemIndex = products.value.findIndex(p => p.id === payload.id)
      if (itemIndex !== -1 && originalProduct) {
        products.value[itemIndex] = originalProduct
      }

      showError(`Failed to rename "${oldName}". Please try again.`)
    }
  }

  async function handleMoveProduct (productId: number) {
    selectedProductId.value = productId
    await fetchCategories()
    showMoveDialog.value = true
  }

  async function handleAddToList (productId: number) {
    selectedProductId.value = productId
    await fetchLists()
    showAddToListDialog.value = true
  }

  function handleAddProduct () {
    showCreateProductDialog.value = true
  }

  async function onProductCreated (productData: { name: string, categoryId: number }) {
    try {
      // Create the product via API
      const newProduct = await ProductApi.add({
        name: productData.name,
        category: { id: productData.categoryId },
      })

      // Add the new product to the current products list
      products.value.push({
        ...newProduct,
        completed: false, // Default UI state
      })

      showCreateProductDialog.value = false
      showSuccess(`Product "${productData.name}" created successfully!`)
    } catch (error) {
      // Check if the error is due to product already existing
      const errorMessage = (error as any)?.message || ''
      if (errorMessage.toLowerCase().includes('product already exists')
        || errorMessage.toLowerCase().includes('already exists')) {
        showError(`Cannot create product, product "${productData.name}" already exists`)
      } else {
        showError(`Failed to create product "${productData.name}". Please try again.`)
      }
    }
  }

  async function confirmMoveToCategory (data: { categoryId: number }) {
    if (!selectedProductId.value) return

    const productToMove = products.value.find(p => p.id === selectedProductId.value)
    const productName = productToMove?.name || 'Product'
    const targetCategory = allCategories.value.find(cat => cat.id === data.categoryId)
    const targetCategoryName = targetCategory?.name || 'category'

    try {
      console.log('Moving product', selectedProductId.value, 'to category', data.categoryId)

      // Update product category via API
      await ProductApi.modify(selectedProductId.value, {
        name: productName, // Required by backend validation
        category: { id: data.categoryId },
      })

      // Remove product from current list (since it's now in a different category)
      products.value = products.value.filter(p => p.id !== selectedProductId.value)

      showSuccess(`"${productName}" moved to ${targetCategoryName}!`)
    } catch (error) {
      console.error('Error moving product:', error)
      showError(`Failed to move "${productName}". Please try again.`)
    }

    showMoveDialog.value = false
    selectedProductId.value = null
  }

  async function confirmAddToList (data: { listId: number }) {
    if (!selectedProductId.value) return

    const productToAdd = products.value.find(p => p.id === selectedProductId.value)
    const productName = productToAdd?.name || 'Product'
    const targetList = allLists.value.find(list => list.id === data.listId)
    const targetListName = targetList?.name || 'list'

    try {
      console.log('Adding product', selectedProductId.value, 'to list', data.listId)

      // Add product to list via API
      await ListItemApi.add(data.listId, {
        product: { id: selectedProductId.value },
        quantity: 1, // Default quantity
        unit: 'unit', // Default unit
      })

      showSuccess(`"${productName}" added to ${targetListName}!`)
    } catch (error) {
      console.error('Error adding product to list:', error)
      showError(`Failed to add "${productName}" to ${targetListName}. Please try again.`)
    }

    showAddToListDialog.value = false
    selectedProductId.value = null
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
        <v-col cols="auto">
          <StandardButton
            icon="mdi-plus"
            title="Add Product"
            @click="handleAddProduct"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">
          <div v-if="isLoading">
            <v-card class="pa-4">
              <v-card-text class="text-center">
                <v-progress-circular indeterminate />
                <p class="mt-2">Loading {{ isFromPantry ? 'pantry items' : 'category items' }}...</p>
              </v-card-text>
            </v-card>
          </div>
          <div v-else-if="categoryData">
            <div v-if="products.length > 0">
              <Card
                v-for="item in products"
                :id="item.id || 0"
                :key="item.id || item.name"
                :completed="item.completed || false"
                :title="item.name"
                @add-to-list="handleAddToList"
                @delete="(id) => handleDeleteItem(id)"
                @move="handleMoveProduct"
                @rename="handleRenameProduct"
                @toggle-complete="(completed) => handleToggleComplete(item.id || 0, completed)"
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon color="grey-lighten-1" icon="mdi-inbox" size="64" />
              <p class="mt-2 text-h6 text-medium-emphasis">
                This {{ isFromPantry ? 'pantry category' : 'category' }} is empty
              </p>
              <p class="text-body-2 text-medium-emphasis">
                {{ isFromPantry ? 'Add items to your pantry to get started' : 'Add items to this category to get started' }}
              </p>
            </div>
          </div>
          <div v-else>
            <v-card class="pa-4">
              <v-card-title>{{ isFromPantry ? 'Pantry Category' : 'Category' }} Not Found</v-card-title>
              <v-card-text>
                <p>
                  No {{ isFromPantry ? 'pantry category' : 'category' }} found with ID: <strong>{{ categoryId }}</strong>
                </p>
                <v-btn class="mt-2" color="primary" @click="goBack">
                  {{ isFromPantry ? 'Back to Pantry' : 'Back to Products' }}
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <!-- Move to Category Dialog -->
    <MoveToCategoryDialog
      v-model="showMoveDialog"
      :categories="availableCategories"
      @move-to-category="confirmMoveToCategory"
    />

    <!-- Add to List Dialog -->
    <AddToListDialog
      v-model="showAddToListDialog"
      :lists="availableLists"
      @add-to-list="confirmAddToList"
    />

    <!-- Create Product Dialog -->
    <CreateProductDialog
      v-model="showCreateProductDialog"
      :categories="currentCategoryOption"
      @create-product="onProductCreated"
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

</style>
