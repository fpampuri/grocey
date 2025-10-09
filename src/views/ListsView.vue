<script setup lang="ts">
  import ListCard from "@/components/List/ListCard.vue";
  import StandardButton from "@/components/StandardButton.vue";
  import SearchBar from "@/components/SearchBar.vue";
  import CreateListDialog from "@/components/dialog/CreateListDialog.vue";
  import EditListDialog from "@/components/dialog/EditListDialog.vue";
  import ConfirmDeleteDialog from "@/components/dialog/ConfirmDeleteDialog.vue";
  import ShareListDialog from "@/components/dialog/ShareListDialog.vue";
  import { useRouter } from "vue-router";
  import { ref, onMounted, computed } from "vue";

  const router = useRouter();

  // Define the List type
  interface List {
    id: number;
    title: string;
    icon: string;
    itemsCount: number;
    users: Array<{
      id: number;
      name: string;
      email: string;
      role: string;
    }>;
    createdBy: number;
    createdAt: string;
    isFavorite?: boolean;
  }

  const lists = ref<List[]>([]);
  const toDeleteList = ref<List | null>(null);
  const isLoading = ref(true);
  const searchQuery = ref("");
  const sortBy = ref("Date");
  const showCreateDialog = ref(false);
  const showEditDialog = ref(false);
  const showFavoritesOnly = ref(false);
  const showDeleteDialog = ref(false);
  const showShareDialog = ref(false);
  const listToShare = ref<List | null>(null);
  const listToEdit = ref<List | null>(null);

  // Filtered list by search and favorites
  const filteredLists = computed(() => {
    let filtered = lists.value;

    // Filter by favorites if enabled
    if (showFavoritesOnly.value) {
      filtered = filtered.filter((list) => list.isFavorite);
    }

    // Filter by search query
    if (searchQuery.value) {
      filtered = filtered.filter((list) =>
        list.title.toLowerCase().includes(searchQuery.value.toLowerCase())
      );
    }

    return filtered;
  });

  // Sort lists based on selected criteria
  function sortLists(listArray: List[]): List[] {
    if (sortBy.value === "Name") {
      return [...listArray].sort((a, b) => a.title.localeCompare(b.title));
    }
    if (sortBy.value === "Items") {
      return [...listArray].sort((a, b) => b.itemsCount - a.itemsCount);
    }
    if (sortBy.value === "Date") {
      return [...listArray].sort((a, b) => 
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
    }
    return listArray;
  }

  // Fetch lists from local mock API
  async function fetchLists() {
    try {
      const response = await fetch("/api/lists.json");

      if (!response.ok) throw new Error("Error fetching lists");

      const lists = await response.json();

      // Lists already have the correct structure, just return them
      return lists;
    } catch (error) {
      console.error("Error fetching lists:", error);
      return [];
    }
  }

  // Fetch real item counts for each list from list-items.json
  async function fetchItemCounts() {
    try {
      const response = await fetch("/api/list-items.json");
      if (!response.ok) throw new Error("Error fetching list items");

      const items = await response.json();

      // Count unique items per list
      const countsByListId = items.reduce(
        (acc: Record<number, number>, item: any) => {
          acc[item.listId] = (acc[item.listId] || 0) + 1;
          return acc;
        },
        {}
      );

      // Update lists with real counts
      lists.value = lists.value.map((list) => ({
        ...list, // Keep existing properties
        itemsCount: countsByListId[list.id] || 0, // Update with real count or 0
      }));
    } catch (error) {
      console.error("Error fetching item counts:", error);
    }
  }

  onMounted(async () => {
    isLoading.value = true;
    lists.value = await fetchLists();
    await fetchItemCounts(); // Get real item counts
    isLoading.value = false;
  });

  function handleListClick(listItem: any) {
    router.push(`/lists/${listItem.id}`);
  }

  function handleStarToggle(isStarred: boolean, listId: number) {
    // Update the list's favorite status in the data
    const listIndex = lists.value.findIndex((list) => list.id === listId);
    if (listIndex !== -1) {
      lists.value[listIndex].isFavorite = isStarred;
    }
  }

  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  function handleSelectionToggle(isSelected: boolean) {
  }

  function handleEdit(listId: number) {
    const list = lists.value.find((l) => l.id === listId);
    if (!list) return;
    
    listToEdit.value = list;
    showEditDialog.value = true;
  }

  function handleEditList(data: { id: number; name: string; icon: string }) {
    const list = lists.value.find((l) => l.id === data.id);
    if (!list) return;
    
    list.title = data.name;
    list.icon = data.icon;
    
    showEditDialog.value = false;
    listToEdit.value = null;
  }
  //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  function handleFavoritesToggle() {
    showFavoritesOnly.value = !showFavoritesOnly.value;
  }

  function handleDeleteList(listId: number) {
    const deleteList = lists.value.find((list) => list.id === listId);

    if (!deleteList) {
      return;
    }

    toDeleteList.value = deleteList;
    showDeleteDialog.value = true;
  }

  function confirmDelete() {
    if (!toDeleteList.value) {
      return;
    }
    
    try {

      // Remove the list from the local state
      lists.value = lists.value.filter(
        (list) => list.id !== toDeleteList.value!.id
      );
      
      } catch (error) {
    }

    showDeleteDialog.value = false;
    toDeleteList.value = null;
  }

  function cancelDelete() {
    showDeleteDialog.value = false;
    toDeleteList.value = null;
  }

  function handleShare(listId: number) {
    const shareList = lists.value.find((list) => list.id === listId);
    
    if (!shareList) {
      console.error("List not found for sharing");
      return;
    }
    
    listToShare.value = shareList;
    showShareDialog.value = true;
  }

  function handleShareList(data: { emails: string[] }) {
    if (!listToShare.value) return;
    
    
    // TODO: Implement actual sharing logic here
    // This would typically make an API call to share the list with multiple users
    
    
    listToShare.value = null;
  }

  function handleAddList() {
    showCreateDialog.value = true;
  }

  function handleCreateList(listData: { name: string; icon: string }) {
    // Create new list object
    const newList: List = {
      id: Date.now(), // Use timestamp as temporary ID
      title: listData.name,
      icon: listData.icon,
      itemsCount: 0,
      isFavorite: false, // Default to not favorite
      users: [
        {
          id: 1,
          name: "Current User",
          email: "user@example.com",
          role: "owner",
        },
      ],
      createdBy: 1,
      createdAt: new Date().toISOString(),
    };

    // Add to lists
    lists.value.unshift(newList);

    // Close dialog
    showCreateDialog.value = false;
  }
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Add List Section -->
      <v-row class="mb-6" align="center">
        <v-col cols="12" md="8">
          <SearchBar v-model="searchQuery" placeholder="Search lists..." />
        </v-col>
        <v-col cols="12" md="4" class="text-md-right">
          <StandardButton
            title="Add List"
            icon="mdi-plus"
            @click="handleAddList"
          />
        </v-col>
      </v-row>

      <!-- Favorites and Sort Section -->
      <v-row class="mb-4" align="center">
        <v-col cols="auto">
          <v-btn
            variant="elevated"
            @click="handleFavoritesToggle"
            :class="
              showFavoritesOnly ? 'favorites-btn-active' : 'favorites-btn'
            "
          >
            <v-icon
              :icon="showFavoritesOnly ? 'mdi-star' : 'mdi-star-outline'"
              class="mr-2"
            />
            Favorites
          </v-btn>
        </v-col>
        <v-spacer />
        <v-col cols="auto">
          <div class="d-flex align-center">
            <span class="mr-3 text-medium-emphasis">Sort by:</span>
            <v-select
              v-model="sortBy"
              :items="['Date', 'Name', 'Items']"
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
        <p class="mt-4 text-h6">Loading lists...</p>
      </div>

      <!-- Lists grid -->
      <v-row v-else>
        <v-col
          cols="12"
          md="6"
          lg="4"
          v-for="item in sortLists(filteredLists)"
          :key="item.id"
        >
          <ListCard
            :title="item.title"
            :icon="item.icon"
            :itemsCount="item.itemsCount"
            :starred="item.isFavorite || false"
            @click="() => handleListClick(item)"
            @toggle-star="(isStarred) => handleStarToggle(isStarred, item.id)"
            @toggle-selection="handleSelectionToggle"
            @edit="() => handleEdit(item.id)"
            @delete="() => handleDeleteList(item.id)"
            @share="() => handleShare(item.id)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredLists.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon icon="mdi-magnify" size="64" color="grey-lighten-1" />
        <p class="mt-2 text-h6 text-medium-emphasis">No lists found</p>
        <p class="text-body-2 text-medium-emphasis">
          Try adjusting your search terms
        </p>
      </div>
    </v-container>

    <!-- Create List Dialog -->
    <CreateListDialog
      v-model="showCreateDialog"
      @create-list="handleCreateList"
    />

    <!-- Edit List Dialog -->
    <EditListDialog
      v-model="showEditDialog"
      :list-data="listToEdit"
      @edit-list="handleEditList"
    />

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      item-type="list"
      :item-name="toDeleteList?.title"
      description="This action cannot be undone. All items in this list will be permanently deleted."
      @confirm="confirmDelete"
    />

    <!-- Share List Dialog -->
    <ShareListDialog
      v-model="showShareDialog"
      :list-name="listToShare?.title"
      :list-id="listToShare?.id"
      @share-list="handleShareList"
    />
  </div>
</template>

<style scoped>
.favorites-btn-active {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
  min-width: 120px;
  min-height: 40px;
  color: white !important;
  background-color: var(--primary-green-light) !important;
}

.favorites-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
  min-width: 120px;
  min-height: 40px;
  color: var(--text-primary) !important;
  background-color: rgb(var(--v-theme-surface)) !important;
  border: 1px solid rgba(0, 0, 0, 0.12);
}

.v-theme--dark .favorites-btn {
  border-color: rgba(255, 255, 255, 0.12);
}

.favorites-btn:hover {
  color: white !important;
  background-color: var(--primary-green-light) !important;
  opacity: 0.7 !important;
}

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
</style>
