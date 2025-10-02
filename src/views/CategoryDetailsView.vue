<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { computed, ref, onMounted } from "vue";
import ProductItemCard from "@/components/Products/ProductItemCard.vue";

const route = useRoute();
const router = useRouter();

type Product = { id: number; name: string; completed: boolean; quantity?: number; expiryDate?: string };
type Category = { id: number; title: string; icon: string };

const categoryId = computed(() => Number(route.params.id));
const isFromPantry = computed(() => route.path.includes('/pantry/'));

const categoryData = ref<Category | null>(null);
const products = ref<Product[]>([]);
const isLoading = ref(true);

function goBack() {
  if (isFromPantry.value) {
    router.push("/pantry");
  } else {
    router.push("/categories");
  }
}

function getMockCategory(id: number): Category | null {
  if (isFromPantry.value) {
    // Pantry categories
    const pantryMap: Record<number, Category> = {
      1: { id: 1, title: "Fresh Produce", icon: "mdi-food-apple" },
      2: { id: 2, title: "Pantry Staples", icon: "mdi-grain" },
      3: { id: 3, title: "Refrigerated", icon: "mdi-fridge" },
      4: { id: 4, title: "Frozen", icon: "mdi-snowflake" },
    };
    return pantryMap[id] ?? null;
  } else {
    // Categories
    const categoryMap: Record<number, Category> = {
      1: { id: 1, title: "Fruits", icon: "mdi-food-apple" },
      2: { id: 2, title: "Vegetables", icon: "mdi-food-variant" },
      3: { id: 3, title: "Dairy", icon: "mdi-cheese" },
    };
    return categoryMap[id] ?? null;
  }
}

function getMockProducts(id: number): Product[] {
  if (isFromPantry.value) {
    // Pantry items with quantities and expiry dates
    const pantryItems: Record<number, Array<{name: string, quantity: number, expiryDate: string}>> = {
      1: [
        { name: "Apples", quantity: 6, expiryDate: "2025-01-10" },
        { name: "Bananas", quantity: 4, expiryDate: "2025-01-08" },
        { name: "Carrots", quantity: 8, expiryDate: "2025-01-15" },
      ],
      2: [
        { name: "Rice", quantity: 2, expiryDate: "2026-01-01" },
        { name: "Pasta", quantity: 3, expiryDate: "2025-12-15" },
        { name: "Flour", quantity: 1, expiryDate: "2025-11-30" },
      ],
      3: [
        { name: "Milk", quantity: 1, expiryDate: "2025-01-05" },
        { name: "Cheese", quantity: 2, expiryDate: "2025-01-12" },
        { name: "Yogurt", quantity: 4, expiryDate: "2025-01-07" },
        { name: "Eggs", quantity: 12, expiryDate: "2025-01-14" },
      ],
      4: [
        { name: "Frozen Peas", quantity: 2, expiryDate: "2026-03-01" },
        { name: "Ice Cream", quantity: 1, expiryDate: "2025-12-01" },
      ],
    };
    const items = pantryItems[id] ?? [];
    return items.map((item, idx) => ({
      id: id * 100 + idx + 1,
      name: item.name,
      quantity: item.quantity,
      expiryDate: item.expiryDate,
      completed: false,
    }));
  } else {
    // Category items
    const categoryItems: Record<number, string[]> = {
      1: ["Apples", "Bananas", "Strawberries"],
      2: ["Tomatoes", "Lettuce"],
      3: ["Milk", "Cheese", "Yogurt", "Butter"],
    };
    const names = categoryItems[id] ?? [];
    return names.map((name, idx) => ({
      id: id * 100 + idx + 1,
      name,
      completed: false,
    }));
  }
}

onMounted(async () => {
  isLoading.value = true;
  const cat = getMockCategory(categoryId.value);
  categoryData.value = cat;
  if (cat) {
    products.value = getMockProducts(categoryId.value);
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
        <v-col>
          <div v-if="isLoading">
            <h1 class="text-h4">Loading...</h1>
          </div>
          <div v-else-if="categoryData">
            <h1 class="text-h4">{{ categoryData.title }}</h1>
          </div>
          <div v-else>
            <h1 class="text-h4">{{ isFromPantry ? 'Pantry Category' : 'Category' }} Not Found</h1>
          </div>
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
              <ProductItemCard
                v-for="item in products"
                :key="item.id"
                :id="item.id"
                :title="item.name"
                :completed="item.completed"
                @toggle-complete="(completed) => handleToggleComplete(item.id, completed)"
                @delete="(id) => handleDeleteItem(id)"
                @rename="handleRenameProduct"
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
                  {{ isFromPantry ? 'Back to Pantry' : 'Back to Categories' }}
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
/* Add any specific styling here if needed */
</style>