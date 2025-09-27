<script setup lang="ts">
    import { useRoute, useRouter } from 'vue-router';
    import { computed, ref, onMounted } from 'vue';
    import ListItemCard from '@/components/List/ListItemCard.vue';

    const route = useRoute();
    const router = useRouter();

    // Extract the list ID from the route parameters
    const listId = computed(() => route.params.id);
    
    // Store the fetched list data and items
    const listData = ref(null);
    const listItems = ref([]);
    const isLoading = ref(true);
    const itemsLoading = ref(true);

    function goBack() {
        router.push('/lists');
    }

    // Fetch list (user) data from JSONPlaceholder
    async function fetchListData(id: string) {
        try {
            const response = await fetch(`https://jsonplaceholder.typicode.com/users/${id}`);
            if (!response.ok) throw new Error('List not found');
            
            const user = await response.json();
            return {
                id: user.id,
                title: `${user.name}`,
                icon: user.id % 2 === 0 ? "mdi-apple" : "mdi-broom",
                userName: user.name,
                userEmail: user.email,
                userWebsite: user.website,
                userCompany: user.company.name
            };
        } catch (error) {
            console.error('Error fetching list data:', error);
            return null;
        }
    }

    // Fetch list items (todos) for this user
    async function fetchListItems(userId: string) {
        try {
            const response = await fetch(`https://jsonplaceholder.typicode.com/todos?userId=${userId}`);
            const todos = await response.json();
            
            return todos.map(todo => ({
                id: todo.id,
                title: todo.title,
                completed: todo.completed,
                userId: todo.userId,
                quantity: Math.floor(Math.random() * 5) + 1 // Add random quantity for demo
            }));
        } catch (error) {
            console.error('Error fetching list items:', error);
            return [];
        }
    }

    // Fetch data when component mounts
    onMounted(async () => {
        isLoading.value = true;
        itemsLoading.value = true;

        // Fetch list data
        const list = await fetchListData(listId.value as string);
        listData.value = list;
        isLoading.value = false;

        // Fetch list items if list exists
        if (list) {
            const items = await fetchListItems(listId.value as string);
            listItems.value = items;
        }
        itemsLoading.value = false;
    });

    // Handle list item actions
    function handleUpdateQuantity(itemId: number, newQuantity: number) {
        const item = listItems.value.find(item => item.id === itemId);
        if (item) {
            item.quantity = newQuantity;
            console.log(`Updated item ${itemId} quantity to ${newQuantity}`);
        }
    }

    function handleToggleComplete(itemId: number, completed: boolean) {
        const item = listItems.value.find(item => item.id === itemId);
        if (item) {
            item.completed = completed;
        }
        console.log(`Toggled item ${itemId} completion to ${completed}`);
    }

    function handleDeleteItem(itemId: number) {
        listItems.value = listItems.value.filter(item => item.id !== itemId);
        console.log(`Deleted item ${itemId}`);
    }
</script>

<template>
  <div class="pa-4">
    <v-container fluid>
      <!-- Header with back button -->
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
          <div v-else-if="listData">
            <h1 class="text-h4">{{ listData.title }}</h1>
          </div>
          <div v-else>
            <h1 class="text-h4">List Not Found</h1>
          </div>
        </v-col>
        <v-col cols="auto" v-if="listData">
          <v-btn icon="mdi-star-outline" variant="text" size="large" />
          <v-btn icon="mdi-share-variant" variant="text" size="large" />
        </v-col>
      </v-row>

      <!-- Add Item Button -->
      <v-row class="mb-4" v-if="listData">
        <v-col cols="12">
          <v-btn 
            color="success"
            prepend-icon="mdi-plus"
            size="large"
            variant="flat"
            class="add-item-btn"
          >
            Add Item
          </v-btn>
        </v-col>
      </v-row>

      <!-- List Items -->
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
            <!-- Loading items -->
            <div v-if="itemsLoading" class="text-center pa-4">
              <v-progress-circular indeterminate size="32" />
              <p class="mt-2">Loading items...</p>
            </div>

            <!-- List items using ListItemCard -->
            <div v-else-if="listItems.length > 0">
              <ListItemCard
                v-for="item in listItems"
                :key="item.id"
                :id="item.id"
                :title="item.title"
                :quantity="item.quantity"
                :completed="item.completed"
                @update-quantity="(quantity) => handleUpdateQuantity(item.id, quantity)"
                @toggle-complete="(completed) => handleToggleComplete(item.id, completed)"
                @delete="(id) => handleDeleteItem(id)"
              />
            </div>

            <!-- No items -->
            <div v-else class="text-center pa-8">
              <v-icon icon="mdi-inbox" size="64" color="grey-lighten-1" />
              <p class="mt-2 text-h6 text-medium-emphasis">This list is empty</p>
              <p class="text-body-2 text-medium-emphasis">Add your first item to get started</p>
            </div>
          </div>
          <div v-else>
            <v-card class="pa-4">
              <v-card-title>List Not Found</v-card-title>
              <v-card-text>
                <p>No list found with ID: <strong>{{ listId }}</strong></p>
                <v-btn @click="goBack" color="primary" class="mt-2">
                  Back to Lists
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.add-item-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
}
</style>
