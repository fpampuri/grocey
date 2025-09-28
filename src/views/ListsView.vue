<script setup lang="ts">
  import ListCard from "@/components/List/ListCard.vue";
  import StandardButton from "@/components/StandardButton.vue";
  import SearchBar from "@/components/SearchBar.vue";
  import CreateListDialog from "@/components/CreateListDialog.vue";
  import { useRouter } from 'vue-router';
  import { ref, onMounted, computed } from 'vue';


  const router = useRouter();

  // Real data from API
  const lists = ref([]);
  const isLoading = ref(true);
  const searchQuery = ref('');
  const sortBy = ref('Date');
  const showCreateDialog = ref(false);

  // Computed property for filtered lists
  const filteredLists = computed(() => {
    if (!searchQuery.value) return lists.value;
    return lists.value.filter(list => 
      list.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  });

  // Fetch lists from JSONPlaceholder
  async function fetchLists() {
    try {
      const response = await fetch('https://jsonplaceholder.typicode.com/users');
      const users = await response.json();
      
      // Transform users into list format
      return users.map(user => ({
        id: user.id,
        title: `${user.name}'s List`,
        icon: user.id % 2 === 0 ? "mdi-apple" : "mdi-broom", // Alternate icons
        itemsCount: Math.floor(Math.random() * 12) + 1, // We'll get real count later
        userName: user.name,
        userEmail: user.email
      }));
    } catch (error) {
      console.error('Error fetching lists:', error);
      return [];
    }
  }

  // Fetch real item counts for each list
  async function fetchItemCounts() {
    try {
      const todosResponse = await fetch('https://jsonplaceholder.typicode.com/todos');
      const todos = await todosResponse.json();
      
      // Count todos per user
      const countsByUserId = todos.reduce((acc, todo) => {
        acc[todo.userId] = (acc[todo.userId] || 0) + 1;
        return acc;
      }, {});

      // Update lists with real counts
      lists.value = lists.value.map(list => ({
        ...list,
        itemsCount: countsByUserId[list.id] || 0
      }));
    } catch (error) {
      console.error('Error fetching item counts:', error);
    }
  }

  onMounted(async () => {
    isLoading.value = true;
    lists.value = await fetchLists();
    await fetchItemCounts(); // Get real item counts
    isLoading.value = false;
  });

  function handleListClick(listItem: any) {
    console.log("✅ List clicked - Navigating to list:", listItem.id);
    router.push(`/lists/${listItem.id}`);
  }

  function handleStarToggle(isStarred: boolean) {
    console.log('⭐ Star toggled:', isStarred ? 'STARRED' : 'UNSTARRED');
  }

  function handleSelectionToggle(isSelected: boolean) {
    console.log('☑️ Selection toggled:', isSelected ? 'SELECTED' : 'UNSELECTED');
  }

  function handleEdit() {
    console.log('✏️ Edit clicked - Opening edit dialog...');
  }

  function handleDelete() {
    console.log('🗑️ Delete clicked - Confirming deletion...');
  }

  function handleShare() {
    console.log('📤 Share clicked - Opening share options...');
  }

  function handleAddList() {
    console.log('➕ Add List clicked - Opening create list dialog...');
    showCreateDialog.value = true;
  }

  function handleCreateList(listData: { name: string; icon: string }) {
    console.log('📝 Creating new list:', listData);
    
    // Create new list object
    const newList = {
      id: Date.now(), // Use timestamp as temporary ID
      title: listData.name,
      icon: listData.icon,
      itemsCount: 0,
      userName: 'Current User',
      userEmail: 'user@example.com'
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
          <SearchBar 
            v-model="searchQuery"
            placeholder="Search lists..."
          />
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
          <div class="d-flex align-center">
            <v-icon icon="mdi-star-outline" class="mr-2" />
            <span class="text-h6 font-weight-medium">Favorites</span>
          </div>
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
              style="min-width: 100px;"
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
          v-for="item in filteredLists"
          :key="item.id"
        >
          <ListCard
            :title="item.title"
            :icon="item.icon"
            :itemsCount="item.itemsCount"
            @click="() => handleListClick(item)"
            @toggle-star="handleStarToggle"
            @toggle-selection="handleSelectionToggle"
            @edit="handleEdit"
            @delete="handleDelete"
            @share="handleShare"
          />
        </v-col>
      </v-row>

      <!-- No results message -->
      <div v-if="!isLoading && filteredLists.length === 0 && searchQuery" class="text-center pa-8">
        <v-icon icon="mdi-magnify" size="64" color="grey-lighten-1" />
        <p class="mt-2 text-h6 text-medium-emphasis">No lists found</p>
        <p class="text-body-2 text-medium-emphasis">Try adjusting your search terms</p>
      </div>
    </v-container>

    <!-- Create List Dialog -->
    <CreateListDialog 
      v-model="showCreateDialog"
      @create-list="handleCreateList"
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
