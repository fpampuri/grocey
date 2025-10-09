<script setup lang="ts">
    import { useRoute, useRouter } from 'vue-router';
    import { computed, ref, onMounted } from 'vue';
    import ListItemCard from '@/components/List/ListItemCard.vue';
    import StandardButton from '@/components/StandardButton.vue';
    import AddItemDialog from '@/components/dialog/AddItemDialog.vue';

    // Define the expected List type
    interface List {
        id: string;
        title: string;
        icon: string;
        itemsCount: number;
        isFavorite: boolean;
        users: any[];
        createdBy: string;
        createdAt: string;
    }

    interface ListItem {
        id: string;
        listId: string;
        productId: string;
        title: string;
        productName: string;
        quantity: number;
        completed: boolean;
        category: string;
    }


    const route = useRoute();
    const router = useRouter();

    // Extract the list ID from the route parameters
    const listId = computed(() => route.params.id);
    
    // Store the fetched list data and items
    const listData = ref<List | null>(null);
    const listItems = ref<ListItem[]>([]);
    const isLoading = ref(true);
    const itemsLoading = ref(true);

    const showAddItemDialog = ref(false);

    // Fetch list data from local mock API
    async function fetchListData(id: string) {
        try {
            const response = await fetch('/api/lists.json');
            if (!response.ok) throw new Error('Lists not found');
            
            const lists = await response.json() as List[];
            const list = lists.find((list: List) => list.id == id);
            
            if (!list) {
                throw new Error('List not found');
            }
            
            return {
                id: list.id,
                title: list.title,
                icon: list.icon,
                users: list.users,
                createdBy: list.createdBy,
                createdAt: list.createdAt
            };
        } catch (error) {
            console.error('Error fetching list data:', error);
            return null;
        }
    }

    // Fetch list items for the given list ID
    async function fetchListItems(listID: string) {
        try {
            const response = await fetch('/api/list-items.json');
            if (!response.ok) throw new Error('Items of list not found');

            const items = await response.json() as ListItem[];

            // Filter items by listId and return the correct structure
            return items
                .filter(item => item.listId == listID)
                .map(item => ({
                    id: item.id,
                    title: item.productName, // Use productName as title for display
                    completed: item.completed,
                    quantity: item.quantity,
                    productId: item.productId,
                    productName: item.productName,
                    category: item.category
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

    // Navigate back to the lists overview
    function goBack() {
        router.push('/lists');
    }

    // Handle list item actions
    function handleUpdateQuantity(itemId: string, newQuantity: number) {
        const item = listItems.value.find(item => item.id === itemId);
        if (item) {
            item.quantity = newQuantity;
        }
    }

    function handleToggleComplete(itemId: string, completed: boolean) {
        const item = listItems.value.find(item => item.id === itemId);
        if (item) {
            item.completed = completed;
        }
    }

    function handleDeleteItem(itemId: string) {
        listItems.value = listItems.value.filter(item => item.id !== itemId);
    }

    function handleAddItem(itemData: { name: string }) {
        const newId = Date.now();
  
        // Create new item object
        const newItem = {
          id: newId,
          listId: parseInt(listId.value as string), 
          productId: newId, // Probaly we'll need to change this
          title: itemData.name,
          quantity: 1,
          completed: false,
          category: "Other", // Default category
        };
        // Add to the list
        listItems.value.push(newItem);
    }

    function openAddItemDialog() {
        showAddItemDialog.value = true;
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
        
        <v-col class="d-flex justify-end">
          <StandardButton 
            title="Add Item"
            icon="mdi-plus"
            @click="openAddItemDialog"
          />
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

    <AddItemDialog 
      v-model="showAddItemDialog"
      @add-item="handleAddItem"
    />
  </div>
</template>

<style scoped>
.add-item-btn {
  border-radius: 12px;
  text-transform: none;
  font-weight: 600;
}
</style>
