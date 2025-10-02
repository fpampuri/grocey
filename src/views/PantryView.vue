<script setup lang="ts">
  import ProductCard from "@/components/Products/ProductCard.vue";
  import StandardButton from "@/components/StandardButton.vue";
  import SearchBar from "@/components/SearchBar.vue";
  import CreateCategoryDialog from "@/components/dialog/CreateCategoryDialog.vue";
  import CreateProductDialog from "@/components/dialog/CreateProductDialog.vue";
  import ConfirmDeleteListDialog from "@/components/dialog/ConfirmDeleteListDialog.vue";
  import { useRouter } from "vue-router";
  import { ref, onMounted, computed } from "vue";

  const router = useRouter();

  

  // Categories and Products state
  type Product = { id: number; name: string; price?: number };
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
  const toDeleteCategory = ref<Category | null>(null);

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
    // Mock demo data; could be fetched from API later
    categories.value = [
      {
        id: 1,
        title: "Fruits",
        icon: "mdi-food-apple",
        products: [
          { id: 101, name: "Apples" },
          { id: 102, name: "Bananas" },
          { id: 103, name: "Strawberries" },
        ],
      },
      {
        id: 2,
        title: "Vegetables",
        icon: "mdi-food-variant",
        products: [
          { id: 201, name: "Tomatoes" },
          { id: 202, name: "Lettuce" },
        ],
      },
      {
        id: 3,
        title: "Dairy",
        icon: "mdi-cheese",
        products: [
          { id: 301, name: "Milk" },
          { id: 302, name: "Cheese" },
          { id: 303, name: "Yogurt" },
          { id: 304, name: "Butter" },
        ],
      },
    ];
    isLoading.value = false;
  });

  function handleCategoryClick(cat: Category) {
    router.push(`/products/category/${cat.id}`);
  }

  function handleSelectionToggle(isSelected: boolean) {
    console.log("Selection toggled:", isSelected ? "SELECTED" : "UNSELECTED");
  }

  function handleEdit() {
    console.log("Edit category");
  }

  function handleDelete() {
    console.log("Delete category");
  }

  function handleRenameCategory(catId: number, newTitle: string) {
    const cat = categories.value.find((c) => c.id === catId);
    if (cat) cat.title = newTitle;
  }

  function handleAddProduct() {
    console.log("Add Product");
    showCreateProductDialog.value = true;
  }

  function handleCreateCategory(categoryData: { name: string; icon: string }) {
    const newCategory: Category = {
      id: Date.now(),
      title: categoryData.name,
      icon: categoryData.icon || "mdi-tag-outline",
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
    target.products.unshift({ id: Date.now(), name: data.name });
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
        <v-col cols="12" md="4" class="text-md-right">
          <StandardButton
            class="mr-4"
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
            @edit="handleEdit"
            @delete="handleDelete"
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

    <!-- Create Product Dialog -->
    <CreateProductDialog
      v-model="showCreateProductDialog"
      :categories="categoryOptions"
      @create-product="handleCreateProduct"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteListDialog
      v-model="showDeleteDialog"
      :category-to-delete="toDeleteCategory"
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
