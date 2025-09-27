<script setup lang="ts">
  import ListCard from "@/components/List/ListCard.vue";
  import { useRouter } from 'vue-router';
  import { ref, onMounted } from 'vue';

  const router = useRouter();

  // Real data from API
  const lists = ref([]);
  const isLoading = ref(true);

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

</script>

<template>
  <div class="pa-3">
    <v-container fluid>
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
          v-for="item in lists"
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
    </v-container>
  </div>
</template>
