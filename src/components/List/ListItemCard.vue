<script setup lang="ts">
import { defineProps, defineEmits, ref, watch } from "vue";

const props = defineProps({
  title: { type: String, required: true },
  quantity: { type: Number, default: 1 },
  completed: { type: Boolean, default: false },
  id: { type: [String, Number], required: true }
});

const emits = defineEmits(["update-quantity", "toggle-complete", "delete"]);

const localQuantity = ref(props.quantity);
const localCompleted = ref(props.completed);

// Keep local state in sync with props
watch(() => props.quantity, (v) => (localQuantity.value = v));
watch(() => props.completed, (v) => (localCompleted.value = v));

function decreaseQuantity() {
  if (localQuantity.value > 1) {
    localQuantity.value--;
    emits("update-quantity", localQuantity.value);
  }
}

function increaseQuantity() {
  localQuantity.value++;
  emits("update-quantity", localQuantity.value);
}

function toggleComplete() {
  localCompleted.value = !localCompleted.value;
  emits("toggle-complete", localCompleted.value);
}

function deleteItem() {
  emits("delete", props.id);
}
</script>

<template>
  <v-card class="list-item-card" outlined>
    <v-row class="item-row" align="center" no-gutters>
      <!-- Item title -->
      <v-col>
        <div 
          class="item-title"
          :class="{ 'completed': localCompleted }"
        >
          {{ title }}
        </div>
      </v-col>

      <!-- Quantity controls -->
      <v-col cols="auto">
        <div class="quantity-controls">
          <!-- Decrease button -->
          <button 
            class="quantity-btn decrease-btn" 
            @click="decreaseQuantity"
            :disabled="localQuantity <= 1"
            aria-label="decrease quantity"
          >
            <v-icon icon="mdi-minus" size="small" />
          </button>

          <!-- Quantity display -->
          <span class="quantity-display">{{ localQuantity }}</span>

          <!-- Increase button -->
          <button 
            class="quantity-btn increase-btn" 
            @click="increaseQuantity"
            aria-label="increase quantity"
          >
            <v-icon icon="mdi-plus" size="small" />
          </button>

          <!-- Delete button -->
          <button 
            class="delete-btn" 
            @click="deleteItem"
            aria-label="delete item"
          >
            <v-icon icon="mdi-delete" size="small" />
          </button>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style scoped>
.list-item-card {
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-sizing: border-box;
  background: var(--v-surface, #fff);
  border: 1px solid var(--v-border-color, #e0e0e0);
  transition: box-shadow 0.2s ease;
}

.list-item-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.item-row {
  gap: 16px;
}

.item-title {
  font-weight: 500;
  font-size: 1rem;
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.item-title.completed {
  text-decoration: line-through;
  color: var(--text-secondary);
  opacity: 0.7;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  background: none;
  border: 1px solid var(--v-border-color, #e0e0e0);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: var(--text-primary);
}

.quantity-btn:hover:not(:disabled) {
  background-color: var(--v-theme-primary, #1976d2);
  color: white;
  border-color: var(--v-theme-primary, #1976d2);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-display {
  font-weight: 600;
  font-size: 1rem;
  min-width: 20px;
  text-align: center;
  color: var(--text-primary);
}

.delete-btn {
  background: none;
  border: 1px solid #ef5350;
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: #ef5350;
  margin-left: 4px;
}

.delete-btn:hover {
  background-color: #ef5350;
  color: white;
}
</style>
