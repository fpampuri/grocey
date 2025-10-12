<script setup lang="ts">
  import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
  import { useRouter } from 'vue-router'
  import ConfirmDeleteDialog from '@/components/dialog/ConfirmDeleteDialog.vue'
  import ListCard from '@/components/List/ListCard.vue'
  import SearchBar from '@/components/SearchBar.vue'
  import StandardButton from '@/components/StandardButton.vue'
  import ToastNotification from '@/components/ToastNotification.vue'
  import { useToast } from '@/composables/useToast'
  import ListItemApi from '@/services/listItem'
  import ShoppingListApi from '@/services/shoppingList'

  const router = useRouter()
  const { showToast, toastMessage, toastType, showSuccess, showError } = useToast()

  interface ListUser {
    id: number
    name: string
    email: string
    role: 'owner' | 'collaborator'
  }

  interface ShoppingList {
    id: number
    name: string
    title: string
    description: string
    recurring: boolean
    icon: string
    metadata: Record<string, any>
    itemsCount: number
    users: ListUser[]
    createdBy: number | null
    createdAt: string
    isFavorite: boolean
  }

  interface RestorePayload {
    listId: number
  }

  const lists = ref<ShoppingList[]>([])
  const toDeleteList = ref<ShoppingList | null>(null)
  const toRestoreList = ref<ShoppingList | null>(null)
  const searchQuery = ref('')
  const sortBy = ref('Date')
  const showDeleteDialog = ref(false)
  const showRestoreDialog = ref(false)
  const showBulkDeleteDialog = ref(false)
  const isLoading = ref(false)
  const loadError = ref<string | null>(null)
  const listItemCountCache = new Map<number, number>()
  const selectedLists = ref<Set<number>>(new Set())
  let searchTimeout: ReturnType<typeof setTimeout> | null = null

  const filteredLists = computed(() => {
    let filtered = lists.value

    if (searchQuery.value.trim()) {
      const term = searchQuery.value.trim().toLowerCase()
      filtered = filtered.filter(list =>
        list.title.toLowerCase().includes(term),
      )
    }

    return filtered
  })

  const selectedCount = computed(() => selectedLists.value.size)

  function sortLists (listArray: ShoppingList[]): ShoppingList[] {
    if (sortBy.value === 'Name') {
      return [...listArray].sort((a: ShoppingList, b: ShoppingList) =>
        a.title.localeCompare(b.title),
      )
    }
    if (sortBy.value === 'Date') {
      return [...listArray].sort(
        (a: ShoppingList, b: ShoppingList) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      )
    }
    if (sortBy.value === 'Items') {
      return [...listArray].sort(
        (a: ShoppingList, b: ShoppingList) => b.itemsCount - a.itemsCount,
      )
    }
    return listArray
  }

  function mapApiList (apiList: any): ShoppingList {
    const metadata = (apiList?.metadata ?? {}) as Record<string, any>
    const icon = metadata.icon ?? 'mdi-format-list-bulleted'
    const isFavorite = Boolean(metadata.isFavorite)

    const users: ListUser[] = []
    if (apiList?.owner) {
      users.push({
        id: apiList.owner.id,
        name: [apiList.owner.name, apiList.owner.surname].filter(Boolean).join(' '),
        email: apiList.owner.email,
        role: 'owner',
      })
    }
    if (Array.isArray(apiList?.sharedWith)) {
      apiList.sharedWith.forEach((user: any) => {
        users.push({
          id: user.id,
          name: [user.name, user.surname].filter(Boolean).join(' '),
          email: user.email,
          role: 'collaborator',
        })
      })
    }

    return {
      id: apiList.id,
      name: apiList.name,
      title: apiList.name,
      description: apiList.description ?? '',
      recurring: Boolean(apiList.recurring),
      icon,
      metadata,
      itemsCount: 0, // Will be populated later
      users,
      createdBy: apiList?.owner?.id ?? null,
      createdAt: apiList.createdAt ?? new Date().toISOString(),
      isFavorite,
    }
  }

  async function fetchListItemsCount (listId: number): Promise<number> {
    if (listItemCountCache.has(listId)) {
      return listItemCountCache.get(listId)!
    }

    try {
      const response = await ListItemApi.getAll(listId)
      // Handle different response structures from the API
      const items = Array.isArray(response)
        ? response
        : Array.isArray((response as any)?.items)
          ? (response as any).items
          : Array.isArray((response as any)?.data)
            ? (response as any).data
            : []

      const count = items.length
      listItemCountCache.set(listId, count)
      return count
    } catch (error) {
      console.error('Error fetching list item count:', error)
      return 0
    }
  }

  async function ensureAllItemsCompleted (listId: number) {
    try {
      // Get all items for this list
      const data = await ListItemApi.getAll(listId)
      const items = Array.isArray(data)
        ? data
        : Array.isArray((data as any)?.data)
          ? (data as any).data
          : Array.isArray((data as any)?.items)
            ? (data as any).items
            : []

      // Mark any incomplete items as completed
      for (const item of items) {
        const itemData = item?.item ?? item
        const isPurchased = Boolean(itemData?.purchased ?? itemData?.completed)
        
        if (!isPurchased && itemData?.id) {
          try {
            await ListItemApi.markAsPurchased(listId, itemData.id)
          } catch (error) {
            console.warn(`Failed to mark item ${itemData.id} as completed:`, error)
            // Continue with other items even if one fails
          }
        }
      }
    } catch (error) {
      console.warn(`Failed to ensure all items completed for list ${listId}:`, error)
      // Don't throw error as this is background cleanup
    }
  }

  async function loadLists () {
    isLoading.value = true
    loadError.value = null

    try {
      const response = await ShoppingListApi.getAll()
      // Handle different response structures from the API
      const rawLists = Array.isArray(response)
        ? response
        : Array.isArray((response as any)?.lists)
          ? (response as any).lists
          : Array.isArray((response as any)?.data)
            ? (response as any).data
            : []

      const mapped = rawLists.map(mapApiList)

      // Filter only completed lists (status === 'completed' in metadata)
      const completedLists = mapped.filter(list => 
        list.metadata && list.metadata.status === 'completed'
      )

      // Fetch item counts for each list and ensure all items are completed
      for (const list of completedLists) {
        if (list.itemsCount) {
          listItemCountCache.set(list.id, list.itemsCount)
        } else {
          list.itemsCount = await fetchListItemsCount(list.id)
        }
        
        // Ensure all items in this completed list are marked as completed
        await ensureAllItemsCompleted(list.id)
      }

      lists.value = completedLists
    } catch (error) {
      console.error('Error loading lists:', error)
      loadError.value = error instanceof Error ? error.message : 'Unable to load history.'
      lists.value = []
    } finally {
      isLoading.value = false
    }
  }

  function handleListClick (listItem: ShoppingList) {
    router.push(`/lists/history/${listItem.id}`)
  }

  function handleSelectionToggle (listId: number, isSelected: boolean) {
    if (isSelected) {
      selectedLists.value.add(listId)
    } else {
      selectedLists.value.delete(listId)
    }
  }

  function handleDeleteList (listId: number) {
    const list = lists.value.find(l => l.id === listId)
    if (list) {
      toDeleteList.value = list
      showDeleteDialog.value = true
    }
  }

  function handleMarkUncompleted (listId: number) {
    const list = lists.value.find(l => l.id === listId)
    if (list) {
      toRestoreList.value = list
      showRestoreDialog.value = true
    }
  }

  function handleBulkDelete () {
    showBulkDeleteDialog.value = true
  }

  async function handleBulkMarkUncompleted () {
    try {
      const listIds = Array.from(selectedLists.value)
      let restoredCount = 0

      // Process lists sequentially to avoid conflicts
      for (const listId of listIds) {
        const list = lists.value.find(l => l.id === listId)
        if (!list) continue

        try {
          // Update list metadata to mark as active
          await ShoppingListApi.modify(listId, {
            name: list.name,
            description: list.description,
            recurring: list.recurring,
            metadata: {
              ...list.metadata,
              status: 'active'
            }
          })

          // Mark all items as not purchased (reset)
          const response = await ListItemApi.getAll(listId)
          const items = Array.isArray(response) ? response : (response as any)?.data || []
          
          for (const item of items) {
            const itemData = item?.item ?? item
            if (itemData?.purchased || itemData?.completed) {
              await ListItemApi.markAsNotPurchased(listId, itemData.id || itemData.itemId)
            }
          }

          restoredCount++
        } catch (error) {
          console.error(`Error restoring list ${listId}:`, error)
        }
      }

      // Remove restored lists from history view
      const restoredIds = Array.from(selectedLists.value)
      lists.value = lists.value.filter(list => !restoredIds.includes(list.id))
      
      // Clear selections
      selectedLists.value.clear()

      // Show success message
      if (restoredCount === listIds.length) {
        showSuccess(
          restoredCount === 1 
            ? 'List marked as uncompleted successfully' 
            : `${restoredCount} lists marked as uncompleted successfully`
        )
      } else if (restoredCount > 0) {
        showSuccess(
          `${restoredCount} of ${listIds.length} lists marked as uncompleted successfully`
        )
      } else {
        showError('Failed to mark lists as uncompleted')
      }
    } catch (error) {
      console.error('Error in bulk mark uncompleted:', error)
      showError('Failed to mark lists as uncompleted')
    }
  }

  async function confirmDelete () {
    if (!toDeleteList.value) return
    const targetId = toDeleteList.value.id

    try {
      await ShoppingListApi.remove(targetId)
      lists.value = lists.value.filter(list => list.id !== targetId)
      listItemCountCache.delete(targetId)
      showSuccess('List deleted successfully.')
    } catch (error) {
      console.error('Error deleting list:', error)
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to delete list.',
      )
    } finally {
      showDeleteDialog.value = false
      toDeleteList.value = null
    }
  }

  async function confirmRestore () {
    if (!toRestoreList.value) return
    const targetId = toRestoreList.value.id

    try {
      // Update list metadata to mark as active
      const updatedMetadata = { 
        ...toRestoreList.value.metadata, 
        status: 'active' 
      }
      
      await ShoppingListApi.modify(targetId, {
        name: toRestoreList.value.name,
        description: toRestoreList.value.description,
        recurring: toRestoreList.value.recurring,
        metadata: updatedMetadata,
      })

      // Mark all items as not purchased (reset)
      const response = await ListItemApi.getAll(targetId)
      const items = Array.isArray(response) ? response : (response as any)?.data || []
      
      for (const item of items) {
        if (item.purchased || item.completed) {
          await ListItemApi.markAsNotPurchased(targetId, item.id || item.itemId)
        }
      }

      // Remove from history view
      lists.value = lists.value.filter(list => list.id !== targetId)
      listItemCountCache.delete(targetId)
      showSuccess('List restored successfully.')
    } catch (error) {
      console.error('Error restoring list:', error)
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to restore list.',
      )
    } finally {
      showRestoreDialog.value = false
      toRestoreList.value = null
    }
  }

  async function confirmBulkDelete () {
    try {
      const listIds = Array.from(selectedLists.value)
      let deletedCount = 0

      // Delete lists sequentially to avoid conflicts
      for (const listId of listIds) {
        try {
          await ShoppingListApi.remove(listId)
          listItemCountCache.delete(listId)
          deletedCount++
        } catch (error) {
          console.error(`Error deleting list ${listId}:`, error)
        }
      }

      // Remove deleted lists from view
      const deletedIds = Array.from(selectedLists.value)
      lists.value = lists.value.filter(list => !deletedIds.includes(list.id))
      
      // Clear selections
      selectedLists.value.clear()

      // Show success message
      if (deletedCount === listIds.length) {
        showSuccess(
          deletedCount === 1 
            ? 'List deleted successfully' 
            : `${deletedCount} lists deleted successfully`
        )
      } else if (deletedCount > 0) {
        showSuccess(
          `${deletedCount} of ${listIds.length} lists deleted successfully`
        )
      } else {
        showError('Failed to delete lists')
      }
    } catch (error) {
      console.error('Error in bulk delete:', error)
      showError('Failed to delete lists')
    } finally {
      showBulkDeleteDialog.value = false
    }
  }

  function cancelDelete () {
    showDeleteDialog.value = false
    toDeleteList.value = null
  }

  function cancelRestore () {
    showRestoreDialog.value = false
    toRestoreList.value = null
  }

  function goBack () {
    router.push('/lists')
  }

  watch(searchQuery, () => {
    if (searchTimeout) clearTimeout(searchTimeout)
    searchTimeout = setTimeout(() => {
      loadLists()
    }, 400)
  })

  onMounted(() => {
    loadLists()
  })

  onBeforeUnmount(() => {
    if (searchTimeout) clearTimeout(searchTimeout)
  })
</script>

<template>
  <div class="pa-3">
    <v-container fluid>
      <!-- Search and Actions Section -->
      <v-row align="center" class="mb-6">
        <v-col cols="12" md="8">
          <SearchBar v-model="searchQuery" placeholder="Search completed lists..." />
        </v-col>
      </v-row>

      <!-- Back Button and Bulk Actions -->
      <v-row align="center" class="mb-4">
        <v-col cols="auto">
          <v-btn
            icon="mdi-arrow-left"
            size="large"
            variant="text"
            @click="goBack"
          />
        </v-col>
        <v-col v-if="selectedCount > 0" cols="auto">
          <StandardButton
            icon="mdi-delete"
            :title="`Delete (${selectedCount})`"
            variant="danger"
            @click="handleBulkDelete"
          />
        </v-col>
        <v-col v-if="selectedCount > 0" cols="auto">
          <StandardButton
            icon="mdi-backup-restore"
            :title="`Mark as Uncompleted (${selectedCount})`"
            variant="secondary"
            @click="handleBulkMarkUncompleted"
          />
        </v-col>
        <v-spacer />
        <v-col cols="auto">
          <div class="d-flex align-center">
            <span class="mr-3 text-medium-emphasis">Sort by:</span>
            <v-select
              v-model="sortBy"
              density="compact"
              hide-details
              :items="['Date', 'Name', 'Items']"
              style="min-width: 100px"
              variant="outlined"
            />
          </div>
        </v-col>
      </v-row>

      <v-row v-if="loadError" class="mb-4">
        <v-col cols="12">
          <v-alert
            v-if="loadError"
            type="error"
            variant="tonal"
          >
            {{ loadError }}
          </v-alert>
        </v-col>
      </v-row>

      <!-- Loading state -->
      <div v-if="isLoading" class="text-center pa-8">
        <v-progress-circular indeterminate size="64" />
        <p class="mt-4 text-h6">Loading history...</p>
      </div>

      <!-- Lists grid -->
      <v-row v-else>
        <v-col
          v-for="item in sortLists(filteredLists)"
          :key="item.id"
          cols="12"
          lg="4"
          md="6"
        >
          <ListCard
            :icon="item.icon"
            :items-count="item.itemsCount"
            :selected="selectedLists.has(item.id)"
            :starred="item.isFavorite || false"
            :title="item.title"
            :show-edit="false"
            :show-share="false"
            :show-send-to-history="false"
            :show-mark-uncompleted="true"
            @click="() => handleListClick(item)"
            @delete="() => handleDeleteList(item.id)"
            @mark-uncompleted="() => handleMarkUncompleted(item.id)"
            @toggle-selection="(isSelected) => handleSelectionToggle(item.id, isSelected)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredLists.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-magnify" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">No completed lists found</p>
        <p class="text-body-2 text-medium-emphasis">
          Try adjusting your search terms
        </p>
      </div>

      <!-- No completed lists message -->
      <div
        v-if="!isLoading && filteredLists.length === 0 && !searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-history" size="64" />
        <p class="mt-2 text-h6 text-medium-emphasis">
          No completed lists yet
        </p>
        <p class="text-body-2 text-medium-emphasis">
          Completed lists will appear here
        </p>
      </div>
    </v-container>

    <!-- Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showDeleteDialog"
      description="This action cannot be undone. The list will be permanently deleted."
      :item-name="toDeleteList?.title"
      item-type="list"
      @confirm="confirmDelete"
    />

    <!-- Restore Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showRestoreDialog"
      description="Restore this list? All items will be reset to incomplete and it will return to your active lists."
      :item-name="toRestoreList?.title"
      item-type="list"
      title="Restore List"
      confirm-text="Restore"
      @confirm="confirmRestore"
      @cancel="cancelRestore"
    />

    <!-- Bulk Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showBulkDeleteDialog"
      :confirm-text="selectedCount === 1 ? 'Delete List' : `Delete ${selectedCount} Lists`"
      :description="selectedCount === 1 ? 'This action cannot be undone. The list will be permanently deleted.' : `This action cannot be undone. ${selectedCount} lists will be permanently deleted.`"
      item-name=""
      :item-type="selectedCount === 1 ? 'list' : 'lists'"
      :title="selectedCount === 1 ? 'Delete List' : `Delete ${selectedCount} Lists`"
      @confirm="confirmBulkDelete"
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
</style>