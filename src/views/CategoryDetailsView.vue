<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { computed, ref, onMounted, nextTick, watch } from 'vue';
import ProductItemCard from '@/components/Products/ProductItemCard.vue';
import StandardButton from '@/components/StandardButton.vue';
import MoveToCategoryDialog from '@/components/MoveToCategoryDialog.vue';
import AddToListDialog from '@/components/AddToListDialog.vue';

type Product = { id: number; name: string; completed: boolean };
type Category = { id: number; title: string; icon: string };

const route = useRoute();
const router = useRouter();

const categoryId = computed(() => Number(route.params.id));
const categoryData = ref<Category | null>(null);
const products = ref<Product[]>([]);
const isLoading = ref(true);
const productsLoading = ref(true);
// Dialog state for move/add actions
const moveDialogOpen = ref(false);
const addDialogOpen = ref(false);
const productToActOn = ref<number | null>(null);

// Inline rename state for category title
const editingTitle = ref(false);
const localTitle = ref('');
const titleInputRef = ref<HTMLInputElement | null>(null);

function goBack() {
  router.push('/products');
}

function getMockCategory(id: number): Category | null {
  const map: Record<number, Category> = {
    1: { id: 1, title: 'Fruits', icon: 'mdi-food-apple' },
    2: { id: 2, title: 'Vegetables', icon: 'mdi-food-variant' },
    3: { id: 3, title: 'Dairy', icon: 'mdi-cheese' },
  };
  return map[id] ?? null;
}

function getMockProducts(id: number): Product[] {
  const byCat: Record<number, string[]> = {
    1: ['Apples', 'Bananas', 'Strawberries'],
    2: ['Tomatoes', 'Lettuce'],
    3: ['Milk', 'Cheese', 'Yogurt', 'Butter'],
  };
  const names = byCat[id] ?? [];
  return names.map((name, idx) => ({
    id: id * 100 + idx + 1,
    name,
    completed: false,
  }));
}

onMounted(async () => {
  isLoading.value = true;
  productsLoading.value = true;
  const cat = getMockCategory(categoryId.value);
  categoryData.value = cat;
  localTitle.value = cat?.title ?? '';
  isLoading.value = false;
  if (cat) {
    products.value = getMockProducts(categoryId.value);
  }
  productsLoading.value = false;
});

function handleToggleComplete(itemId: number, completed: boolean) {
  const item = products.value.find(p => p.id === itemId);
  if (item) item.completed = completed;
}

function handleDeleteItem(itemId: number) {
  products.value = products.value.filter(p => p.id !== itemId);
}

function handleAddProduct() {
  console.log('Add product to category', categoryId.value);
}

// Mock options aligned with ProductsView demo data
const categoryOptions = computed(() => ([
  { value: 1, label: 'Fruits' },
  { value: 2, label: 'Vegetables' },
  { value: 3, label: 'Dairy' },
]));

function openMoveDialog(id: number) {
  productToActOn.value = id;
  moveDialogOpen.value = true;
}

function confirmMoveToCategory(payload: { categoryId: number }) {
  if (productToActOn.value == null) return;
  const prod = products.value.find(p => p.id === productToActOn.value);
  products.value = products.value.filter(p => p.id !== productToActOn.value);
  // TODO: push prod into target category via shared store
  console.log('Moved product', prod?.id, 'to category', payload.categoryId);
  productToActOn.value = null;
}

const listOptions = computed(() => ([
  { value: 11, label: "Alice's List" },
  { value: 22, label: "Bob's List" },
  { value: 33, label: 'Groceries Weekly' },
]));

function openAddToListDialog(id: number) {
  productToActOn.value = id;
  addDialogOpen.value = true;
}

function confirmAddToList(payload: { listId: number }) {
  if (productToActOn.value == null) return;
  console.log('Added product', productToActOn.value, 'to list', payload.listId);
  productToActOn.value = null;
}

function handleRenameProduct(payload: { id: number; name: string }) {
  const item = products.value.find(p => p.id === payload.id);
  if (item) item.name = payload.name;
}

watch(() => categoryData.value?.title, (v) => { if (!editingTitle.value) localTitle.value = v ?? ''; });

function startEditTitle() {
  editingTitle.value = true;
  nextTick(() => titleInputRef.value?.focus());
}

function commitTitle() {
  const newName = localTitle.value.trim();
  if (categoryData.value && newName) {
    categoryData.value.title = newName;
  }
  editingTitle.value = false;
}
function cancelTitle() {
  localTitle.value = categoryData.value?.title ?? '';
  editingTitle.value = false;
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
            <div class="title-row">
              <template v-if="editingTitle">
                <input
                  ref="titleInputRef"
                  v-model="localTitle"
                  class="rename-input"
                  type="text"
                  @keydown.enter.stop.prevent="commitTitle"
                  @keydown.esc.stop.prevent="cancelTitle"
                  @blur="commitTitle"
                />
              </template>
              <template v-else>
                <h1 class="text-h4 mb-0">{{ categoryData.title }}</h1>
                <button class="icon-btn edit-btn" @click="startEditTitle" aria-label="Rename category">
                  <v-icon icon="mdi-pencil" />
                </button>
              </template>
            </div>
          </div>
          <div v-else>
            <h1 class="text-h4">Category Not Found</h1>
          </div>
        </v-col>
      </v-row>

      <v-row class="mb-4" v-if="categoryData">
        <v-col cols="12">
          <StandardButton 
            title="Add Product"
            icon="mdi-plus"
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
                <p class="mt-2">Loading category...</p>
              </v-card-text>
            </v-card>
          </div>
          <div v-else-if="categoryData">
            <div v-if="productsLoading" class="text-center pa-4">
              <v-progress-circular indeterminate size="32" />
              <p class="mt-2">Loading products...</p>
            </div>
            <div v-else-if="products.length > 0">
              <ProductItemCard
                v-for="item in products"
                :key="item.id"
                :id="item.id"
                :title="item.name"
                :completed="item.completed"
                @toggle-complete="(completed) => handleToggleComplete(item.id, completed)"
                @move="openMoveDialog"
                @add-to-list="openAddToListDialog"
                @delete="(id) => handleDeleteItem(id)"
                @rename="handleRenameProduct"
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon icon="mdi-inbox" size="64" color="grey-lighten-1" />
              <p class="mt-2 text-h6 text-medium-emphasis">This category is empty</p>
              <p class="text-body-2 text-medium-emphasis">Add your first product to get started</p>
            </div>
          </div>
          <div v-else>
            <v-card class="pa-4">
              <v-card-title>Category Not Found</v-card-title>
              <v-card-text>
                <p>No category found with ID: <strong>{{ categoryId }}</strong></p>
                <v-btn @click="goBack" color="primary" class="mt-2">
                  Back to Products
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <!-- Move to Category Dialog -->
    <MoveToCategoryDialog
      v-model="moveDialogOpen"
      :categories="categoryOptions"
      @move-to-category="confirmMoveToCategory"
    />
    <!-- Add to List Dialog -->
    <AddToListDialog
      v-model="addDialogOpen"
      :lists="listOptions"
      @add-to-list="confirmAddToList"
    />
  </div>
  
</template>

<style scoped>
.add-item-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rename-input {
  width: 100%;
  max-width: 420px;
  padding: 6px 10px;
  border: 2px solid var(--primary-green-light);
  border-radius: 8px;
  font-size: 1.25rem;
}

.icon-btn {
  background: none;
  border: 1px solid var(--v-border-color, #e0e0e0);
  border-radius: 6px;
  width: 36px;
  height: 36px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--text-primary);
}
.icon-btn:hover { background-color: var(--primary-green); color: white; border-color: var(--primary-green); }
.edit-btn { border-color: var(--v-border-color, #e0e0e0); }
</style>
