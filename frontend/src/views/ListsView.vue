<script setup lang="ts">
  import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
  import { useRouter } from 'vue-router'
  import ConfirmDeleteDialog from '@/components/dialog/ConfirmDeleteDialog.vue'
  import CreateListDialog from '@/components/dialog/CreateListDialog.vue'
  import EditListDialog from '@/components/dialog/EditListDialog.vue'
  import ShareListDialog from '@/components/dialog/ShareListDialog.vue'
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

  interface SharePayload {
    emails: string[]
  }

  interface CreatePayload {
    name: string
    description: string
    recurring: boolean
    icon: string
  }

  interface UpdatePayload extends CreatePayload {
    id: number
  }

  const lists = ref<ShoppingList[]>([])
  const toDeleteList = ref<ShoppingList | null>(null)
  const searchQuery = ref('')
  const sortBy = ref('Date')
  const showCreateDialog = ref(false)
  const showEditDialog = ref(false)
  const showFavoritesOnly = ref(false)
  const showDeleteDialog = ref(false)
  const showBulkDeleteDialog = ref(false)
  const showShareDialog = ref(false)
  const listToShare = ref<ShoppingList | null>(null)
  const listToEdit = ref<ShoppingList | null>(null)
  const isLoading = ref(false)
  const loadError = ref<string | null>(null)
  const listItemCountCache = new Map<number, number>()
  const selectedLists = ref<Set<number>>(new Set())
  let searchTimeout: ReturnType<typeof setTimeout> | null = null

  const filteredLists = computed(() => {
    let filtered = lists.value

    if (showFavoritesOnly.value) {
      filtered = filtered.filter(list => list.isFavorite)
    }

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
      return [...listArray].sort((a, b) => a.title.localeCompare(b.title))
    }
    if (sortBy.value === 'Items') {
      return [...listArray].sort((a, b) => b.itemsCount - a.itemsCount)
    }
    if (sortBy.value === 'Date') {
      return [...listArray].sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      )
    }
    return listArray
  }

  function mapApiList (data: any): ShoppingList {
    const metadata = (data?.metadata ?? {}) as Record<string, any>
    const icon = metadata.icon ?? 'mdi-format-list-bulleted'
    const isFavorite = Boolean(metadata.isFavorite)
    const items = Array.isArray(data?.items) ? data.items : []

    const users: ListUser[] = []
    if (data?.owner) {
      users.push({
        id: data.owner.id,
        name: [data.owner.name, data.owner.surname].filter(Boolean).join(' '),
        email: data.owner.email,
        role: 'owner',
      })
    }
    if (Array.isArray(data?.sharedWith)) {
      data.sharedWith.forEach((user: any) => {
        users.push({
          id: user.id,
          name: [user.name, user.surname].filter(Boolean).join(' '),
          email: user.email,
          role: 'collaborator',
        })
      })
    }

    return {
      id: data.id,
      name: data.name,
      title: data.name,
      description: data.description ?? '',
      recurring: Boolean(data.recurring),
      icon,
      metadata,
      itemsCount: metadata.itemsCount ?? items.length ?? 0,
      users,
      createdBy: data?.owner?.id ?? null,
      createdAt: data.createdAt ?? new Date().toISOString(),
      isFavorite,
    }
  }

  async function fetchListItemsCount (listId: number): Promise<number> {
    if (listItemCountCache.has(listId)) {
      return listItemCountCache.get(listId) ?? 0
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

      for (const list of mapped) {
        if (list.itemsCount) {
          listItemCountCache.set(list.id, list.itemsCount)
        } else {
          list.itemsCount = await fetchListItemsCount(list.id)
        }
      }

      lists.value = mapped
    } catch (error) {
      console.error('Error loading lists:', error)
      loadError.value = error instanceof Error ? error.message : 'Unable to load shopping lists.'
      lists.value = []
    } finally {
      isLoading.value = false
    }
  }

  function handleListClick (listItem: ShoppingList) {
    router.push(`/lists/${listItem.id}`)
  }

  async function handleStarToggle (isStarred: boolean, listId: number) {
    const list = lists.value.find(l => l.id === listId)
    if (!list) return

    const previous = list.isFavorite
    list.isFavorite = isStarred
    list.metadata = { ...list.metadata, icon: list.icon, isFavorite: isStarred }

    try {
      await ShoppingListApi.modify(listId, {
        name: list.name,
        description: list.description,
        recurring: list.recurring,
        metadata: {
          icon: list.icon,
          isFavorite: isStarred,
          itemsCount: list.itemsCount,
          ...list.metadata,
        },
      })
      showSuccess(isStarred ? 'List marked as favorite.' : 'List removed from favorites.')
    } catch (error) {
      console.error('Error updating favorite state:', error)
      list.isFavorite = previous
      list.metadata = { ...list.metadata, isFavorite: previous }
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to update favorite state.',
      )
    }
  }

  function handleEdit (listId: number) {
    const list = lists.value.find(l => l.id === listId)
    if (!list) return
    listToEdit.value = { ...list }
    showEditDialog.value = true
  }

  async function handleEditList (data: UpdatePayload) {
    const list = lists.value.find(l => l.id === data.id)
    if (!list) return

    try {
      const response = await ShoppingListApi.modify(data.id, {
        name: data.name,
        description: data.description,
        recurring: data.recurring,
        metadata: {
          icon: data.icon,
          isFavorite: list.isFavorite,
          itemsCount: list.itemsCount,
          ...list.metadata,
        },
      })

      // Handle different response structures from the API
      const updated = (response as any)?.list ?? response
      const mapped = mapApiList(updated)
      mapped.itemsCount = list.itemsCount
      listItemCountCache.set(mapped.id, mapped.itemsCount)
      lists.value = lists.value.map(l => (l.id === mapped.id ? mapped : l))
      showSuccess('List updated successfully.')
    } catch (error) {
      console.error('Error updating list:', error)
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to update list.',
      )
    } finally {
      showEditDialog.value = false
      listToEdit.value = null
    }
  }

  function handleFavoritesToggle () {
    showFavoritesOnly.value = !showFavoritesOnly.value
  }

  function handleDeleteList (listId: number) {
    const list = lists.value.find(list => list.id === listId)
    if (!list) return
    toDeleteList.value = list
    showDeleteDialog.value = true
  }

  function handleSelectionToggle (listId: number, isSelected: boolean) {
    if (isSelected) {
      selectedLists.value.add(listId)
    } else {
      selectedLists.value.delete(listId)
    }
  }

  function handleBulkDelete () {
    showBulkDeleteDialog.value = true
  }

  async function confirmBulkDelete () {
    try {
      const listIds = Array.from(selectedLists.value)
      let deletedCount = 0

      // Delete lists sequentially to avoid SQLite transaction conflicts
      for (const listId of listIds) {
        try {
          await ShoppingListApi.remove(listId)
          deletedCount++
          listItemCountCache.delete(listId)
        } catch (error) {
          console.error(`Failed to delete list ${listId}:`, error)
        // Continue with other deletions even if one fails
        }
      }

      // Remove successfully deleted lists from the local state
      lists.value = lists.value.filter(list => !selectedLists.value.has(list.id))

      // Clear selection
      selectedLists.value.clear()

      if (deletedCount === listIds.length) {
        showSuccess(`Successfully deleted ${deletedCount} list${deletedCount === 1 ? '' : 's'}`)
      } else if (deletedCount > 0) {
        showSuccess(`Deleted ${deletedCount} of ${listIds.length} lists. Some deletions failed.`)
      } else {
        showError('Unable to delete any lists.')
      }
    } catch (error) {
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to delete lists.',
      )
    } finally {
      showBulkDeleteDialog.value = false
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

  function cancelDelete () {
    showDeleteDialog.value = false
    toDeleteList.value = null
  }

  function handleShare (listId: number) {
    const shareList = lists.value.find(list => list.id === listId)
    if (!shareList) {
      console.error('List not found for sharing')
      return
    }
    listToShare.value = shareList
    showShareDialog.value = true
  }

  async function handleShareList (data: SharePayload) {
    if (!listToShare.value) return
    const listId = listToShare.value.id
    showShareDialog.value = false

    let successCount = 0
    const failedEmails: string[] = []

    try {
      // Process emails sequentially to avoid overwhelming the server
      for (const email of data.emails) {
        try {
          await ShoppingListApi.share(listId, email)
          successCount++
        } catch (error) {
          console.error(`Failed to share with ${email}:`, error)
          failedEmails.push(email)
        }
      }

      // Provide detailed feedback based on results
      if (successCount === data.emails.length) {
        showSuccess(
          data.emails.length === 1
            ? 'List shared successfully.'
            : `List shared with all ${data.emails.length} users.`,
        )
      } else if (successCount > 0) {
        showSuccess(
          `List shared with ${successCount} of ${data.emails.length} users.`,
        )
        if (failedEmails.length > 0) {
          console.warn('Failed to share with:', failedEmails)
        }
      } else {
        showError('Unable to share list with any of the provided email addresses.')
      }

      // Refresh the lists to show updated sharing status
      await loadLists()
    } catch (error) {
      console.error('Error in share process:', error)
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to share list.',
      )
    } finally {
      listToShare.value = null
    }
  }

  function handleAddList () {
    showCreateDialog.value = true
  }

  async function handleCreateList (listData: CreatePayload) {
    try {
      const response = await ShoppingListApi.add({
        name: listData.name,
        description: listData.description,
        recurring: listData.recurring,
        metadata: {
          icon: listData.icon,
          isFavorite: false,
          itemsCount: 0,
        },
      })

      // Handle different response structures from the API
      const newList = (response as any)?.list ?? response
      const mapped = mapApiList(newList)
      listItemCountCache.set(mapped.id, 0)
      lists.value.unshift({ ...mapped, itemsCount: 0 })
      showSuccess('List created successfully.')
    } catch (error) {
      console.error('Error creating list:', error)
      showError(
        error instanceof Error
          ? error.message
          : 'Unable to create list.',
      )
    } finally {
      showCreateDialog.value = false
    }
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
      <!-- Search and Add List Section -->
      <v-row align="center" class="mb-6">
        <v-col cols="12" md="8">
          <SearchBar v-model="searchQuery" placeholder="Search lists..." />
        </v-col>
        <v-col class="text-md-right" cols="12" md="4">
          <StandardButton
            icon="mdi-plus"
            title="Add List"
            @click="handleAddList"
          />
        </v-col>
      </v-row>

      <!-- Favorites and Sort Section -->
      <v-row align="center" class="mb-4">
        <v-col cols="auto">
          <v-btn
            :class="
              showFavoritesOnly ? 'favorites-btn-active' : 'favorites-btn'
            "
            variant="elevated"
            @click="handleFavoritesToggle"
          >
            <v-icon
              class="mr-2"
              :icon="showFavoritesOnly ? 'mdi-star' : 'mdi-star-outline'"
            />
            Favorites
          </v-btn>
        </v-col>

        <v-col v-if="selectedCount > 0" cols="auto">
          <StandardButton
            icon="mdi-delete"
            :title="`Delete (${selectedCount})`"
            variant="danger"
            @click="handleBulkDelete"
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
        <p class="mt-4 text-h6">Loading lists...</p>
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
            @click="() => handleListClick(item)"
            @delete="() => handleDeleteList(item.id)"
            @edit="() => handleEdit(item.id)"
            @share="() => handleShare(item.id)"
            @toggle-selection="(isSelected) => handleSelectionToggle(item.id, isSelected)"
            @toggle-star="(isStarred) => handleStarToggle(isStarred, item.id)"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div
        v-if="!isLoading && filteredLists.length === 0 && searchQuery"
        class="text-center pa-8"
      >
        <v-icon color="grey-lighten-1" icon="mdi-magnify" size="64" />
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
      description="This action cannot be undone. All items in this list will be permanently deleted."
      :item-name="toDeleteList?.title"
      item-type="list"
      @confirm="confirmDelete"
    />

    <!-- Bulk Delete Confirmation Dialog -->
    <ConfirmDeleteDialog
      v-model="showBulkDeleteDialog"
      :confirm-text="selectedCount === 1 ? 'Delete List' : `Delete ${selectedCount} Lists`"
      :description="selectedCount === 1
        ? 'This action cannot be undone. All items in this list will be permanently deleted.'
        : `This action cannot be undone. All items in these ${selectedCount} lists will be permanently deleted.`"
      :item-type="selectedCount === 1 ? 'list' : 'lists'"
      :title="selectedCount === 1 ? 'Delete List' : `Delete ${selectedCount} Lists`"
      @confirm="confirmBulkDelete"
    />

    <!-- Share List Dialog -->
    <ShareListDialog
      v-model="showShareDialog"
      :list-id="listToShare?.id"
      :list-name="listToShare?.title"
      @share-list="handleShareList"
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
