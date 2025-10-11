<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { computed, ref, onMounted } from "vue";
import Card from "@/components/Products/ProductItemCard.vue";
import MoveToCategoryDialog from "@/components/dialog/MoveToCategoryDialog.vue";
import AddToListDialog from "@/components/dialog/AddToListDialog.vue";
import CategoryApi, { type Category as CategoryApiType } from "@/services/category";
import ProductApi, { type Product as ProductApiType } from "@/services/product";
import ShoppingListApi, { type ShoppingList } from "@/services/shoppingList";

const route = useRoute();
const router = useRouter();

type Product = ProductApiType & { completed?: boolean; quantity?: number; expiryDate?: string };
type Category = {
  id: number;
  name?: string;
  title: string;
  icon: string;
  metadata?: any;
  createdAt?: string;
  updatedAt?: string;
};

const categoryId = computed(() => Number(route.params.id));
const isFromPantry = computed(() => route.path.includes('/pantry/'));

const categoryData = ref<Category | null>(null);
const products = ref<Product[]>([]);
const isLoading = ref(true);

// Data for dialogs
const allCategories = ref<CategoryApiType[]>([]);
const allLists = ref<ShoppingList[]>([]);

// Dialog states
const showMoveDialog = ref(false);
const showAddToListDialog = ref(false);
const selectedProductId = ref<number | null>(null);

// Categories for MoveToCategoryDialog - populated from API
const availableCategories = computed(() => {
  return allCategories.value
    .filter(cat => cat.id !== categoryId.value)
    .map(cat => ({
      value: cat.id!,
      label: cat.name,
      icon: cat.metadata?.icon || 'mdi-box'
    }));
});

// Lists for AddToListDialog - populated from API
const availableLists = computed(() => {
  return allLists.value.map(list => ({
    value: list.id!,
    label: list.name,
    icon: list.metadata?.icon || 'mdi-format-list-bulleted'
  }));
});

function goBack() {
  if (isFromPantry.value) {
    router.push("/pantry");
  } else {
    router.push("/products");
  }
}

// Fetch categories for MoveToCategoryDialog (with caching)
async function fetchCategories() {
  if (allCategories.value.length > 0 || isFromPantry.value) return; // Already loaded or not needed
  
  try {
    const apiCategoriesResponse = await CategoryApi.getAll();
    allCategories.value = Array.isArray(apiCategoriesResponse) 
      ? apiCategoriesResponse 
      : (apiCategoriesResponse as any)?.data || [];
  } catch (error) {
    console.error('Error fetching categories:', error);
    allCategories.value = [];
  }
}

// Fetch lists for AddToListDialog (with caching)
async function fetchLists() {
  if (allLists.value.length > 0) return; // Already loaded
  
  try {
    const apiListsResponse = await ShoppingListApi.getAll();
    allLists.value = Array.isArray(apiListsResponse) 
      ? apiListsResponse 
      : (apiListsResponse as any)?.data || [];
  } catch (error) {
    console.error('Error fetching lists:', error);
    allLists.value = [];
  }
}



onMounted(async () => {
  isLoading.value = true;
  try {
    if (isFromPantry.value) {
      // For pantry, show empty state - no mock data
      categoryData.value = null;
      products.value = [];
    } else {
      // For categories, use real API calls
      const apiCategory = await CategoryApi.get(categoryId.value);
      categoryData.value = {
        id: apiCategory.id || categoryId.value,
        name: apiCategory.name,
        title: apiCategory.name,
        icon: apiCategory.metadata?.icon || 'mdi-box',
        metadata: apiCategory.metadata,
        createdAt: apiCategory.createdAt,
        updatedAt: apiCategory.updatedAt
      };
      
      // Fetch products for this category
      const apiProductsResponse = await ProductApi.getAll();
      
      // Handle different possible response formats
      const allProducts: ProductApiType[] = Array.isArray(apiProductsResponse) 
        ? apiProductsResponse 
        : (apiProductsResponse as any)?.data || [];
      
      products.value = allProducts
        .filter(product => product.category?.id === categoryId.value)
        .map(product => ({
          ...product,
          completed: false // Default UI state
        }));
    }
  } catch (error) {
    console.error('Error loading category data:', error);
    // Set empty state on error
    categoryData.value = null;
    products.value = [];
  }
  isLoading.value = false;
});

function handleToggleComplete(itemId: number, completed: boolean) {
  const item = products.value.find((p) => p.id === itemId);
  if (item) item.completed = completed;
}

function handleDeleteItem(itemId: number) {
  products.value = products.value.filter((p) => p.id !== itemId);
}

function handleRenameProduct(payload: { id: number; name: string }) {
  const item = products.value.find((p) => p.id === payload.id);
  if (item) item.name = payload.name;
}

async function handleMoveProduct(productId: number) {
  selectedProductId.value = productId;
  await fetchCategories();
  showMoveDialog.value = true;
}

async function handleAddToList(productId: number) {
  selectedProductId.value = productId;
  await fetchLists();
  showAddToListDialog.value = true;
}

function confirmMoveToCategory(data: { categoryId: number }) {
  console.log('Moving product', selectedProductId.value, 'to category', data.categoryId);
  // Remove product from current list
  if (selectedProductId.value) {
    products.value = products.value.filter(p => p.id !== selectedProductId.value);
  }
  showMoveDialog.value = false;
  selectedProductId.value = null;
}

function confirmAddToList(data: { listId: number }) {
  console.log('Adding product', selectedProductId.value, 'to list', data.listId);
  showAddToListDialog.value = false;
  selectedProductId.value = null;
}
</script>

<template>
  <div class="pa-4">
    <v-container fluid>
      <v-row class="mb-4" align="center">
        <v-col cols="auto">
          <v-btn
            icon="mdi-arrow-left"
            @click="goBack"
            variant="text"
            size="large"
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
                :key="item.id || item.name"
                :id="item.id || 0"
                :title="item.name"
                :completed="item.completed || false"
                @toggle-complete="(completed) => handleToggleComplete(item.id || 0, completed)"
                @delete="(id) => handleDeleteItem(id)"
                @rename="handleRenameProduct"
                @move="handleMoveProduct"
                @add-to-list="handleAddToList"
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon icon="mdi-inbox" size="64" color="grey-lighten-1" />
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
                <v-btn @click="goBack" color="primary" class="mt-2">
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
  </div>
</template>

<style scoped>
/* Add any specific styling here if needed */
</style>