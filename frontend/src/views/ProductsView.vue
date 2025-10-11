<script setup lang="ts">
import ProductCard from "@/components/Products/ProductCard.vue";
import StandardButton from "@/components/StandardButton.vue";
import SearchBar from "@/components/SearchBar.vue";
import CreateCategoryDialog from "@/components/dialog/CreateCategoryDialog.vue";
import EditCategoryDialog from "@/components/dialog/EditCategoryDialog.vue";
import CreateProductDialog from "@/components/dialog/CreateProductDialog.vue";
import ConfirmDeleteDialog from "@/components/dialog/ConfirmDeleteDialog.vue";
import { useRouter } from "vue-router";
import { ref, onMounted, computed } from "vue";
import CategoryApi, { type Category as CategoryApiType } from "@/services/category";
import ProductApi, { type Product as ProductApiType } from "@/services/product";
import ToastNotification from "@/components/ToastNotification.vue";
import { useToast } from "@/composables/useToast";

const router = useRouter();

// Toast notifications
const { showToast, toastMessage, toastType, showSuccess, showError } = useToast();

// Categories and Products state - extending API types for UI needs
type Product = ProductApiType & { price: number }; // Default price, not used in UI
type Category = CategoryApiType & {
  title: string; // UI display name (mapped from 'name')
  icon: string; // UI icon (from metadata)
  products: Product[]; // UI-specific aggregated products
};

const categories = ref<Category[]>([]);
const isLoading = ref(true);
const searchQuery = ref("");
const sortBy = ref("Name");
const showCreateDialog = ref(false);
const showEditDialog = ref(false);
const showCreateProductDialog = ref(false);
const showDeleteDialog = ref(false);
const categoryToDelete = ref<Category | null>(null);
const categoryToEdit = ref<Category | null>(null);

const filteredCategories = computed<Category[]>(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return categories.value;
  return categories.value
    .map((cat: Category) => ({
      ...cat,
      products: cat.products.filter((p: Product) =>
        p.name.toLowerCase().includes(q)
      ),
    }))
    .filter(
      (cat: Category) =>
        cat.title.toLowerCase().includes(q) || cat.products.length > 0
    );
});

function sortCategories(list: Category[]): Category[] {
  if (sortBy.value === "Name") {
    return [...list].sort((a: Category, b: Category) =>
      a.title.localeCompare(b.title)
    );
  }
  if (sortBy.value === "Items") {
    return [...list].sort(
      (a: Category, b: Category) => b.products.length - a.products.length
    );
  }
  return list;
}

onMounted(async () => {
  isLoading.value = true;
  
  // Fetch categories from API
  const apiCategoriesResponse = await CategoryApi.getAll();
  console.log('Categories API response:', apiCategoriesResponse);
  
  // Fetch all products to group by category
  const apiProductsResponse = await ProductApi.getAll();
  console.log('Products API response:', apiProductsResponse);
  
  // Handle different possible response formats
  const apiCategories: CategoryApiType[] = Array.isArray(apiCategoriesResponse) 
    ? apiCategoriesResponse 
    : (apiCategoriesResponse as any)?.data || [];
    
  const apiProducts: ProductApiType[] = Array.isArray(apiProductsResponse) 
    ? apiProductsResponse 
    : (apiProductsResponse as any)?.data || [];
  
  // Transform API data to UI format
  categories.value = apiCategories.map(category => ({
    ...category,
    title: category.name,
    icon: category.metadata?.icon || 'mdi-box',
    products: apiProducts
      .filter(product => product.category?.id === category.id)
      .map(product => ({ ...product, price: 0 })) // Default price value
  }));
  
  isLoading.value = false;
});

function handleCategoryClick(cat: Category) {
  router.push(`/products/${cat.id}`);
}

function handleSelectionToggle(isSelected: boolean) {
  console.log("Selection toggled:", isSelected ? "SELECTED" : "UNSELECTED");
}

function handleEdit(categoryId: number) {
  const category = categories.value.find(c => c.id === categoryId);
  if (!category) return;
  
  categoryToEdit.value = category;
  showEditDialog.value = true;
}

async function handleEditCategory(data: { id: number; name: string; icon: string }) {
  try {
    // Update category via API
    await CategoryApi.modify(data.id, {
      name: data.name,
      metadata: { icon: data.icon }
    });
    
    // Update local state
    const category = categories.value.find(c => c.id === data.id);
    if (category) {
      category.name = data.name;
      category.title = data.name;
      category.icon = data.icon;
      if (category.metadata) {
        category.metadata.icon = data.icon;
      }
    }
    
    showEditDialog.value = false;
    categoryToEdit.value = null;
    showSuccess(`Category "${data.name}" updated successfully!`);
  } catch (error) {
    console.error('Error updating category:', error);
    showError('Failed to update category. Please try again.');
  }
}

function handleDelete(categoryId: number) {
  const category = categories.value.find(c => c.id === categoryId);
  if (!category) return;
  
  categoryToDelete.value = category;
  showDeleteDialog.value = true;
}

async function confirmDelete() {
  if (!categoryToDelete.value?.id) return;
  
  try {
    const categoryName = categoryToDelete.value.title;
    console.log('🗑️ Deleting category:', categoryName);
    
    // Delete category via API
    await CategoryApi.remove(categoryToDelete.value.id);
    
    // Remove the category from local state
    categories.value = categories.value.filter(
      category => category.id !== categoryToDelete.value!.id
    );
    
    console.log('✅ Category deleted successfully');
    
    // Reset state
    showDeleteDialog.value = false;
    categoryToDelete.value = null;
    showSuccess(`Category "${categoryName}" deleted successfully!`);
  } catch (error) {
    console.error('Error deleting category:', error);
    showError('Failed to delete category. Please try again.');
  }
}

function handleAddProduct() {
  showCreateProductDialog.value = true;
}

async function handleCreateCategory(categoryData: { name: string; icon: string }) {
  try {
    // Create category via API
    const newCategory = await CategoryApi.add({
      name: categoryData.name,
      metadata: { icon: categoryData.icon || "mdi-tag-outline" }
    });
    
    // Add to local state with UI format
    const categoryForUI: Category = {
      ...newCategory,
      title: newCategory.name,
      icon: newCategory.metadata?.icon || "mdi-tag-outline",
      products: []
    };
    
    categories.value.unshift(categoryForUI);
    showCreateDialog.value = false;
    showSuccess(`Category "${categoryData.name}" created successfully!`);
  } catch (error) {
    console.error('Error creating category:', error);
    showError('Failed to create category. Please try again.');
  }
}

const categoryOptions = computed(() =>
  categories.value
    .filter(c => c.id) // Only include categories with valid IDs
    .map((c) => ({ value: c.id!, label: c.title, icon: c.icon }))
);

// Transform category for dialog
const categoryDataForDialog = computed(() => {
  if (!categoryToEdit.value || !categoryToEdit.value.id) return null;
  return {
    id: categoryToEdit.value.id,
    title: categoryToEdit.value.title,
    icon: categoryToEdit.value.icon
  };
});

async function handleCreateProduct(data: { name: string; categoryId: number }) {
  try {
    // Create product via API
    const newProduct = await ProductApi.add({
      name: data.name,
      category: { id: data.categoryId }
    });
    
    // Add to local state
    const target = categories.value.find((c) => c.id === data.categoryId);
    if (target) {
      target.products.unshift({ ...newProduct, price: 0 });
    }
    showSuccess(`Product "${data.name}" created successfully!`);
  } catch (error) {
    console.error('Error creating product:', error);
    showError('Failed to create product. Please try again.');
  }
}
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add List Section -->
      <v-row class="mb-6" align="center">
        <v-col cols="12" md="8">
          <SearchBar
            v-model="searchQuery"
            placeholder="Search categories or products..."
          />
        </v-col>
        <v-col cols="12" md="4" class="d-flex flex-column flex-sm-row justify-end align-end">
          <StandardButton
            class="mb-2 mb-sm-0 mr-sm-3"
            title="Add Product"
            icon="mdi-plus"
            @click="handleAddProduct"
          />
          <StandardButton
            title="Add Category"
            icon="mdi-plus"
            @click="showCreateDialog = true"
          />
        </v-col>
      </v-row>

      <!-- Sort Section -->
      <v-row class="mb-4" align="center">
        <v-spacer />
        <v-col cols="auto">
          <div class="d-flex align-center">
            <span class="mr-3 text-medium-emphasis">Sort by:</span>
            <v-select
              v-model="sortBy"
              :items="['Name', 'Items']"
              variant="outlined"
              hide-details
              density="compact"
              style="min-width: 100px"
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
          cols="12"
          md="6"
          lg="4"
          v-for="cat in sortCategories(filteredCategories)"
          :key="cat.id"
        >
          <ProductCard
            :title="cat.title"
            :icon="cat.icon"
            :itemsCount="cat.products.length"
            @click="() => handleCategoryClick(cat)"
            @edit="() => cat.id && handleEdit(cat.id)"
            @delete="() => cat.id && handleDelete(cat.id)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredCategories.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon icon="mdi-magnify" size="64" color="grey-lighten-1" />
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
      @create-product="handleCreateProduct"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      item-type="category"
      :item-name="categoryToDelete?.title"
      description="This action cannot be undone. All products in this category will be moved to 'Uncategorized'."
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