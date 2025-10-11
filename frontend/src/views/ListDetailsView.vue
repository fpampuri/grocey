<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { computed, ref, watch, onBeforeUnmount } from "vue";
import { useToast } from "@/composables/useToast";
import ListItemCard from "@/components/List/ListItemCard.vue";
import StandardButton from "@/components/StandardButton.vue";
import AddItemDialog from "@/components/dialog/AddItemDialog.vue";
import ToastNotification from "@/components/ToastNotification.vue";
import ShoppingListApi from "@/services/shoppingList";
import ListItemApi from "@/services/listItem";
import ProductApi from "@/services/product";

interface ListUser {
  id: number;
  name: string;
  email: string;
  role: "owner" | "collaborator";
}

interface ShoppingListItem {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unit: string;
  completed: boolean;
  lastPurchasedAt: string | null;
  metadata: Record<string, any>;
}

interface ShoppingListDetail {
  id: number;
  name: string;
  title: string;
  description: string;
  recurring: boolean;
  icon: string;
  metadata: Record<string, any>;
  users: ListUser[];
  createdBy: number | null;
  createdAt: string;
}

const route = useRoute();
const router = useRouter();
const { showToast, toastMessage, toastType, showSuccess, showError } = useToast();

const listId = computed(() => Number(route.params.id));
const listData = ref<ShoppingListDetail | null>(null);
const listItems = ref<ShoppingListItem[]>([]);
const isLoading = ref(true);
const itemsLoading = ref(false);
const loadError = ref<string | null>(null);
const itemsError = ref<string | null>(null);
const showAddItemDialog = ref(false);



function mapListDetail(data: any): ShoppingListDetail {
  const metadata = (data?.metadata ?? {}) as Record<string, any>;
  const icon = metadata.icon ?? "mdi-format-list-bulleted";
  const users: ListUser[] = [];

  if (data?.owner) {
    users.push({
      id: data.owner.id,
      name: [data.owner.name, data.owner.surname].filter(Boolean).join(" "),
      email: data.owner.email,
      role: "owner",
    });
  }

  if (Array.isArray(data?.sharedWith)) {
    data.sharedWith.forEach((user: any) => {
      users.push({
        id: user.id,
        name: [user.name, user.surname].filter(Boolean).join(" "),
        email: user.email,
        role: "collaborator",
      });
    });
  }

  return {
    id: data.id,
    name: data.name,
    title: data.name,
    description: data.description ?? "",
    recurring: Boolean(data.recurring),
    icon,
    metadata,
    users,
    createdBy: data?.owner?.id ?? null,
    createdAt: data.createdAt ?? new Date().toISOString(),
  };
}

function mapListItem(data: any): ShoppingListItem {
  const parsedId = Number(data.id);
  const parsedProductId = Number(data.product?.id ?? data.productId ?? 0);
  return {
    id: Number.isFinite(parsedId) ? parsedId : 0,
    productId: Number.isFinite(parsedProductId) ? parsedProductId : 0,
    productName: data.product?.name ?? data.productName ?? "Unnamed product",
    quantity: data.quantity ?? 1,
    unit: data.unit ?? "unit",
    completed: Boolean(data.purchased ?? data.completed),
    lastPurchasedAt: data.lastPurchasedAt ?? null,
    metadata: data.metadata ?? {},
  };
}

async function loadList() {
  isLoading.value = true;
  loadError.value = null;

  try {
    const data = await ShoppingListApi.get(listId.value);
    listData.value = mapListDetail(data);
  } catch (error) {
    console.error("Error fetching list data:", error);
    if (error && typeof error === 'object' && 'message' in error) {
      loadError.value = (error as { message: string }).message;
    } else if (error instanceof Error) {
      loadError.value = error.message;
    } else {
      loadError.value = "Unable to load the list.";
    }
    listData.value = null;
    listItems.value = [];
    itemsError.value = null;
  } finally {
    isLoading.value = false;
  }
}

async function loadListItems() {
  itemsLoading.value = true;
  itemsError.value = null;

  try {
    const data = await ListItemApi.getAll(listId.value);
    const collection = Array.isArray(data) ? data : [];
    listItems.value = collection.map(mapListItem);
  } catch (error) {
    console.error("Error fetching list items:", error);
    if (error && typeof error === 'object' && 'message' in error) {
      itemsError.value = (error as { message: string }).message;
    } else if (error instanceof Error) {
      itemsError.value = error.message;
    } else {
      itemsError.value = "Unable to load items.";
    }
    listItems.value = [];
  } finally {
    itemsLoading.value = false;
  }
}

async function handleUpdateQuantity(itemId: number | string, newQuantity: number) {
  const numericId = Number(itemId);
  if (!Number.isFinite(numericId)) return;
  const target = listItems.value.find((item) => Number(item.id) === numericId);
  if (!target) return;

  const previous = target.quantity;
  target.quantity = newQuantity;
  target.metadata = target.metadata ?? {};

  try {
    await ListItemApi.modify(listId.value, numericId, {
      quantity: newQuantity,
      unit: target.unit || "unit",
      metadata: target.metadata ?? {},
    });
    showSuccess("Item quantity updated.");
  } catch (error) {
    console.error("Error updating quantity:", error);
    target.quantity = previous;
    showError(
      error && typeof error === 'object' && 'message' in error
        ? (error as { message: string }).message
        : "Unable to update quantity."
    );
  }
}

async function handleToggleComplete(itemId: number | string, completed: boolean) {
  const numericId = Number(itemId);
  if (!Number.isFinite(numericId)) return;
  const target = listItems.value.find((item) => Number(item.id) === numericId);
  if (!target) return;

  const previous = target.completed;
  target.completed = completed;

  try {
     if (completed) {
      await ListItemApi.markAsPurchased(listId.value, numericId);
    } else {
      await ListItemApi.markAsNotPurchased(listId.value, numericId);
    }
    showSuccess(
      completed ? "Marked item as purchased." : "Marked item as pending."
    );
  } catch (error) {
    console.error("Error toggling item state:", error);
    target.completed = previous;
    showError(
      error && typeof error === 'object' && 'message' in error
        ? (error as { message: string }).message
        : "Unable to update item."
    );
  }
}

async function handleDeleteItem(itemId: number | string) {
  const numericId = Number(itemId);
  if (!Number.isFinite(numericId)) return;

  const previousItems = [...listItems.value];
  listItems.value = listItems.value.filter((item) => Number(item.id) !== numericId);

  try {
    await ListItemApi.remove(listId.value, numericId);
    showSuccess("Item removed from the list.");
  } catch (error) {
    console.error("Error deleting item:", error);
    listItems.value = previousItems;
    showError(
      error && typeof error === 'object' && 'message' in error
        ? (error as { message: string }).message
        : "Unable to delete item."
    );
  }
}

async function resolveProductId(productName: string): Promise<number> {
  const trimmed = productName.trim();
  if (!trimmed) throw new Error("Product name is required.");

  try {
    const data = await ProductApi.getByName(trimmed);
    const collection = Array.isArray(data) ? data : [];
    const match = collection.find(
      (product: any) =>
        (product.name ?? "").trim().toLowerCase() === trimmed.toLowerCase()
    );
    if (match?.id) return match.id;
  } catch (error) {
    console.warn("Unable to find existing product", error);
  }

  try {
    const data = await ProductApi.add({
      name: trimmed,
      metadata: {},
    });
    if (!data?.id) {
      throw new Error("Unable to create product for list item.");
    }
    return data.id;
  } catch (error) {
    if (error && typeof error === 'object' && 'message' in error && (error as any).message?.includes('409')) {
      // Product already exists, try to retrieve it again
      const data = await ProductApi.getByName(trimmed);
      const collection = Array.isArray(data) ? data : [];
      const match = collection.find(
        (product: any) =>
          (product.name ?? "").trim().toLowerCase() === trimmed.toLowerCase()
      );
      if (match?.id) return match.id;
    }
    throw error;
  }
}

async function handleAddItem(itemData: { name: string }) {
  if (!itemData.name.trim()) return;

  try {
    const productId = await resolveProductId(itemData.name);

    const data = await ListItemApi.add(listId.value, {
      product: { id: productId },
      quantity: 1,
      unit: "unit",
      metadata: {},
    });
    const mapped = mapListItem(data);
    listItems.value.unshift(mapped);
    showSuccess("Item added to the list.");
  } catch (error) {
    console.error("Error adding item:", error);
    showError(
      error && typeof error === 'object' && 'message' in error
        ? (error as { message: string }).message
        : error instanceof Error
        ? error.message
        : "Unable to add item."
    );
  }
}

function openAddItemDialog() {
  showAddItemDialog.value = true;
}

function goBack() {
  router.push("/lists");
}

watch(
  () => listId.value,
  async (id) => {
    if (!Number.isFinite(id)) {
      loadError.value = "Invalid list identifier.";
      listData.value = null;
      listItems.value = [];
      isLoading.value = false;
      itemsLoading.value = false;
      itemsError.value = null;
      return;
    }
    await loadList();
    if (listData.value) {
      await loadListItems();
    }
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  // Cleanup if needed
});
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

        <v-col class="d-flex justify-end">
          <StandardButton
            title="Add Item"
            icon="mdi-plus"
            @click="openAddItemDialog"
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12">

          <div v-if="isLoading">
            <v-card class="pa-4">
              <v-card-text class="text-center">
                <v-progress-circular indeterminate />
                <p class="mt-2">Loading list...</p>
              </v-card-text>
            </v-card>
          </div>
          <div v-else-if="listData">
            <div v-if="itemsLoading" class="text-center pa-4">
              <v-progress-circular indeterminate size="32" />
              <p class="mt-2">Loading items...</p>
            </div>
            <v-alert
              v-else-if="itemsError"
              type="error"
              variant="tonal"
              class="mb-4"
            >
              {{ itemsError }}
            </v-alert>
            <div v-else-if="listItems.length > 0">
              <ListItemCard
                v-for="item in listItems"
                :key="item.id"
                :id="item.id"
                :title="item.productName"
                :quantity="item.quantity"
                :completed="item.completed"
                @update-quantity="(quantity) => handleUpdateQuantity(item.id, quantity)"
                @toggle-complete="(completed) => handleToggleComplete(item.id, completed)"
                @delete="(id) => handleDeleteItem(id)"
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon icon="mdi-inbox" size="64" color="grey-lighten-1" />
              <p class="mt-2 text-h6 text-medium-emphasis">This list is empty</p>
              <p class="text-body-2 text-medium-emphasis">
                Add your first item to get started
              </p>
            </div>
          </div>
          <div v-else>
            <v-card class="pa-4">
              <v-card-title>List Not Found</v-card-title>
              <v-card-text>
                <p v-if="loadError">{{ loadError }}</p>
                <p v-else>No list found with ID: <strong>{{ route.params.id }}</strong></p>
                <v-btn @click="goBack" color="primary" class="mt-2">
                  Back to Lists
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <AddItemDialog
      v-model="showAddItemDialog"
      @add-item="handleAddItem"
    />

    <!-- Toast Messages -->
    <ToastNotification
      v-model="showToast"
      :message="toastMessage"
      :type="toastType"
    />
  </div>
</template>

<style scoped>
.add-item-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
