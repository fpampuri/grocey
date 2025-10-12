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

  const router = useRouter()

  // Categories and Products state for pantry items
  type Product = {
    id: number
    name: string
    quantity?: number
    expiryDate?: string
  }
  type Category = {
    id: number
    title: string
    icon: string
    products: Product[]
  }

  const categories = ref<Category[]>([])
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
    // TODO: Implement pantry API integration
    categories.value = []
    isLoading.value = false
  })

  function handleCategoryClick (cat: Category) {
    router.push(`/pantry/${cat.id}`)
  }

  function handleSelectionToggle (isSelected: boolean) {
    console.log('Selection toggled:', isSelected ? 'SELECTED' : 'UNSELECTED')
  }

  function handleEdit (categoryId: number) {
    const category = categories.value.find(c => c.id === categoryId)
    if (!category) return

    categoryToEdit.value = category
    showEditDialog.value = true
  }

  function handleEditCategory (data: { id: number, name: string, icon: string }) {
    const category = categories.value.find(c => c.id === data.id)
    if (!category) return

    category.title = data.name
    category.icon = data.icon

    showEditDialog.value = false
    categoryToEdit.value = null
  }

  function handleDelete (categoryId: number) {
    const category = categories.value.find(c => c.id === categoryId)
    if (!category) return

    categoryToDelete.value = category
    showDeleteDialog.value = true
  }

  function confirmDelete () {
    if (!categoryToDelete.value) return

    console.log('🗑️ Deleting pantry category:', categoryToDelete.value.title)

    // Remove the category from the list
    categories.value = categories.value.filter(
      category => category.id !== categoryToDelete.value!.id,
    )

    console.log('✅ Pantry category deleted successfully')

    // Reset state
    showDeleteDialog.value = false
    categoryToDelete.value = null
  }

  function handleAddProduct () {
    console.log('Add Pantry Item')
    showCreateProductDialog.value = true
  }

  function handleCreateCategory (categoryData: { name: string, icon: string }) {
    const newCategory: Category = {
      id: Date.now(),
      title: categoryData.name,
      icon: categoryData.icon || 'mdi-package-variant',
      products: [],
    }
    categories.value.unshift(newCategory)
    showCreateDialog.value = false
  }

  const categoryOptions = computed(() =>
    categories.value.map(c => ({ value: c.id, label: c.title, icon: c.icon })),
  )

  function handleCreateProduct (data: { name: string, categoryId: number }) {
    const target = categories.value.find(c => c.id === data.categoryId)
    if (!target) return
    target.products.unshift({ id: Date.now(), name: data.name, quantity: 1 })
  }
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add Section -->
      <v-row align="center" class="mb-6">
        <v-col cols="12" md="8">
          <SearchBar
            v-model="searchQuery"
            placeholder="Search pantry items or categories..."
          />
        </v-col>
        <v-col class="d-flex flex-column flex-sm-row justify-end align-end" cols="12" md="4">
          <StandardButton
            class="mb-2 mb-sm-0 mr-sm-3"
            icon="mdi-plus"
            title="Add Item"
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
              style="min-width: 100px"
              variant="outlined"
            />
          </div>
        </v-col>
      </v-row>

      <!-- Loading state -->
      <div v-if="isLoading" class="text-center pa-8">
        <v-progress-circular indeterminate size="64" />
        <p class="mt-4 text-h6">Loading pantry...</p>
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
            :icon="cat.icon"
            :items-count="cat.products.length"
            :title="cat.title"
            @click="() => handleCategoryClick(cat)"
            @delete="() => handleDelete(cat.id)"
            @edit="() => handleEdit(cat.id)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredCategories.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-magnify" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">No pantry items found</p>
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
      :category-data="categoryToEdit"
      @edit-category="handleEditCategory"
    />

    <!-- Create Product Dialog -->
    <CreateProductDialog
      v-model="showCreateProductDialog"
      :categories="categoryOptions"
      @create-product="handleCreateProduct"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      description="This action cannot be undone. All items in this category will be removed from your pantry."
      :item-name="categoryToDelete?.title"
      item-type="pantry category"
      @confirm="confirmDelete"
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
