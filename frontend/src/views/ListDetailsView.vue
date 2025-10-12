<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import AddItemDialog from "@/components/dialog/AddItemDialog.vue";
import ConfirmDeleteDialog from "@/components/dialog/ConfirmDeleteDialog.vue";
import EditListDialog from "@/components/dialog/EditListDialog.vue";
import ShareListDialog from "@/components/dialog/ShareListDialog.vue";
import ListItemCard from "@/components/List/ListItemCard.vue";
import StandardButton from "@/components/StandardButton.vue";
import ToastNotification from "@/components/ToastNotification.vue";
import { useToast } from "@/composables/useToast";
import CategoryApi from "@/services/category";
import ListItemApi from "@/services/listItem";
import ProductApi from "@/services/product";
import ShoppingListApi from "@/services/shoppingList";

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
const { showToast, toastMessage, toastType, showSuccess, showError } =
  useToast();

const listId = computed(() => Number(route.params.id));
const listData = ref<ShoppingListDetail | null>(null);
const listItems = ref<ShoppingListItem[]>([]);
const isLoading = ref(true);
const itemsLoading = ref(false);
const loadError = ref<string | null>(null);
const itemsError = ref<string | null>(null);
const showAddItemDialog = ref(false);
const showEditDialog = ref(false);
const showShareDialog = ref(false);
const showDeleteDialog = ref(false);

// Computed property to sort items: unpurchased alphabetically first, then purchased alphabetically
const sortedListItems = computed(() => {
  return [...listItems.value].sort((a, b) => {
    // First, sort by completed status (false comes before true)
    if (a.completed !== b.completed) {
      return a.completed ? 1 : -1;
    }
    // Then, sort alphabetically by product name
    const nameA = (a.productName || "").toLowerCase();
    const nameB = (b.productName || "").toLowerCase();
    return nameA.localeCompare(nameB);
  });
});

const DEFAULT_CATEGORY_NAME = "Miscellaneous";
const DEFAULT_CATEGORY_ICON = "mdi-shape";
let cachedDefaultCategoryId: number | null | undefined;
let defaultCategoryLookup: Promise<number | null> | null = null;

async function ensureDefaultCategoryId(): Promise<number | null> {
  if (cachedDefaultCategoryId !== undefined) {
    return cachedDefaultCategoryId ?? null;
  }
  if (defaultCategoryLookup) {
    return defaultCategoryLookup;
  }

  defaultCategoryLookup = (async () => {
    try {
      const response = await CategoryApi.getAll();
      const collection = Array.isArray(response)
        ? response
        : Array.isArray((response as any)?.data)
        ? (response as any).data
        : [];

      const match = collection.find(
        (category: any) =>
          typeof category?.name === "string" &&
          category.name.trim().toLowerCase() ===
            DEFAULT_CATEGORY_NAME.toLowerCase()
      );

      if (match?.id) {
        cachedDefaultCategoryId = Number(match.id);
        return cachedDefaultCategoryId;
      }

      const created = await CategoryApi.add({
        name: DEFAULT_CATEGORY_NAME,
        metadata: { icon: DEFAULT_CATEGORY_ICON },
      });

      if (created?.id) {
        cachedDefaultCategoryId = Number(created.id);
        return cachedDefaultCategoryId;
      }
    } catch (error) {
      console.warn("Unable to ensure default category", error);
    } finally {
      defaultCategoryLookup = null;
    }

    cachedDefaultCategoryId = null;
    return cachedDefaultCategoryId;
  })();

  return defaultCategoryLookup;
}

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
  const raw = data && typeof data === "object" ? data.item ?? data : {};
  const product = raw?.product ?? data?.product;
  const parsedId = Number(raw?.id ?? raw?.itemId);
  const parsedProductId = Number(product?.id ?? raw?.productId ?? 0);

  return {
    id: Number.isFinite(parsedId) ? parsedId : 0,
    productId: Number.isFinite(parsedProductId) ? parsedProductId : 0,
    productName: product?.name ?? raw?.productName ?? "Unnamed product",
    quantity: Number.isFinite(Number(raw?.quantity))
      ? Number(raw?.quantity)
      : 1,
    unit: raw?.unit ?? "unit",
    completed: Boolean(raw?.purchased ?? raw?.completed),
    lastPurchasedAt: raw?.lastPurchasedAt ?? null,
    metadata: (raw?.metadata ?? {}) as Record<string, any>,
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
    if (error && typeof error === "object" && "message" in error) {
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
    const collection = Array.isArray(data)
      ? data
      : Array.isArray((data as any)?.data)
      ? (data as any).data
      : Array.isArray((data as any)?.items)
      ? (data as any).items
      : [];
    listItems.value = collection.map(mapListItem);
  } catch (error) {
    console.error("Error fetching list items:", error);
    if (error && typeof error === "object" && "message" in error) {
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

async function handleUpdateQuantity(
  itemId: number | string,
  newQuantity: number
) {
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
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Unable to update quantity."
    );
  }
}

async function handleToggleComplete(
  itemId: number | string,
  completed: boolean
) {
  const numericId = Number(itemId);
  if (!Number.isFinite(numericId)) return;
  const target = listItems.value.find((item) => Number(item.id) === numericId);
  if (!target) return;

  const previous = target.completed;
  target.completed = completed;

  try {
    await (completed
      ? ListItemApi.markAsPurchased(listId.value, numericId)
      : ListItemApi.markAsNotPurchased(listId.value, numericId));
    showSuccess(
      completed ? "Marked item as purchased." : "Marked item as pending."
    );

    // Check if all items are completed and auto-send to history
    if (completed) {
      await checkAndAutoCompleteList();
    }
  } catch (error) {
    console.error("Error toggling item state:", error);
    target.completed = previous;
    showError(
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Unable to update item."
    );
  }
}

async function checkAndAutoCompleteList() {
  // Check if all items are completed
  if (listItems.value.length === 0) return; // Don't auto-complete empty lists

  const allCompleted = listItems.value.every((item) => item.completed);
  if (!allCompleted || !listData.value) return;

  try {
    // Send list to history by updating metadata
    await ShoppingListApi.modify(listId.value, {
      name: listData.value.name,
      description: listData.value.description,
      recurring: listData.value.recurring,
      metadata: {
        icon: listData.value.icon,
        itemsCount: listItems.value.length,
        ...listData.value.metadata,
        status: "completed",
      },
    });

    showSuccess("🎉 All items completed! List sent to history.");

    // Navigate back to lists after a short delay to let user see the message
    setTimeout(() => {
      goBack();
    }, 2000);
  } catch (error) {
    console.error("Error auto-completing list:", error);
    // Don't show error to user as this is automatic behavior
  }
}

async function handleDeleteItem(itemId: number | string) {
  const numericId = Number(itemId);
  if (!Number.isFinite(numericId)) return;

  const previousItems = [...listItems.value];
  listItems.value = listItems.value.filter(
    (item) => Number(item.id) !== numericId
  );

  try {
    await ListItemApi.remove(listId.value, numericId);
    showSuccess("Item removed from the list.");

    // Check if all remaining items are completed after deletion
    await checkAndAutoCompleteList();
  } catch (error) {
    console.error("Error deleting item:", error);
    listItems.value = previousItems;
    showError(
      error && typeof error === "object" && "message" in error
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
    if (match?.id) {
      if (!match.category?.id) {
        const defaultCategoryId = await ensureDefaultCategoryId();
        if (defaultCategoryId) {
          try {
            await ProductApi.modify(match.id, {
              name: match.name ?? trimmed,
              category: { id: defaultCategoryId },
            });
          } catch (error) {
            console.warn("Unable to assign default category to product", error);
          }
        }
      }
      return match.id;
    }
  } catch (error) {
    console.warn("Unable to find existing product", error);
  }

  try {
    const defaultCategoryId = await ensureDefaultCategoryId();
    const data = await ProductApi.add({
      name: trimmed,
      metadata: {},
      ...(defaultCategoryId ? { category: { id: defaultCategoryId } } : {}),
    });
    if (!data?.id) {
      throw new Error("Unable to create product for list item.");
    }
    return data.id;
  } catch (error) {
    if (
      error &&
      typeof error === "object" &&
      "message" in error &&
      (error as any).message?.includes("409")
    ) {
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
      error && typeof error === "object" && "message" in error
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

function openEditDialog() {
  showEditDialog.value = true;
}

function openShareDialog() {
  showShareDialog.value = true;
}

function openDeleteDialog() {
  showDeleteDialog.value = true;
}

async function handleEditList(data: {
  id: number;
  name: string;
  description: string;
  recurring: boolean;
  icon: string;
}) {
  if (!listData.value) return;

  try {
    const response = await ShoppingListApi.modify(listId.value, {
      name: data.name,
      description: data.description,
      recurring: data.recurring,
      metadata: {
        icon: data.icon,
        itemsCount: listItems.value.length,
        ...listData.value.metadata,
      },
    });

    // Update local state
    listData.value.name = data.name;
    listData.value.title = data.name;
    listData.value.description = data.description;
    listData.value.recurring = data.recurring;
    listData.value.icon = data.icon;
    listData.value.metadata = {
      ...listData.value.metadata,
      icon: data.icon,
    };

    showSuccess("List updated successfully.");

    // Trigger a custom event that the NavBar can listen to for title updates
    window.dispatchEvent(
      new CustomEvent("list-updated", {
        detail: {
          listId: listId.value,
          newName: data.name,
        },
      })
    );
  } catch (error) {
    console.error("Error updating list:", error);
    showError(
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Unable to update list."
    );
  } finally {
    showEditDialog.value = false;
  }
}

async function handleShareList(data: { emails: string[] }) {
  showShareDialog.value = false;

  let successCount = 0;
  const failedEmails: string[] = [];

  try {
    // Process emails sequentially to avoid overwhelming the server
    for (const email of data.emails) {
      try {
        await ShoppingListApi.share(listId.value, email);
        successCount++;
      } catch (error) {
        console.error(`Failed to share with ${email}:`, error);
        failedEmails.push(email);
      }
    }

    // Provide detailed feedback based on results
    if (successCount === data.emails.length) {
      showSuccess(
        data.emails.length === 1
          ? "List shared successfully."
          : `List shared with all ${data.emails.length} users.`
      );
    } else if (successCount > 0) {
      showSuccess(
        `List shared with ${successCount} of ${data.emails.length} users.`
      );
      if (failedEmails.length > 0) {
        console.warn("Failed to share with:", failedEmails);
      }
    } else {
      showError(
        "Unable to share list with any of the provided email addresses."
      );
    }

    // Refresh the list to show updated sharing status
    await loadList();
  } catch (error) {
    console.error("Error in share process:", error);
    showError(
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Unable to share list."
    );
  }
}

async function handleSendToHistory() {
  if (!listData.value) return;

  try {
    // Call the API to update the list
    await ShoppingListApi.modify(listId.value, {
      name: listData.value.name,
      description: listData.value.description,
      recurring: listData.value.recurring,
      metadata: {
        icon: listData.value.icon,
        itemsCount: listItems.value.length,
        ...listData.value.metadata,
        status: "completed",
      },
    });

    showSuccess("🎉 List sent to history!");

    // Navigate back to lists after a short delay to let user see the message
    setTimeout(() => {
      goBack();
    }, 2000);
  } catch (error) {
    console.error("Error sending list to history:", error);
    showError(
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Failed to send list to history"
    );
  }
}

async function handleDeleteList() {
  if (!listData.value) return;

  try {
    await ShoppingListApi.remove(listId.value);
    showSuccess("List deleted successfully.");

    // Navigate back to lists after a short delay to let user see the message
    setTimeout(() => {
      goBack();
    }, 1500);
  } catch (error) {
    console.error("Error deleting list:", error);
    showError(
      error && typeof error === "object" && "message" in error
        ? (error as { message: string }).message
        : "Unable to delete list."
    );
  } finally {
    showDeleteDialog.value = false;
  }
}

function goBack() {
  // Check if we came from history route and go back appropriately
  if (route.path.startsWith("/lists/history/")) {
    router.push("/lists/history");
  } else {
    router.push("/lists");
  }
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
      <v-row align="center" class="mb-4">
        <v-col cols="auto">
          <v-btn
            icon="mdi-arrow-left"
            size="large"
            variant="text"
            @click="goBack"
          />
        </v-col>

        <v-col class="d-flex justify-end align-center" style="gap: 8px">
          <StandardButton
            icon="mdi-plus"
            title="Add Item"
            @click="openAddItemDialog"
          />

          <v-menu v-if="listData" offset-y>
            <template #activator="{ props: activator }">
              <v-btn
                v-bind="activator"
                icon="mdi-dots-horizontal"
                size="large"
                variant="text"
                class="list-menu-btn"
              />
            </template>
            <v-list class="action-menu">
              <v-list-item class="menu-item" @click="openEditDialog">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-pencil</v-icon>
                </template>
                <v-list-item-title class="menu-title"
                  >Edit List</v-list-item-title
                >
              </v-list-item>

              <v-list-item class="menu-item" @click="openShareDialog">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-share-variant</v-icon>
                </template>
                <v-list-item-title class="menu-title"
                  >Share List</v-list-item-title
                >
              </v-list-item>

              <v-list-item class="menu-item" @click="handleSendToHistory">
                <template #prepend>
                  <v-icon class="menu-icon">mdi-checkbox-marked-circle</v-icon>
                </template>
                <v-list-item-title class="menu-title"
                  >Mark as Completed</v-list-item-title
                >
              </v-list-item>

              <v-list-item
                class="menu-item delete-item"
                @click="openDeleteDialog"
              >
                <template #prepend>
                  <v-icon class="menu-icon">mdi-delete</v-icon>
                </template>
                <v-list-item-title class="menu-title"
                  >Delete List</v-list-item-title
                >
              </v-list-item>
            </v-list>
          </v-menu>
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
              class="mb-4"
              type="error"
              variant="tonal"
            >
              {{ itemsError }}
            </v-alert>
            <div v-else-if="sortedListItems.length > 0">
              <ListItemCard
                v-for="item in sortedListItems"
                :id="item.id"
                :key="item.id"
                :completed="item.completed"
                :quantity="item.quantity"
                :title="item.productName"
                @delete="(id) => handleDeleteItem(id)"
                @toggle-complete="
                  (completed) => handleToggleComplete(item.id, completed)
                "
                @update-quantity="
                  (quantity) => handleUpdateQuantity(item.id, quantity)
                "
              />
            </div>
            <div v-else class="text-center pa-8">
              <v-icon color="grey-lighten-1" icon="mdi-inbox" size="64" />
              <p class="mt-2 text-h6 text-medium-emphasis">
                This list is empty
              </p>
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
                <p v-else>
                  No list found with ID: <strong>{{ route.params.id }}</strong>
                </p>
                <v-btn class="mt-2" color="primary" @click="goBack">
                  Back to Lists
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <AddItemDialog v-model="showAddItemDialog" @add-item="handleAddItem" />

    <!-- Edit List Dialog -->
    <EditListDialog
      v-model="showEditDialog"
      :list-data="listData"
      @edit-list="handleEditList"
    />

    <!-- Share List Dialog -->
    <ShareListDialog
      v-model="showShareDialog"
      :list-id="listData?.id"
      :list-name="listData?.title"
      @share-list="handleShareList"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      description="This action cannot be undone. All items in this list will be permanently deleted."
      :item-name="listData?.title"
      item-type="list"
      @confirm="handleDeleteList"
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

.list-menu-btn {
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.list-menu-btn:hover {
  background-color: var(--primary-green);
  color: white;
}

.action-menu {
  min-width: 150px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-item {
  padding: 12px 16px;
  color: var(--text-primary);
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-icon {
  margin-right: 12px;
  transition: color 0.2s ease;
  color: var(--text-primary) !important;
}

.menu-item:hover {
  background-color: var(--primary-green);
  color: white;
}

.menu-item:hover .menu-icon {
  color: white !important;
}

.menu-title {
  font-weight: 600;
}
</style>
