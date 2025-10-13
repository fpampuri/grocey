<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import ConfirmDeleteDialog from '@/components/dialog/ConfirmDeleteDialog.vue'
  import CreateCategoryDialog from '@/components/dialog/CreateCategoryDialog.vue'
  import CreateProductDialog from '@/components/dialog/CreateProductDialog.vue'
  import EditCategoryDialog from '@/components/dialog/EditCategoryDialog.vue'
  import ProductCard from '@/components/Products/ProductCard.vue'
  import SearchBar from '@/components/SearchBar.vue'
  import StandardButton from '@/components/StandardButton.vue'
  import ToastNotification from '@/components/ToastNotification.vue'
  import { useToast } from '@/composables/useToast'
  import CategoryApi, { type Category as CategoryApiType } from '@/services/category'
  import ProductApi, { type Product as ProductApiType } from '@/services/product'

  const router = useRouter()

  // Toast notifications
  const { showToast, toastMessage, toastType, showSuccess, showError } = useToast()

  // Categories and Products state - extending API types for UI needs
  type Product = ProductApiType & { price: number } // Default price, not used in UI
  type Category = CategoryApiType & {
    title: string // UI display name (mapped from 'name')
    icon: string // UI icon (from metadata)
    products: Product[] // UI-specific aggregated products
  }

  const categories = ref<Category[]>([])
  const DEFAULT_CATEGORY_NAME = 'Miscellaneous'
  const DEFAULT_CATEGORY_ICON = 'mdi-shape'
  const DEFAULT_CATEGORY_NAME_LOWER = DEFAULT_CATEGORY_NAME.toLowerCase()
  const isLoading = ref(true)
  const searchQuery = ref('')
  const sortBy = ref('Name')
  const showCreateDialog = ref(false)
  const showEditDialog = ref(false)
  const showCreateProductDialog = ref(false)
  const showDeleteDialog = ref(false)
  const categoryToDelete = ref<Category | null>(null)
  const categoryToEdit = ref<Category | null>(null)

  const filteredCategories = computed<Category[]>(() => {
    const q = searchQuery.value.trim().toLowerCase()
    if (!q) return categories.value
    return categories.value
      .map((cat: Category) => ({
        ...cat,
        products: cat.products.filter((p: Product) =>
          p.name.toLowerCase().includes(q),
        ),
      }))
      .filter(
        (cat: Category) =>
          cat.title.toLowerCase().includes(q) || cat.products.length > 0,
      )
  })

  function sortCategories (list: Category[]): Category[] {
    if (sortBy.value === 'Name') {
      return [...list].sort((a: Category, b: Category) =>
        a.title.localeCompare(b.title),
      )
    }
    if (sortBy.value === 'Items') {
      return [...list].sort(
        (a: Category, b: Category) => b.products.length - a.products.length,
      )
    }
    return list
  }

  onMounted(async () => {
    isLoading.value = true
    try {
      const [apiCategoriesResponse, apiProductsResponse] = await Promise.all([
        CategoryApi.getAll(),
        ProductApi.getAll(),
      ])

      const apiCategories: CategoryApiType[] = Array.isArray(apiCategoriesResponse)
        ? apiCategoriesResponse
        : (apiCategoriesResponse as any)?.data || []

      const apiProducts: ProductApiType[] = Array.isArray(apiProductsResponse)
        ? apiProductsResponse
        : (apiProductsResponse as any)?.data || []

      // Normalize categories for UI
      const normalizedCategories: Category[] = apiCategories.map(category => ({
        ...category,
        title: category.name,
        icon: category.metadata?.icon || 'mdi-box',
        products: [],
      }))

      let defaultCategory
        = normalizedCategories.find(
          category => category.title.toLowerCase() === DEFAULT_CATEGORY_NAME_LOWER,
        ) ?? null

      if (defaultCategory) {
        defaultCategory.products = []
      } else {
        try {
          const createdCategory = await CategoryApi.add({
            name: DEFAULT_CATEGORY_NAME,
            metadata: { icon: DEFAULT_CATEGORY_ICON },
          })
          defaultCategory = {
            ...createdCategory,
            title: createdCategory.name,
            icon: createdCategory.metadata?.icon || DEFAULT_CATEGORY_ICON,
            products: [],
          }
          normalizedCategories.unshift(defaultCategory)
        } catch (error) {
          console.error('Error ensuring default category:', error)
          defaultCategory = {
            title: DEFAULT_CATEGORY_NAME,
            name: DEFAULT_CATEGORY_NAME,
            icon: DEFAULT_CATEGORY_ICON,
            products: [],
          } as Category
          normalizedCategories.unshift(defaultCategory)
        }
      }

      const ensuredDefaultCategory = defaultCategory!

      const categoriesById = new Map<number, Category>()
      for (const category of normalizedCategories) {
        category.products = category.products ?? []
        if (typeof category.id === 'number') {
          categoriesById.set(category.id, category)
        }
      }

      for (const product of apiProducts) {
        const productWithPrice: Product = { ...product, price: 0 }
        const targetCategory = product.category?.id
          ? categoriesById.get(product.category.id)
          : undefined;
        (targetCategory ?? ensuredDefaultCategory).products.push(productWithPrice)
      }

      categories.value = normalizedCategories
    } catch (error) {
      console.error('Error loading categories or products:', error)
      categories.value = []
    } finally {
      isLoading.value = false
    }
  })

  function handleCategoryClick (cat: Category) {
    router.push(`/products/${cat.id}`)
  }

  function handleSelectionToggle (isSelected: boolean) {
    console.log('Selection toggled:', isSelected ? 'SELECTED' : 'UNSELECTED')
  }

  function isDefaultCategory (category: Category | null | undefined): boolean {
    if (!category?.title) return false
    return category.title.trim().toLowerCase() === DEFAULT_CATEGORY_NAME_LOWER
  }

  function handleEdit (categoryId: number) {
    const category = categories.value.find(c => c.id === categoryId)
    if (!category) return

    if (isDefaultCategory(category)) {
      showError('The Miscellaneous category cannot be edited.')
      return
    }

    categoryToEdit.value = category
    showEditDialog.value = true
  }

  async function handleEditCategory (data: { id: number, name: string, icon: string }) {
    try {
      // Update category via API
      const target = categories.value.find(c => c.id === data.id)
      if (isDefaultCategory(target)) {
        showError('The Miscellaneous category cannot be edited.')
        showEditDialog.value = false
        categoryToEdit.value = null
        return
      }

      await CategoryApi.modify(data.id, {
        name: data.name,
        metadata: { icon: data.icon },
      })

      // Update local state
      const category = target
      if (category) {
        category.name = data.name
        category.title = data.name
        category.icon = data.icon
        if (category.metadata) {
          category.metadata.icon = data.icon
        }
      }

      showEditDialog.value = false
      categoryToEdit.value = null
      showSuccess(`Category "${data.name}" updated successfully!`)
    } catch (error) {
      console.error('Error updating category:', error)
      showError('Failed to update category. Please try again.')
    }
  }

  function handleDelete (categoryId: number) {
    const category = categories.value.find(c => c.id === categoryId)
    if (!category) return

    if (isDefaultCategory(category)) {
      showError('The Miscellaneous category cannot be deleted.')
      return
    }

    categoryToDelete.value = category
    showDeleteDialog.value = true
  }

  async function confirmDelete () {
    if (!categoryToDelete.value?.id) return

    if (isDefaultCategory(categoryToDelete.value)) {
      showError('The Miscellaneous category cannot be deleted.')
      showDeleteDialog.value = false
      categoryToDelete.value = null
      return
    }

    try {
      const categoryName = categoryToDelete.value.title
      const categoryId = categoryToDelete.value.id
      console.log('🗑️ Deleting category:', categoryName)

      // Get all products in the category that will be deleted
      const productsToDelete = categoryToDelete.value.products || []

      // Delete all products in the category first
      for (const product of productsToDelete) {
        if (product.id) {
          try {
            await ProductApi.remove(product.id)
            console.log(`🗑️ Deleted product: ${product.name}`)
          } catch (error) {
            console.error(`Failed to delete product ${product.name}:`, error)
          }
        }
      }

      // Delete category via API
      await CategoryApi.remove(categoryId)

      // Remove the category from local state
      categories.value = categories.value.filter(
        category => category.id !== categoryId,
      )

      console.log('✅ Category and its products deleted successfully')

      // Reset state
      showDeleteDialog.value = false
      categoryToDelete.value = null
      showSuccess(`Category "${categoryName}" and its ${productsToDelete.length} product(s) deleted successfully!`)
    } catch (error) {
      console.error('Error deleting category:', error)
      showError('Failed to delete category. Please try again.')
    }
  }

  function handleAddProduct () {
    showCreateProductDialog.value = true
  }

  async function handleCreateCategory (categoryData: { name: string, icon: string }) {
    try {
      // Create category via API
      const newCategory = await CategoryApi.add({
        name: categoryData.name,
        metadata: { icon: categoryData.icon || 'mdi-tag-outline' },
      })

      // Add to local state with UI format
      const categoryForUI: Category = {
        ...newCategory,
        title: newCategory.name,
        icon: newCategory.metadata?.icon || 'mdi-tag-outline',
        products: [],
      }

      categories.value.unshift(categoryForUI)
      showCreateDialog.value = false
      showSuccess(`Category "${categoryData.name}" created successfully!`)
    } catch (error) {
      console.error('Error creating category:', error)
      showError('Failed to create category. Please try again.')
    }
  }

  const categoryOptions = computed(() =>
    categories.value
      .filter(c => c.id) // Only include categories with valid IDs
      .map(c => ({ value: c.id!, label: c.title, icon: c.icon })),
  )

  const defaultCategoryId = computed<number | undefined>(() => {
    const match = categories.value.find(
      category => category.title.toLowerCase() === DEFAULT_CATEGORY_NAME_LOWER,
    )
    return match?.id ?? undefined
  })

  // Transform category for dialog
  const categoryDataForDialog = computed(() => {
    if (!categoryToEdit.value || !categoryToEdit.value.id) return null
    return {
      id: categoryToEdit.value.id,
      title: categoryToEdit.value.title,
      icon: categoryToEdit.value.icon,
    }
  })

  async function handleCreateProduct (data: { name: string, categoryId: number }) {
    try {
      // Create product via API
      const newProduct = await ProductApi.add({
        name: data.name,
        category: { id: data.categoryId },
      })

      // Add to local state
      const fallbackCategory = categories.value.find(
        category => category.title.toLowerCase() === DEFAULT_CATEGORY_NAME_LOWER,
      )
      const target
        = categories.value.find(c => c.id === data.categoryId) ?? fallbackCategory
      if (target) {
        target.products.unshift({ ...newProduct, price: 0 })
      }
      showSuccess(`Product "${data.name}" created successfully!`)
    } catch (error) {
      console.error('Error creating product:', error)
      showError('Failed to create product. Please try again.')
    }
  }
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add List Section -->
      <v-row align="center" class="mb-6">
        <v-col cols="12" md="8">
          <SearchBar
            v-model="searchQuery"
            placeholder="Search categories or products..."
          />
        </v-col>
        <v-col class="d-flex flex-column flex-sm-row justify-end align-end" cols="12" md="4">
          <StandardButton
            class="mb-2 mb-sm-0 mr-sm-3"
            icon="mdi-plus"
            title="Add Product"
            @click="handleAddProduct"
          />
          <StandardButton
            icon="mdi-plus"
            title="Add Category"
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
        <p class="mt-4 text-h6">Loading categories...</p>
      </div>

      <!-- Categories grid -->
      <v-row v-else>
        <v-col
          v-for="cat in sortCategories(filteredCategories)"
          :key="cat.id"
          cols="12"
          lg="4"
          md="6"
        >
          <ProductCard
            :disable-actions="isDefaultCategory(cat)"
            :icon="cat.icon"
            :items-count="cat.products.length"
            :title="cat.title"
            @click="() => handleCategoryClick(cat)"
            @delete="() => cat.id && handleDelete(cat.id)"
            @edit="() => cat.id && handleEdit(cat.id)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredCategories.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-magnify" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">
          No categories or products found
        </p>
        <p class="text-body-2 text-medium-emphasis">
          Try adjusting your search terms
        </p>
      </div>
    </v-container>

    <!-- Create Category Dialog -->
    <CreateCategoryDialog
      v-model="showCreateDialog"
      @create-category="handleCreateCategory"
    />

    <!-- Edit Category Dialog -->
    <EditCategoryDialog
      v-model="showEditDialog"
      :category-data="categoryDataForDialog"
      @edit-category="handleEditCategory"
    />

    <!-- Create Product Dialog -->
    <CreateProductDialog
      v-model="showCreateProductDialog"
      :categories="categoryOptions"
      :default-category-id="defaultCategoryId"
      @create-product="handleCreateProduct"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      description="This action cannot be undone. All products in this category will be permanently deleted."
      :item-name="categoryToDelete?.title"
      item-type="category"
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
