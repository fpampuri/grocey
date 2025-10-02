<script setup lang="ts">
import ProductCard from "@/components/Products/ProductCard.vue";
import StandardButton from "@/components/StandardButton.vue";
import SearchBar from "@/components/SearchBar.vue";
import CreateCategoryDialog from "@/components/dialog/CreateCategoryDialog.vue";
import CreateProductDialog from "@/components/dialog/CreateProductDialog.vue";
import ConfirmDeleteDialog from "@/components/dialog/ConfirmDeleteDialog.vue";
import { useRouter } from "vue-router";
import { ref, onMounted, computed } from "vue";

const router = useRouter();

// Categories and Products state for pantry items
type Product = {
  id: number;
  name: string;
  quantity?: number;
  expiryDate?: string;
};
type Category = {
  id: number;
  title: string;
  icon: string;
  products: Product[];
};

const categories = ref<Category[]>([]);
const isLoading = ref(true);
const searchQuery = ref("");
const sortBy = ref("Name");
const showCreateDialog = ref(false);
const showCreateProductDialog = ref(false);
const showDeleteDialog = ref(false);
const categoryToDelete = ref<Category | null>(null);

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
  // Mock pantry data with quantities and expiry dates
  categories.value = [
    {
      id: 1,
      title: "Fresh Produce",
      icon: "mdi-food-apple",
      products: [
        { id: 101, name: "Apples", quantity: 6, expiryDate: "2025-01-10" },
        { id: 102, name: "Bananas", quantity: 4, expiryDate: "2025-01-08" },
        { id: 103, name: "Carrots", quantity: 8, expiryDate: "2025-01-15" },
      ],
    },
    {
      id: 2,
      title: "Pantry Staples",
      icon: "mdi-grain",
      products: [
        { id: 201, name: "Rice", quantity: 2, expiryDate: "2026-01-01" },
        { id: 202, name: "Pasta", quantity: 3, expiryDate: "2025-12-15" },
        { id: 203, name: "Flour", quantity: 1, expiryDate: "2025-11-30" },
      ],
    },
    {
      id: 3,
      title: "Refrigerated",
      icon: "mdi-fridge",
      products: [
        { id: 301, name: "Milk", quantity: 1, expiryDate: "2025-01-05" },
        { id: 302, name: "Cheese", quantity: 2, expiryDate: "2025-01-12" },
        { id: 303, name: "Yogurt", quantity: 4, expiryDate: "2025-01-07" },
        { id: 304, name: "Eggs", quantity: 12, expiryDate: "2025-01-14" },
      ],
    },
    {
      id: 4,
      title: "Frozen",
      icon: "mdi-snowflake",
      products: [
        { id: 401, name: "Frozen Peas", quantity: 2, expiryDate: "2026-03-01" },
        { id: 402, name: "Ice Cream", quantity: 1, expiryDate: "2025-12-01" },
      ],
    },
  ];
  isLoading.value = false;
});

function handleCategoryClick(cat: Category) {
  router.push(`/pantry/${cat.id}`);
}

function handleSelectionToggle(isSelected: boolean) {
  console.log("Selection toggled:", isSelected ? "SELECTED" : "UNSELECTED");
}

function handleEdit() {
  console.log("Edit pantry category");
}

function handleDelete(categoryId: number) {
  const category = categories.value.find((c) => c.id === categoryId);
  if (!category) return;

  categoryToDelete.value = category;
  showDeleteDialog.value = true;
}

function confirmDelete() {
  if (!categoryToDelete.value) return;

  console.log("🗑️ Deleting pantry category:", categoryToDelete.value.title);

  // Remove the category from the list
  categories.value = categories.value.filter(
    (category) => category.id !== categoryToDelete.value!.id
  );

  console.log("✅ Pantry category deleted successfully");

  // Reset state
  showDeleteDialog.value = false;
  categoryToDelete.value = null;
}

function handleRenameCategory(catId: number, newTitle: string) {
  const cat = categories.value.find((c) => c.id === catId);
  if (cat) cat.title = newTitle;
}

function handleAddProduct() {
  console.log("Add Pantry Item");
  showCreateProductDialog.value = true;
}

function handleCreateCategory(categoryData: { name: string; icon: string }) {
  const newCategory: Category = {
    id: Date.now(),
    title: categoryData.name,
    icon: categoryData.icon || "mdi-package-variant",
    products: [],
  };
  categories.value.unshift(newCategory);
  showCreateDialog.value = false;
}

const categoryOptions = computed(() =>
  categories.value.map((c) => ({ value: c.id, label: c.title, icon: c.icon }))
);

function handleCreateProduct(data: { name: string; categoryId: number }) {
  const target = categories.value.find((c) => c.id === data.categoryId);
  if (!target) return;
  target.products.unshift({ id: Date.now(), name: data.name, quantity: 1 });
}
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add Section -->
      <v-row class="mb-6" align="center">
        <v-col cols="12" md="8">
          <SearchBar
            v-model="searchQuery"
            placeholder="Search pantry items or categories..."
          />
        </v-col>
        <v-col cols="12" md="4" class="text-md-right">
          <StandardButton
            class="mr-4"
            title="Add Item"
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
        <p class="mt-4 text-h6">Loading pantry...</p>
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
            @edit="handleEdit"
            @delete="() => handleDelete(cat.id)"
            @rename="(newTitle) => handleRenameCategory(cat.id, newTitle)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredCategories.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon icon="mdi-magnify" size="64" color="grey-lighten-1" />
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

    <!-- Create Product Dialog -->
    <CreateProductDialog
      v-model="showCreateProductDialog"
      :categories="categoryOptions"
      @create-product="handleCreateProduct"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      item-type="pantry category"
      :item-name="categoryToDelete?.title"
      description="This action cannot be undone. All items in this category will be removed from your pantry."
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
